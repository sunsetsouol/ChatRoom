package org.example.onmessage.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.entity.dto.WsMessageDTO;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.vo.ResultStatusEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Component
@RequiredArgsConstructor
public class MsgWriter {
    private final RedisCacheService redisCacheService;
    private final MsgReader msgReader;

    public void saveTem(WsMessageDTO wsMessageDTO) {
        String key = RedisConstant.TEM_MESSAGE + wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getDevice();
        redisCacheService.addZSet(key, wsMessageDTO, wsMessageDTO.getClientMessageId());
//        redisCacheService.setCacheObject(RedisConstant.ACK + wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getDevice(), "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public void saveDurably(List<MessageBO> messageBOS) {
        Long fromUserId = messageBOS.stream().findAny().orElseThrow(() -> new BusinessException(ResultStatusEnum.FROM_USER_ID_EMPTY)).getFromUserId();
        Integer device = messageBOS.stream().findAny().orElseThrow(() -> new BusinessException(ResultStatusEnum.DEVICE_EMPTY)).getDevice();
        // 存到持久化消息
        String key = RedisConstant.MESSAGE +  fromUserId;
        messageBOS.forEach(messageBO -> {
            redisCacheService.addZSet(key, messageBO, messageBO.getId());
            // 设置ack
            redisCacheService.setCacheObject(RedisConstant.ACK + fromUserId + ":" + messageBO.getId(), "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
        });
        // 删除暂存信息
        String deleteKey = RedisConstant.TEM_MESSAGE + fromUserId + ":" + device;
        redisCacheService.removeZSetByScore(deleteKey, messageBOS.get(0).getClientMessageId(), messageBOS.get(messageBOS.size() -1).getClientMessageId());

    }

    public void saveSingleChatMsg(MessageBO messageBO) {
        Long fromUserId = messageBO.getFromUserId();
        Long toUserId = messageBO.getTargetId();
        //小的在前面，大的在后面
        String key = RedisConstant.SINGLE_CHAT +
                (fromUserId > toUserId
                ? toUserId + ":" + fromUserId
                : fromUserId + ":" + toUserId);
        //单聊的消息进行存储。
        //TODO;异步存储，避免阻塞主线程，调用代理层，通过队列写数据库
        if (msgReader.hasMsg(key, messageBO.getId(), MessageBO.class)) {
            return;
        }
        // 保存消息缓存
        redisCacheService.addZSet(key, messageBO, messageBO.getId());
    }

//    public void saveMessageIdMap(Long fromUserId, Integer device, Map<String, String> messageIdMap) {
//        redisCacheService.addZSet(RedisConstant.CLIENT_ID_MAP + fromUserId + ":" + device, messageIdMap);
//    }

    public void saveMessageIdMap(Long fromUserId, Integer device, Long clientMessageId, Long messageId) {
        redisCacheService.addZSet(RedisConstant.CLIENT_ID_MAP + fromUserId + ":" + device, messageId, clientMessageId);
    }

    public void saveToInboxMsg(MessageBO message) {
        // 保存到收件箱
        redisCacheService.addZSet(RedisConstant.INBOX + message.getFromUserId(), message.getTargetId(), message.getId());
        redisCacheService.addZSet(RedisConstant.INBOX + message.getTargetId(), message.getFromUserId(), message.getId());
    }

//    public void updateMaxClientId(Long fromUserId, Integer device, Long clientMessageId) {
//        Long[] cacheObject = redisCacheService.getCacheObject(RedisConstant.CLIENT_ID + fromUserId, Long[].class);
//        if (cacheObject == null) {
//            cacheObject = new Long[]{0L, 0L};
//        }
//        cacheObject[device] = clientMessageId;
//        redisCacheService.setCacheObject(RedisConstant.CLIENT_ID + fromUserId, cacheObject);
//    }
}
