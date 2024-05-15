package org.example.onmessage.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
public interface RedisConstant {
    String BUFFER_PREFIX = "buffer:";
    long LOCK_TIME = 3;
    long WAIT_TIME = 10;

    String TEM_MESSAGE = "tem:message:";

    String MESSAGE = "message:";
    String ACK = "ack:";
    Integer ACK_EXPIRE_TIME = 10;
    String EXPIRED = "expired";
    String DEL = "del";
    String SINGLE_CHAT = "single_chat:";
    String INBOX = "inbox:";
}
