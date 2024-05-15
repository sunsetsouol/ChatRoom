package org.example.onmessage.publish.event;

import lombok.Getter;
import org.example.onmessage.entity.bo.MessageBO;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Getter
public class DealMessageEvent extends ApplicationEvent {
    private MessageBO messageBO;
    public DealMessageEvent(Object source, MessageBO messageBO) {
        super(source);
        this.messageBO = messageBO;
    }
}
