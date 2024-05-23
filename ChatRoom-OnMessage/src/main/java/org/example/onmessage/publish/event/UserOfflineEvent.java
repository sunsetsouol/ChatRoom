package org.example.onmessage.publish.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private final Long userId;
    public UserOfflineEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }

}
