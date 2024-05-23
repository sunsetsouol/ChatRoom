package org.example.onmessage.publish.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private Long userId;
    private String ip;
    public UserOnlineEvent(Object source, Long userId, String ip) {
        super(source);
        this.userId = userId;
        this.ip = ip;
    }
}
