package org.example.onmessage.dao;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RedisConstant;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    // TODO：细分一点，不要全用这个函数
    // 1. 读取指定score的set
    // 2. 倒数指定个数的set
    // 3. 正途，找到clientId后的信息
    // 4. 找到clientId后的信息
    public <T> List<T> getWindowsMsg(String key, long min, long max, long offset, long bufferSize, Class<T> tclass) {
        Set<ZSetOperations.TypedTuple<String>> zget = redisCacheService.zget(key, min, max, offset, bufferSize, tclass);
        return zget.stream().map(typedTuple -> JSON.parseObject(typedTuple.getValue(), tclass)).collect(Collectors.toList());
    }

    public<T> List<T> getMsg(String key, Long bufferSize, Class<T> tclass) {
        Set<ZSetOperations.TypedTuple<String>> zget = redisCacheService.zget(key, 0, Long.MAX_VALUE, 0, bufferSize, tclass);
        return zget.stream().map(typedTuple -> JSON.parseObject(typedTuple.getValue(), tclass)).collect(Collectors.toList());
    }


    public <T> boolean hasMsg(String key, Long globalMessageId, Class<T> tClass) {
        return Objects.nonNull(redisCacheService.zget(key,  globalMessageId, tClass));
    }

    public MessageBO getMessageByClientId(WsMessageDTO wsMessageDTO) {
        Long globalMessageId = redisCacheService.zget(RedisConstant.CLIENT_ID_MAP + wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getDevice(), wsMessageDTO.getClientMessageId(), Long.class);
        if (Objects.isNull(globalMessageId)) {
            return null;
        }
        String key = null;
        if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())){
            key = RedisConstant.SINGLE_CHAT +
                    (wsMessageDTO.getFromUserId() > wsMessageDTO.getTargetId()
                            ? wsMessageDTO.getTargetId() + ":" + wsMessageDTO.getFromUserId()
                            : wsMessageDTO.getFromUserId() + ":" + wsMessageDTO.getTargetId());
        }else if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode())){
            key = RedisConstant.GROUP_CHAT + wsMessageDTO.getTargetId();
        }
        return getMsgByScore(key, globalMessageId, MessageBO.class);
    }

    public <T> T getMsgByScore(String key, long score, Class<T> tClass) {
        return redisCacheService.zget(key, score, tClass);
    }

    public Long getMaxClientId(Long fromUserId, Integer device) {
        Double clientId = redisCacheService.getFirstZSetScore(RedisConstant.CLIENT_ID_MAP + fromUserId + ":" + device);
        if (Objects.isNull(clientId)){
            return 0L;
        }
        return clientId.longValue();
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
