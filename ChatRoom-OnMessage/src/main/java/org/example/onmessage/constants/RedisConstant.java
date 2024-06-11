package org.example.onmessage.constants;

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
    String EXPIRED = "expired";
    String DEL = "del";
    String SINGLE_CHAT = "single_chat:";
    String INBOX = "inbox:";

    String CLIENT_ID_MAP = "clientIdMap:";

    String GROUP_CHAT = "group_chat:";
    String GROUP_INBOX = "group_inbox:";
    String BUSINESS_ACK = "business_ack:";

    String ALREADY_ACK = "already_ack:";

    Integer ACK_EXPIRE_TIME = 60;
    String SLIDING_WINDOW_LIMITER_PREFIX = "sliding_window_limiter:";

    Integer SLIDING_WINDOW_RATE = 10;

    Integer SLIDING_WINDOW_CAPACITY = 50;
    String TOKEN_BUKET_LIMITER_TOKEN_PREFIX = "token_bucket_limiter_token:";
    String TOKEN_BUKET_LIMITER_TIMESTAMP_PREFIX = "token_bucket_limiter_timestamp:";
    Integer TOKEN_BUKET_RATE = 2;
    Integer TOKEN_BUKET_CAPACITY = 10;
    Integer TOKEN_BUKET_REQUESTED = 1;
    String TOKEN_BUCKET = "tokenBucket";
}
