package org.example.onmessage.publish.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.publish.event.UserOnlineEvent;
import org.example.onmessage.service.common.RedisCacheService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserOnlineListener {
    private final RedisCacheService redisCacheService;
    @EventListener(UserOnlineEvent.class)
    public void userOnline(UserOnlineEvent event) {
        Long userId = event.getUserId();
        String ip = event.getIp();
        log.info("用户上线事件：{}", event.getUserId());
        redisCacheService.setCacheObject(RedisCacheConstants.ONLINE + userId, ip);
    }
}
