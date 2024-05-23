package org.example.onmessage.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.publish.event.DealMessageEvent;
import org.example.onmessage.publish.event.MessageBusinessAckEvent;
import org.example.onmessage.publish.event.UserOfflineEvent;
import org.example.onmessage.publish.event.UserOnlineEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 事件发布工具类
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PublishEventUtils {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void dealMessage(Object source, MessageBO messageBO){
        applicationEventPublisher.publishEvent(new DealMessageEvent(source, messageBO));
    }

    public void pushMessageAck(Object source, WsMessageDTO wsMessageDTO) {
        applicationEventPublisher.publishEvent(new MessageBusinessAckEvent(source, wsMessageDTO));
    }

    public void userOnline(Object source, Long userId, String ip) {
        applicationEventPublisher.publishEvent(new UserOnlineEvent(source, userId, ip));
    }

    public void userOffline(Object source, Long userId) {
        applicationEventPublisher.publishEvent(new UserOfflineEvent(source, userId));
    }
}
