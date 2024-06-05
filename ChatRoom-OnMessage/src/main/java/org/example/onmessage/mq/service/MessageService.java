package org.example.onmessage.mq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.constants.ThreadPoolConstant;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.publish.PublishEventUtils;
import org.example.onmessage.service.AckService;
import org.example.onmessage.service.MQService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageSendBo;
import org.example.pojo.bo.MessageBO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageService {
    private final RabbitTemplate rabbitTemplate;
    private final RedisCacheService redisCacheService;
    private final MsgWriter msgWriter;
    private final AckService ackService;
    private final PublishEventUtils publishEventUtils;
    private final MQService mqService;
    @Resource(name = ThreadPoolConstant.MESSAGE_SAVE_THREAD_POOL_NAME)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void push2mq(MessageBO messageBO) {
        if (Objects.isNull(messageBO)) {
            return;
        }
        if (AbstractMessage.MessageType.GROUP.getCode().equals(messageBO.getMessageType())) {
            pushGroupMessage2Mq(messageBO);
        }else {
            pushSingleMessage2Mq(messageBO);
        }
//        Message message = MessageBuilder
//                .withBody(JSON.toJSONString(messageBO).getBytes(StandardCharsets.UTF_8))
//                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
//                .build();
//
//
//        List<String> routingKeys = new ArrayList<>();
//
//        if (AbstractMessage.MessageType.GROUP.getCode().equals(messageBO.getMessageType())) {
//            // todo：如果是群聊，rk应该是数组，包含在线的全部成员
//            String hashValue = redisCacheService.getHashValue(RedisCacheConstants.ONLINE + messageBO.getFromUserId(), messageBO.getDevice().toString(), String.class);
//            routingKeys.add(hashValue);
//
//
//        } else if (messageBO.getIsAck()) {
//            String hashValue = redisCacheService.getHashValue(RedisCacheConstants.ONLINE + messageBO.getFromUserId(), messageBO.getDevice().toString(), String.class);
//            routingKeys.add(hashValue);
//        } else {
//            List<String> hashAllValues = redisCacheService.getHashAllValues(RedisCacheConstants.ONLINE + messageBO.getTargetId(), String.class);
//            routingKeys.addAll(hashAllValues);
//        }
//        for (String key : routingKeys) {
//            push2mq(message, EXCHANGE, key);
//        }



//        if(routingKeys.isEmpty()){
//            sendToDefaultQueue(message);
//        }else {
//            for (String key : routingKeys) {
//                push2mq(message, key);
//            }
//        }

//        if (!StringUtils.hasText(routingKey)) {
//            sendToDefaultQueue(message);
//        } else {
//            push2mq(message, routingKey);
//        }
    }

    private void pushSingleMessage2Mq(MessageBO messageBO) {
        Set<Long> offlineUsers = new HashSet<>();
        Map<String, Set<String>> ipUserIdMap = new HashMap<>();

        Set<String> userIds;
        // 如果之前ack过直接获取未ack的用户
        if (redisCacheService.hasKey(RedisConstant.BUSINESS_ACK + messageBO.getId())) {
            dealUnAckedMessage(messageBO, offlineUsers, ipUserIdMap);
        }else {

            userIds = new HashSet<>();

            // 发送目标设备和ip的map
            Map<String, String> receiveUserDeviceIpMap = redisCacheService.getHashMap(RedisCacheConstants.ONLINE + messageBO.getTargetId(), String.class);
            // 如果发送对象不在线

            if (CollectionUtils.isEmpty(receiveUserDeviceIpMap)) {
                // 如果是空，代表不在线，离线保存
                offlineUsers.add(messageBO.getTargetId());
            }
            // 发送给发送者其他设备
            Map<String, String> fromUserDeviceIpMap = redisCacheService.getHashMap(RedisCacheConstants.ONLINE + messageBO.getFromUserId(), String.class);
            fromUserDeviceIpMap.putAll(receiveUserDeviceIpMap);


            // 封装成ip和userId的map
            fromUserDeviceIpMap.forEach((device, ip) ->{
                Set<String> userSet = ipUserIdMap.computeIfAbsent(ip, k -> new HashSet<>());
                userSet.add(messageBO.getTargetId().toString() + ":" + device);
            });

            // 添加userId
            ipUserIdMap.values().forEach(userIds::addAll);

            ackService.setBusinessAck(messageBO, userIds);
        }
        // 发送到mq
        ipUserIdMap.forEach((key, value) ->{
            MessageSendBo messageSendBo = new MessageSendBo(messageBO, value);

            mqService.push2mq(messageSendBo, RabbitMQConstant.MQ_GROUP_EXCHANGE, key);
        });
        // 保存offline消息
        if (!offlineUsers.isEmpty()){
            CompletableFuture.runAsync(() -> saveOfflineMessage(messageBO, offlineUsers, ipUserIdMap.isEmpty()), threadPoolTaskExecutor);
        }


    }

    private void dealUnAckedMessage(MessageBO messageBO, Set<Long> offlineUsers, Map<String, Set<String>> ipUserIdMap) {
        Set<String> userIds;
        userIds = ackService.getUnAcked(messageBO);
        userIds.forEach(user -> {
            String userIdString = user.substring(0, user.indexOf(":"));
            String deviceString = user.substring(user.indexOf(":") + 1);
            String ip = redisCacheService.getHashValue(RedisCacheConstants.ONLINE + userIdString, deviceString, String.class);
            if (StringUtils.hasText(ip)) {
                Set<String> userSet = ipUserIdMap.computeIfAbsent(ip, k -> new HashSet<>());
                userSet.add(user);
            }else {
                offlineUsers.add(Long.parseLong(user.substring(0, user.indexOf(":"))));
            }
        });
    }

    private void saveOfflineMessage(MessageBO messageBO, Set<Long> userIds, boolean flag){
        msgWriter.saveDurably(messageBO, userIds);
        if (flag){
            publishEventUtils.pushAckToUser(this, messageBO, messageBO.getFromUserId());
        }
    }
    public void pushGroupMessage2Mq(MessageBO message) {
        // messageId-> {ip-》userId}\

        Set<Long> offlineUsers = new HashSet<>();
        Set<String> memberIds ;
        Map<String, Set<String>> ipUserIdMap = new HashMap<>();

        // 如果之前ack过直接获取未ack的用户
        if (redisCacheService.hasKey(RedisConstant.BUSINESS_ACK + message.getId())) {
            dealUnAckedMessage(message, offlineUsers, ipUserIdMap);
        }else {
            // 没有ack过，获取所有成员
            Set<Long> userIds = redisCacheService.gAllSet(RedisCacheConstants.ROOM_MEMBER + message.getTargetId(), Long.class);

            memberIds = new HashSet<>();
            // 封装成ip和userId的map
            userIds.forEach(id -> {
                Map<String, String> hashMap = redisCacheService.getHashMap(RedisCacheConstants.ONLINE + id, String.class);
                if (hashMap.isEmpty()) {
                    offlineUsers.add(id);
                }else {
                    hashMap.forEach((device, ip) -> {
                        Set<String> userSet = ipUserIdMap.computeIfAbsent(ip, k -> new HashSet<>());
                        String user = id.toString() + ":" + device;
                        userSet.add(user);
                        memberIds.add(user);
                    });
                }
            });
        }

        ipUserIdMap.forEach((key, value) ->{
            MessageSendBo messageSendBo = new MessageSendBo(message, value);

            mqService.push2mq(messageSendBo, RabbitMQConstant.MQ_GROUP_EXCHANGE, key);
        });
        // 保存offline消息
        if (!CollectionUtils.isEmpty(offlineUsers)){
            CompletableFuture.runAsync(() -> saveOfflineMessage(message, offlineUsers, ipUserIdMap.isEmpty()), threadPoolTaskExecutor);
        }

//        redisCacheService.putHashKey(RedisCacheConstants.GROUP_MESSAGE_ACK_KEY + message.getId(), "offline", JSON.toJSONString(offlineUsers));

    }



    private void sendToDefaultQueue(Message message) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(
                result -> {
                    if (!result.isAck()) {
                        log.warn("消息发送失败，队列{} ,消息{}", RabbitMQConstant.DEFAULT_QUEUE, message);
                        rabbitTemplate.convertAndSend(RabbitMQConstant.DEFAULT_QUEUE, message, correlationData);
                    }
                },
                ex -> {
                    log.warn("消息发送失败，队列{} ,消息{}", RabbitMQConstant.DEFAULT_QUEUE, message);
                }
        );
        rabbitTemplate.convertAndSend(RabbitMQConstant.DEFAULT_QUEUE, message, correlationData);
    }
}
