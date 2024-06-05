package org.example.onmessage.publish.event;

import lombok.Getter;
import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Getter
public class MessageBusinessAckEvent extends ApplicationEvent {
    private MessageBO messageBO;
    private AbstractMessage.MessageType messageType;
    public MessageBusinessAckEvent(Object source, MessageBO messageBO, AbstractMessage.MessageType businessAck) {
        super(source);
        this.messageBO = messageBO;
        this.messageType = businessAck;
    }

}
