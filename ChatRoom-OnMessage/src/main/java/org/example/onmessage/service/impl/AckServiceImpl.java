package org.example.onmessage.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.publish.PublishEventUtils;
import org.example.onmessage.service.AckService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.bo.MessageBO;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/5
 */
@Service
@RequiredArgsConstructor
public class AckServiceImpl implements AckService {
    private final RedisCacheService redisCacheService;
    private final PublishEventUtils publishEventUtils;
    @Override
    public Set<String> getUnAcked(MessageBO messageBO) {
        return redisCacheService.getCacheSet(RedisConstant.BUSINESS_ACK + messageBO.getId(), String.class);
    }

    @Override
    public void deleteAck(MessageBO messageBO) {
        redisCacheService.deleteObject(RedisConstant.BUSINESS_ACK + messageBO.getId());
    }

    @Override
    public long setBusinessAck(MessageBO messageBO, Set<String> userIds) {
        return redisCacheService.setCacheSet(RedisConstant.BUSINESS_ACK + messageBO.getId(), userIds);
    }

    @Override
    public boolean ack(MessageBO message, Set<String> userIds) {
        String key = RedisConstant.BUSINESS_ACK + message.getId();
        redisCacheService.removeSet(key, userIds);
        // 如果全部ack了直接给用户ack
        if (!redisCacheService.hasKey(key)){
            publishEventUtils.pushAckToUser(this, message, message.getFromUserId());
        }
        return true;
    }
}
