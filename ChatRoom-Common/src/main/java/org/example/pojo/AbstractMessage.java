package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@Data
public abstract class AbstractMessage {

    /**
     * {@link MessageType#code}
     * 消息类型
     */
    private Integer messageType;

    /**
     * 回复消息id
     */
    private Long replyMessageId;

    /**
     * 设备
     * {@link DeviceType#code}
     */
    private Integer device;

    @AllArgsConstructor
    @Getter
    public enum MessageType {
        PING(1, "心跳"),
        SINGLE(2, "私聊"),
        GROUP(3, "群聊"),
        ACK(4, "服务器接收到用户消息的ACK"),
        SERVER_ACK(5, "服务器正确处理用户消息的ACK"),
        GET_MESSAGE(6, "获取消息"),
        GET_MESSAGE_ACK(7, "获取消息ACK"),
        INIT(8, "初始化"),
        FILL_EMPTY(9, "填充空消息"),
        USER_RECEIVE_ACK(10, "用户接收到服务器消息的ACK"),
        BUSINESS_ACK(11, "服务器之间业务ACK"),
        ;

        private final Integer code;
        private final String type;
        private final static Map<Integer, String> typeMap = new HashMap<>();
        static {
            for (MessageType type : MessageType.values()) {
                typeMap.put(type.code, type.type);
            }
        }

        public static Map<Integer, String> getTypeMap() {
            return typeMap;
        }

        public static String getType(Integer code) {
            return typeMap.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum DeviceType {
        WEB(0, "WEB"),
        MOBILE(1, "MOBILE"),
        ;

        private final Integer code;
        private final String type;
        private final static Map<Integer, String> typeMap = new HashMap<>();
        static {
            for (DeviceType type : DeviceType.values()) {
                typeMap.put(type.code, type.type);
            }
        }

        public static Map<Integer, String> getTypeMap() {
            return typeMap;
        }

        public static String getType(Integer code) {
            return typeMap.get(code);
        }
    }
}
