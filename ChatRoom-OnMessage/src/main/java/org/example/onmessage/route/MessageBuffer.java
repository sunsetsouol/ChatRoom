package org.example.onmessage.route;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.IdStrategy.IdGen.IdGenType;
import org.example.IdStrategy.IdGen.IdGenerator;
import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.exception.BusinessException;
import org.example.onmessage.adapter.MessageAdapter;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.dao.MsgWriter;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.entity.dto.WsMessageDTO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.mq.service.MQService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.vo.ResultStatusEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageBuffer {
    private static final Map<Long, Long[]> CLIENT_MAX_ID_MAP = new ConcurrentHashMap<>();
    private static final Long BUFFER_SIZE = 50L;
    private final RedissonClient redissonClient;
    private final MsgReader msgReader;
    private final MsgWriter msgWriter;
    private final MQService mqService;
    private final RedisCacheService redisCacheService;
    private final IdGeneratorStrategyFactory idGeneratorStrategyFactory;
//    private final IdGenerator idGenerator = idGeneratorStrategyFactory.getIdGeneratorStrategy(IdGenType.SNOWFLAKE.type);


    public void handleMsg(WsMessageDTO wsMessageDTO) {
        // 发送者id
        Long fromUserId = wsMessageDTO.getFromUserId();

        Integer device = wsMessageDTO.getDevice();

        RLock lock = redissonClient.getLock(RedisConstant.BUFFER_PREFIX + device + ":" + fromUserId);

        try {
            if (!lock.tryLock(RedisConstant.LOCK_TIME, RedisConstant.WAIT_TIME, TimeUnit.SECONDS)) {
                log.warn("消息发送太频繁，直接舍弃，等客户端重发");
                throw new BusinessException(ResultStatusEnum.FREQUENT_SEND);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Long curMaxId = getMaxClientId(fromUserId, device);

            Long clientMessageId = wsMessageDTO.getClientMessageId();

            if (clientMessageId - curMaxId > BUFFER_SIZE) {
                // 超出缓存区直接抛弃，等客户端重发
                log.warn("消息超过缓存区，直接舍弃，等客户端重发");
            } else {
                // 消息没有超出缓存区，加入缓存
                msgWriter.saveTem(wsMessageDTO);
//                MessageBO messageBO = BeanUtil.copyProperties(wsMessageDTO, MessageBO.class);
//                messageBO.setId(idGenerator.getLongId());
//                msgWriter.saveTem(wsMessageDTO);
//                mqService.push2mq(JSON.toJSONString(wsMessageDTO), RabbitMQConstant.WS_EXCHANGE, "ws.a");
                if (clientMessageId == curMaxId + 1) {
                    // 消息有序到达，直接发送当前消息，并且发送缓存区有序的消息
                    log.info("{} 消息 {} 有序到达，直接发送当前消息，并且发送缓存区有序的消息", AbstractMessage.DeviceType.getType(device), clientMessageId);
                    // 更新当前用户设备最大id
                    List<MessageBO> messageList = getMessage(device, fromUserId, clientMessageId, wsMessageDTO);
                    CLIENT_MAX_ID_MAP.get(fromUserId)[device] = messageList.get(messageList.size() - 1).getClientMessageId();
                    // 推到mq
//                    mqService.push2mq(JSON.toJSONString(messageList), RabbitMQConstant.WS_EXCHANGE, "ws.a");
                    messageList.forEach(mqService::push2mq);
                    // 持久化存储
                    msgWriter.saveDurably(messageList);
                } else if (clientMessageId <= curMaxId) {
                    // 重复消息，直接重发
                    log.info("{} 消息 {} 重复到达，直接重发", AbstractMessage.DeviceType.getType(device), clientMessageId);

                    MessageBO message = getMessageByClientId(wsMessageDTO);
                    if (Objects.nonNull(message)) {
                        mqService.push2mq(message);
                    }
                    wsMessageDTO.setClientMessageId(curMaxId);
                    // 设置ack
//                    redisCacheService.setCacheObject(RedisConstant.ACK + fromUserId + ":" + message.getId(), "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
                }else {
                    // 剩下的情况就是无序到达，上面直接缓存了ack就行了
                    wsMessageDTO.setClientMessageId(curMaxId);
                }


                // ack
//                ClientMessageAck ack = ClientMessageAck
//                        .builder()
//                        .clientMessageId(clientMessageId)
//                        .device(device)
//                        .ackType(ClientMessageAck.AckType.FIRST_ACK.getCode())
//                        .isAck(true)
//                        .build();
//                GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), JSON.toJSONString(ack));
                WsMessageDTO ack1Message = MessageAdapter.getAck1Message(wsMessageDTO);
                GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), JSON.toJSONString(ack1Message));
            }

        }catch (Exception e) {
            e.printStackTrace();
            log.error("消息处理失败", e);

        } finally {
            lock.unlock();
        }

    }

    public MessageBO getMessageByClientId(WsMessageDTO wsMessageDTO){
        // 为什么不去tem根据score找呢？因为没有全局唯一id
        List<MessageBO> messageBOList = msgReader.getWindowsMsg(RedisConstant.MESSAGE + wsMessageDTO.getFromUserId(), 0, Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
        return messageBOList.stream().filter(messageBO -> wsMessageDTO.getClientMessageId().equals(messageBO.getClientMessageId())).findFirst().orElse(null);

    }

    private List<MessageBO> getMessage(Integer device, Long fromUserId, Long clientId, WsMessageDTO wsMessageDTO) {

        IdGenerator idGeneratorStrategy = idGeneratorStrategyFactory.getIdGeneratorStrategy(IdGenType.SNOWFLAKE.type);

        // 找到缓存区该用户该设备的消息并转成list
//        Set<ZSetOperations.TypedTuple<Object>> windowsMsg = msgReader.getWindowsMsg(RedisConstant.TEM_MESSAGE + fromUserId + ":" + device, clientId, Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
//        List<WsMessageDTO> collect = windowsMsg.stream().map(zSetOperation -> (WsMessageDTO) zSetOperation.getValue()).collect(Collectors.toList());

        List<WsMessageDTO> collect = msgReader.getWindowsMsg(RedisConstant.TEM_MESSAGE + fromUserId + ":" + device, clientId, Long.MAX_VALUE, 0L, BUFFER_SIZE, WsMessageDTO.class);

        List<MessageBO> result = new ArrayList<>();
        int subIndex = 1;

        collect.add(0, wsMessageDTO);
        MessageBO message = BeanUtil.copyProperties(wsMessageDTO, MessageBO.class);
        message.setId(idGeneratorStrategy.getLongId());
        result.add(message);

        // 找到连续的消息
        Long pre = wsMessageDTO.getClientMessageId();
        Long cur;
        for (int i = 1; i < collect.size(); i++) {
            cur = collect.get(i).getClientMessageId();
            if (cur - pre != 1) {
                break;
            }
            pre = cur;
            MessageBO messageBO = BeanUtil.copyProperties(collect.get(i), MessageBO.class);
            messageBO.setId(idGeneratorStrategy.getLongId());
            subIndex = i+1;
        }

        return result.subList(0, subIndex);
    }

    private Long getMaxClientId(Long fromUserId, Integer device) {
        Long[] clientIds = CLIENT_MAX_ID_MAP.get(fromUserId);

        Long currentMaxId = 0L;

        // 如果clientIds是null，可能是第一次获取，也可能是刚刚分配到当前机器
        if (Objects.isNull(clientIds)) {
            // 先从redis尝试获取

            List<MessageBO> msgList = msgReader.getMsg(RedisConstant.MESSAGE + fromUserId, BUFFER_SIZE, MessageBO.class);
            LongStream longStream = msgList.stream().filter(msg -> device.equals( msg.getDevice())).mapToLong(WsMessageDTO::getClientMessageId);
            currentMaxId = longStream.max().orElse(0L);

            Long[] deviceClientIds = {0L, 0L};
            deviceClientIds[device] = currentMaxId;
            CLIENT_MAX_ID_MAP.put(fromUserId, deviceClientIds);

            // 如果不为空，更新map

//            Set<ZSetOperations.TypedTuple<Object>> zset =
//                    msgReader.getWindowsMsg(RedisConstant.MESSAGE + fromUserId, 0L, Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
//
//            List<ZSetOperations.TypedTuple<Object>> collect = zset.stream().filter(zSetOperation -> device.equals(((MessageBO) zSetOperation.getValue()).getDevice())).collect(Collectors.toList());
//            if (collect.isEmpty()) {
//                // 如果是空，应该是第一次发送
//                CLIENT_MAX_ID_MAP.put(fromUserId, new Long[]{0L, 0L});
//                return currentMaxId;
//            }
//            MessageBO value = (MessageBO) collect.get(collect.size() - 1).getValue();
//            return value.getClientMessageId();


//            if (zset.isEmpty()){
//                // 如果是空，应该是第一次发送
//                CLIENT_MAX_ID_MAP.put(fromUserId, new Long[]{0L, 0L});
//                return new MessageHistoryBO(currentMaxId, msgList);
//            }
//            // 如果不为空，找到当前设别的缓存
//            msgList.addAll(zset);

//            long asLong = msgList.stream().filter(zSetOperation -> device.equals(((MessageBO) zSetOperation.getValue()).getDevice())).mapToLong(zSetOperation -> ((MessageBO) zSetOperation.getValue()).getClientMessageId()).max().getAsLong();

//            Set<ZSetOperations.TypedTuple<Object>> zset =
//                    msgReader.getWindowsMsg(RedisConstant.TEM_MESSAGE + device + ":" + fromUserId, Long.MAX_VALUE, 0L, BUFFER_SIZE, WsMessageDTO.class);
//            // 如果还是空，应该是第一次发送
//            if (zset.isEmpty()){
//                // 初始化map
//                CLIENT_MAX_ID_MAP.put(fromUserId, new Long[]{0L, 0L});
//                return new MessageHistoryBO(currentMaxId, msgList);
//            }
//            // 如果不是空，找到最大的连续的clientId
//            msgList.addAll(zset);
//
//            double pre = msgList.get(0).getScore();
//            int start = 1;
//            // 如果消息比缓冲区数量小，那么有可能是第一次发送，这时候要从0开始
//            if (msgList.size() < BUFFER_SIZE){
//                start = 0;
//                pre = 0;
//            }
//
//            double cur ;
//
//            for (int i = start; i < msgList.size(); i++) {
//                cur = msgList.get(i).getScore();
//                if ((cur - pre != 1)){
//                    break;
//                }
//                pre = cur;
//            }
//            currentMaxId = (long) pre;
//            Long[] deviceClientIds = {0L, 0L};
//            deviceClientIds[device] = currentMaxId;
//            CLIENT_MAX_ID_MAP.put(fromUserId, deviceClientIds);

        } else {
            currentMaxId = clientIds[device];
        }
        return currentMaxId;
    }
}
