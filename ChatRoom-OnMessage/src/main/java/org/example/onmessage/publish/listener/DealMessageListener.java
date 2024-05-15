package org.example.onmessage.publish.listener;

import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RedisConstant;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.publish.event.DealMessageEvent;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class DealMessageListener {
    private final RedisCacheService redisCacheService;
    @EventListener(DealMessageEvent.class)
    public void dealMessage(DealMessageEvent event) {
        MessageBO message = event.getMessageBO();
        redisCacheService.deleteObject(RedisConstant.ACK + message.getFromUserId() + ":" + message.getId());
    }
}
