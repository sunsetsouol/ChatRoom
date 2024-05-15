package org.example.onmessage.publish.listener;

import lombok.RequiredArgsConstructor;
import org.example.onmessage.constants.RabbitMQConstant;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.bo.MessageBO;
import org.example.onmessage.mq.service.MQService;
import org.example.onmessage.publish.event.MessageBusinessAckEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Component
@RequiredArgsConstructor
public class MessageBusinessAckListener {
    private final MQService mqService;

    @EventListener(MessageBusinessAckEvent.class)
    public void messageBusinessAck(MessageBusinessAckEvent event) {
        MessageBO message = event.getMessageBO();
        message.setMessageType(AbstractMessage.MessageType.BUSINESS_ACK.getCode());
        mqService.push2mq(message);
    }
}