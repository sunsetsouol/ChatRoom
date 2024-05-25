package org.example.onmessage.publish.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private Long userId;
    private WebSocketSession webSocketSession;

    public UserOnlineEvent(Object source, Long userId, WebSocketSession webSocketSession) {
        super(source);
        this.userId = userId;
        this.webSocketSession = webSocketSession;
    }
}
