package org.example.onmessage.publish.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisCacheConstants;
import org.example.onmessage.publish.event.UserOfflineEvent;
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
public class UserOfflineListener {
    private final RedisCacheService redisCacheService;
    @EventListener(UserOfflineEvent.class)
    public void userOffline(UserOfflineEvent event) {
        Long userId = event.getUserId();
        log.info("用户下线事件：{}", userId);
        redisCacheService.deleteObject(RedisCacheConstants.ONLINE + userId);
    }
}
