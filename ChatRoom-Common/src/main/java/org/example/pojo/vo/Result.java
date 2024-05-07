package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.constant.HttpStatus;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/1/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private Integer code;

    private String message;

    private T data;


    public static <T> Result<T> success(){
        return new Result<T>(ResultStatusEnum.SUCCESS.code(), ResultStatusEnum.SUCCESS.message(),null);
    }

    public static <T> Result<T> success(ResultStatusEnum statusEnum){
        return new Result<T>(statusEnum.code(), statusEnum.message(), null);
    }

    public static <T> Result<T> success(ResultStatusEnum statusEnum, T data){
        return new Result<T>(statusEnum.code(), statusEnum.message(), data);
    }

    public static <T> Result<T> success(T data){
        return new Result<T>(ResultStatusEnum.SUCCESS.code(), ResultStatusEnum.SUCCESS.message(), data);
    }

    public static <T> Result<T> fail(ResultStatusEnum statusEnum){
        return new Result<T>(statusEnum.code(), statusEnum.message(), null);
    }

    public static <T>Result<T> fail(String message) {
        return new Result<T>(HttpStatus.ERROR, message, null);
    }

    public static <T>Result<T> fail(Integer code, String message) {
        return new Result<T>(code, message, null);
    }
}
