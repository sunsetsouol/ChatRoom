package org.example.pojo.vo;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
public enum ResultStatusEnum {

    SUCCESS(200,"成功"),
    BUSY(400, "服务器繁忙"),
    UNAUTHORIZED(403, "没有权限"),
    TOKEN_EXPIRED(100, "token过期"),
    TOKEN_ERROR(101, "token错误"),
    USER_NOT_EXIST(1001, "用户不存在"),
    PHONE_ALREADY_REGISTERED(1002, "手机号已注册"),
    EMPTY_MESSAGE_TEXT(2001, "文本消息不能为空"),
    EMPTY_MESSAGE_FILE(2002, "二进制数据不能为空"),
    FROM_USER_ID_EMPTY(2003, "发送者消息id不能为空"),
    RECEIVER_USER_ID_EMPTY(2004, "接收者id不能为空"),


    GROUP_ID_EMPTY(2005, "群聊id不能为空"),
    FREQUENT_SEND(2006, "消息发送频繁"),
    DEVICE_EMPTY(2007, "设备不能为空"),
    MESSAGE_NOT_FOUND(2008, "消息找不到了")
    ;

    private final Integer code;

    private final String message;

    ResultStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
