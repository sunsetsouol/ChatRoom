package org.example.onmessage.route;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.IdStrategy.IdGen.IdGenType;
import org.example.IdStrategy.IdGen.IdGenerator;
import org.example.IdStrategy.IdGen.IdGeneratorStrategyFactory;
import org.example.constant.GlobalConstants;
import org.example.onmessage.adapter.MessageAdapter;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.dao.MsgWriter;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.publish.PublishEventUtils;
import org.example.onmessage.push.PushWorker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class MessageBuffer {
    private static final Map<Long, Long[]> CLIENT_MAX_ID_MAP = new ConcurrentHashMap<>();
    private static final Long BUFFER_SIZE = 50L;
    private final RedissonClient redissonClient;
    private final MsgReader msgReader;
    private final MsgWriter msgWriter;
    private final PushWorker pushWorker;
    private final PublishEventUtils publishEventUtils;
    private final IdGeneratorStrategyFactory idGeneratorStrategyFactory;
//    private final IdGenerator idGenerator = idGeneratorStrategyFactory.getIdGeneratorStrategy(IdGenType.SNOWFLAKE.type);


    public void handleMsg(WsMessageDTO wsMessageDTO) {

        // 发送者id
        Long fromUserId = wsMessageDTO.getFromUserId();

        Integer device = wsMessageDTO.getDevice();

        RLock lock = redissonClient.getLock(RedisConstant.BUFFER_PREFIX + fromUserId);

        try {
            if (!lock.tryLock(RedisConstant.LOCK_TIME, RedisConstant.WAIT_TIME, TimeUnit.SECONDS)) {
                log.warn("消息发送太频繁，直接舍弃，等客户端重发");
                return;
            }
        } catch (InterruptedException e) {
            log.warn("获取锁失败", e);
            return;
        }

        try {
            Long curMaxId = getMaxClientId(fromUserId, device);

            Long clientMessageId = wsMessageDTO.getClientMessageId();

            // 防止消息乱发，clientId一直不对
            if (clientMessageId - curMaxId > BUFFER_SIZE) {
                // 超出缓存区直接抛弃，等客户端重发
                log.warn("消息超过缓存区，直接舍弃，等客户端重发");
            } else {
                // 消息没有超出缓存区，加入缓存，set自动去重
                msgWriter.saveTem(wsMessageDTO);


                if (clientMessageId == curMaxId + 1) {
                    // 消息有序到达，直接发送当前消息，并且发送缓存区有序的消息
                    log.info("{} 消息 {} 有序到达，直接发送当前消息，并且发送缓存区有序的消息", AbstractMessage.DeviceType.getType(device), clientMessageId);
                    // 更新当前用户设备最大id
                    List<MessageBO> messageList = getMessage(device, fromUserId, clientMessageId);
                    // 有序到达直接推给用户并且保存到持久化
//                    pushWorker.push(messageList);

                    // TODO：异步防止阻塞
                    messageList.forEach(pushWorker::push);
                    MessageBO lastMessage = messageList.get(messageList.size() - 1);
//                    msgWriter.updateMaxClientId(fromUserId, device, lastMessage.getClientMessageId());
                    CLIENT_MAX_ID_MAP.get(fromUserId)[device] = lastMessage.getClientMessageId();
                } else if (clientMessageId <= curMaxId) {
                    // 重复消息，直接重发
                    log.info("{} 消息 {} 重复到达，直接重发", AbstractMessage.DeviceType.getType(device), clientMessageId);

                    // 找到消息重发
                    MessageBO message = getMessageByClientId(wsMessageDTO);
                    if (Objects.nonNull(message)) {
                        pushWorker.push(message);
                    } else {
                        // 如果找不到消息说明已经ack了
                        publishEventUtils.pushMessageAck(this, wsMessageDTO);
                    }

                }
                // 剩下无序到达情况上面保存后就可以了

            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("消息处理失败", e);

        } finally {
            lock.unlock();
        }

    }

    public MessageBO getMessageByClientId(WsMessageDTO wsMessageDTO) {
//        // 为什么不去tem根据score找呢？因为没有全局唯一id
//        List<MessageBO> messageBOList = msgReader.getWindowsMsg(RedisConstant.MESSAGE + wsMessageDTO.getFromUserId(), 0, Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
//        return messageBOList.stream().filter(messageBO -> wsMessageDTO.getClientMessageId().equals(messageBO.getClientMessageId())).findFirst().orElse(null);
        return msgReader.getMessageByClientId(wsMessageDTO);

    }

    private List<MessageBO> getMessage(Integer device, Long fromUserId, Long clientId) {

        IdGenerator idGeneratorStrategy = idGeneratorStrategyFactory.getIdGeneratorStrategy(IdGenType.SNOWFLAKE.type);

        // 找到缓存区该用户该设备的消息并转成list
//        Set<ZSetOperations.TypedTuple<Object>> windowsMsg = msgReader.getWindowsMsg(RedisConstant.TEM_MESSAGE + fromUserId + ":" + device, clientId, Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
//        List<WsMessageDTO> collect = windowsMsg.stream().map(zSetOperation -> (WsMessageDTO) zSetOperation.getValue()).collect(Collectors.toList());

        List<WsMessageDTO> collect = msgReader.getWindowsMsg(RedisConstant.TEM_MESSAGE + fromUserId + ":" + device, clientId, Long.MAX_VALUE, 0L, BUFFER_SIZE, WsMessageDTO.class);

        List<MessageBO> result = new ArrayList<>();
        int subIndex = 1;


        MessageBO first = BeanUtil.copyProperties(collect.get(0), MessageBO.class);
        first.setId(idGeneratorStrategy.getLongId());
        result.add(first);

        // 找到连续的消息
        Long pre = collect.get(0).getClientMessageId();
        Long cur;
        for (int i = 1; i < collect.size(); i++) {
            cur = collect.get(i).getClientMessageId();
            if (cur - pre != 1) {
                break;
            }
            pre = cur;
            MessageBO messageBO = BeanUtil.copyProperties(collect.get(i), MessageBO.class);
            Long messageId = idGeneratorStrategy.getLongId();
            messageBO.setId(messageId);
            subIndex = i + 1;
            result.add(messageBO);
        }
        List<MessageBO> messageBOS = result.subList(0, subIndex);
        // 保存到持久化
        msgWriter.saveDurably(messageBOS);
        return messageBOS;
    }

    public Long getMaxClientId(Long fromUserId, Integer device) {
//        return msgReader.getMaxClientId(fromUserId, device);
        Long[] clientIds = CLIENT_MAX_ID_MAP.get(fromUserId);

        Long currentMaxId = 0L;

        // 如果clientIds是null，可能是第一次获取，也可能是刚刚分配到当前机器
        if (Objects.isNull(clientIds)) {
            // 先从redis尝试获取
            Long clientId = msgReader.getMaxClientId(fromUserId, device);
            clientIds = new Long[]{null, null};
            clientIds[device] = clientId;
            CLIENT_MAX_ID_MAP.put(fromUserId, clientIds);
            currentMaxId = clientId;

        } else if (Objects.isNull(clientIds[device])) {
            Long clientId = msgReader.getMaxClientId(fromUserId, device);
            clientIds[device] = clientId;
        } else {
            currentMaxId = clientIds[device];
        }
        return currentMaxId;
    }

    public void getUnreadMessage(WsMessageDTO wsMessageDTO) {
        // TODO： 不应该是clientId查找
        List<Long> needToUpdate = msgReader.getWindowsMsg(RedisConstant.INBOX + wsMessageDTO.getFromUserId(), wsMessageDTO.getClientMessageId(), Long.MAX_VALUE, 0L, GlobalConstants.MAX_FRIEND, Long.class);
        List<MessageBO> result = new ArrayList<>();
        for (Long userId : needToUpdate) {
            String key = RedisConstant.SINGLE_CHAT +
                    (wsMessageDTO.getFromUserId() > userId
                            ? userId + ":" + wsMessageDTO.getFromUserId()
                            : wsMessageDTO.getFromUserId() + ":" + userId);
            List<MessageBO> messageBOS = msgReader.getWindowsMsg(key, wsMessageDTO.getClientMessageId(), Long.MAX_VALUE, 0L, BUFFER_SIZE, MessageBO.class);
            result.addAll(messageBOS);
        }
        if (!result.isEmpty()) {
            result.forEach(message -> {
                GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), JSON.toJSONString(message));
            });
        }
        GlobalWsMap.sendText(wsMessageDTO.getFromUserId(), JSON.toJSONString(MessageAdapter.getUnreadAckMessage(wsMessageDTO)));
    }
}
