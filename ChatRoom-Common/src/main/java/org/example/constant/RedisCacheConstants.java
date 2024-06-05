package org.example.constant;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
public interface RedisCacheConstants {
    String ONLINE = "online:";
    Integer HEARTBEAT_TIMEOUT = 5;
    String ROOM_MEMBER = "room_member:";
    Long MEMBER_UPPER = 200L;

    String HASH_RING = "hash_ring";
    String GROUP_MESSAGE_ACK_KEY = "group_message_ack_key";
}
