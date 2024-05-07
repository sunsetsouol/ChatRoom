package org.example.exception;

import org.example.pojo.vo.ResultStatusEnum;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/2
 */
public class BusinessException extends RuntimeException{
    private final Integer code;
    private final String message;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(ResultStatusEnum resultStatusEnum) {
        this.code = resultStatusEnum.code();
        this.message = resultStatusEnum.message();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
