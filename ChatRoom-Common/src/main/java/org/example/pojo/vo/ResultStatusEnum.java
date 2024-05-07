package org.example.pojo.vo;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
public enum ResultStatusEnum {

    SUCCESS(200,"成功"),
    BUSY(400, "服务器繁忙"),
    USER_NOT_EXIST(1001, "用户不存在"),
    PHONE_ALREADY_REGISTERED(1002, "手机号已注册"),
    TOKEN_EXPIRED(100, "token过期"),
    TOKEN_ERROR(101, "token错误")
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
