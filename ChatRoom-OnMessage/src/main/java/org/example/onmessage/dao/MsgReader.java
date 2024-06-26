package org.example.onmessage.dao;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RedisConstant;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/10
 */
@Component
@RequiredArgsConstructor
public class MsgReader {
    private final RedisCacheService redisCacheService;



    public <T> List<T> getWindowsMsg(String key, long min, long max, long offset, long bufferSize, Class<T> tclass) {
        Set<String> zget = redisCacheService.zget(key, min, max, offset, bufferSize, tclass);
        return zget.stream().map(message -> JSON.parseObject(message, tclass)).collect(Collectors.toList());
    }

    public<T> List<T> getMsg(String key, Long bufferSize, Class<T> tclass) {
        Set<String> zget = redisCacheService.zget(key, 0, Long.MAX_VALUE, 0, bufferSize, tclass);
        return zget.stream().map(message -> JSON.parseObject(message, tclass)).collect(Collectors.toList());
    }


    public <T> boolean hasMsg(String key, Long globalMessageId, Class<T> tClass) {
        return Objects.nonNull(redisCacheService.zget(key,  globalMessageId, tClass));
    }

    public MessageBO getMessageByClientId(WsMessageDTO wsMessageDTO) {
        String globalIdString = redisCacheService.getHashValue(RedisConstant.CLIENT_ID_MAP + wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getDevice(), wsMessageDTO.getClientMessageId().toString(), String.class);
        if (!StringUtils.hasText(globalIdString)) {
            return null;
        }
        Long globalMessageId = Long.valueOf(globalIdString);
        MessageBO message = redisCacheService.getZSetByScore(RedisConstant.INBOX + wsMessageDTO.getFromUserId(), globalMessageId, MessageBO.class);
        if (Objects.isNull(message)){
            message = BeanUtil.copyProperties(wsMessageDTO, MessageBO.class);
            message.setId(globalMessageId);
        }
        return message;
//        List<MessageBO> lastZSetScore = redisCacheService.getLastZSet(RedisConstant.INBOX + wsMessageDTO.getFromUserId(), 50, MessageBO.class);
//        return lastZSetScore.stream().filter(messageBO -> messageBO.getClientMessageId().equals(wsMessageDTO.getClientMessageId()) && messageBO.getDevice().equals(wsMessageDTO.getDevice())).findFirst().orElse(null);

//        Long globalMessageId = redisCacheService.zget(RedisConstant.CLIENT_ID_MAP + wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getDevice(), wsMessageDTO.getClientMessageId(), Long.class);
//        if (Objects.isNull(globalMessageId)) {
//            return null;
//        }
//        String key = null;
//        if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())){
//            key = RedisConstant.SINGLE_CHAT +
//                    (wsMessageDTO.getFromUserId() > wsMessageDTO.getTargetId()
//                            ? wsMessageDTO.getTargetId() + ":" + wsMessageDTO.getFromUserId()
//                            : wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getTargetId());
//        }else if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode())){
//            key = RedisConstant.GROUP_CHAT + wsMessageDTO.getTargetId();
//        }
//        return getMsgByScore(key, globalMessageId, MessageBO.class);
    }

    public <T> T getMsgByScore(String key, long score, Class<T> tClass) {
        return redisCacheService.zget(key, score, tClass);
    }

    public Long getMaxClientId(Long fromUserId, Integer device) {
        List<WsMessageDTO> temMessage = redisCacheService.getLastZSet(RedisConstant.TEM_MESSAGE + fromUserId + ":" + device, 50L, WsMessageDTO.class);
        // 将反转后的消息再反转
        Collections.reverse(temMessage);

        if (temMessage.isEmpty()) {
            return 0L;
        }
        Long pre = temMessage.get(0).getClientMessageId();
        for (int i = 1; i < temMessage.size(); i++) {
            if (temMessage.get(i).getClientMessageId() - pre != 1) {
                break;
            }
            pre = temMessage.get(i).getClientMessageId();
        }
        return pre;
    }

    public List<MessageBO> getUnreadMessage(Long userId, Long lastGlobalId) {
        Set<String> zget = redisCacheService.zget(RedisConstant.INBOX + userId, lastGlobalId, Long.MAX_VALUE, 0, Long.MAX_VALUE, MessageBO.class);
        return zget.stream().map(message -> JSON.parseObject(message, MessageBO.class)).collect(Collectors.toList());
    }


    public boolean isBusinessAcked(MessageBO message) {
        return redisCacheService.hasKey(RedisConstant.ALREADY_ACK + message.getId());
    }

    public void acked(Long id) {
        redisCacheService.setCacheObject(RedisConstant.ALREADY_ACK + id, "", RedisConstant.ACK_EXPIRE_TIME, TimeUnit.SECONDS);
    }
//    public Long getMaxClientId(Long fromUserId, Integer device) {
//        Long[] clientIds = redisCacheService.getCacheObject(RedisConstant.CLIENT_ID + fromUserId, Long[].class);
//        if (clientIds == null) {
//            clientIds = new Long[]{0L, 0L};
//            redisCacheService.setCacheObject(RedisConstant.CLIENT_ID + fromUserId, clientIds);
//        }
//        return clientIds[device];
//    }
}
