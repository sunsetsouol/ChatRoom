package org.example.onmessage.publish.event;

import lombok.Getter;
import org.example.pojo.dto.WsMessageDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
@Getter
public class MessageBusinessAckEvent extends ApplicationEvent {
    private WsMessageDTO wsMessageDTO;
    public MessageBusinessAckEvent(Object source, WsMessageDTO wsMessageDTO) {
        super(source);
        this.wsMessageDTO = wsMessageDTO;
    }

}
