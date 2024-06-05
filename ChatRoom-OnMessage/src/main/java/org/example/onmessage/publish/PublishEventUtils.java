package org.example.onmessage.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.publish.event.MessageAckToUserEvent;
import org.example.onmessage.service.impl.AckServiceImpl;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.example.onmessage.publish.event.DealMessageEvent;
import org.example.onmessage.publish.event.MessageBusinessAckEvent;
import org.example.onmessage.publish.event.UserOfflineEvent;
import org.example.onmessage.publish.event.UserOnlineEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

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

    public void pushMessageAck(Object source, MessageBO messageBO) {
        applicationEventPublisher.publishEvent(new MessageBusinessAckEvent(source, messageBO, AbstractMessage.MessageType.USER_RECEIVE_ACK));
    }


    public void userOnline(Object source, Long userId, WebSocketSession webSocketSession) {
        applicationEventPublisher.publishEvent(new UserOnlineEvent(source, userId, webSocketSession));
    }

    public void userOffline(Object source, Long userId) {
        applicationEventPublisher.publishEvent(new UserOfflineEvent(source, userId));
    }

    public void pushAckToUser(Object source, MessageBO message, Long ackTargetUserId) {
        applicationEventPublisher.publishEvent(new MessageAckToUserEvent(source, message, ackTargetUserId, AbstractMessage.MessageType.SERVER_ACK));
    }
}
