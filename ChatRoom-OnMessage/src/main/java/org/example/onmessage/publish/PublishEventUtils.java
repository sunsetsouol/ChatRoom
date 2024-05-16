package org.example.onmessage.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.entity.dto.WsMessageDTO;
import org.example.onmessage.mq.listener.RabbitMQListener;
import org.example.onmessage.publish.event.DealMessageEvent;
import org.example.onmessage.publish.event.MessageBusinessAckEvent;
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
}
