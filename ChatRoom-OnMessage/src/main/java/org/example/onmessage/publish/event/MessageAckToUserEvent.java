package org.example.onmessage.publish.event;

import lombok.Getter;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/6
 */
@Getter
public class MessageAckToUserEvent extends ApplicationEvent {
    private MessageBO message;
    private Long ackTargetUserId;
    private AbstractMessage.MessageType messageType;
    public MessageAckToUserEvent(Object source, MessageBO message, Long ackTargetUserId, AbstractMessage.MessageType messageType) {
        super(source);
        this.message = message;
        this.ackTargetUserId = ackTargetUserId;
        this.messageType = messageType;
    }
}
