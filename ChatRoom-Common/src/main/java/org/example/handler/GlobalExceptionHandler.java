//package org.example.handler;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.exception.BusinessException;
//import org.example.pojo.vo.Result;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.BindException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolationException;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/5/2
// */
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    /**
//     * 自定义验证异常
//     */
//    @ExceptionHandler(BindException.class)
//    public Result<String> handleBindException(BindException e, HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',参数校验异常{}", requestURI, e.getMessage());
//        String message = e.getAllErrors().get(0).getDefaultMessage();
//        return Result.fail(message);
//    }
//
//    /**
//     * 请求方式不支持
//     */
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public Result<String> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
//                                                              HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
//        return Result.fail(e.getMessage());
//    }
//
//
//    /**
//     * 自定义验证异常
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',参数校验异常{}", requestURI, e.getMessage());
//        String message = e.getBindingResult().getFieldError().getDefaultMessage();
//        return Result.fail(message);
//    }
//
//    /**
//     * Validation异常处理
//     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(ConstraintViolationException.class)
//    public Result<String> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',参数校验异常{}", requestURI, e.getMessage());
//        return Result.fail(e.getMessage());
//    }
//
//
//    /**
//     * 业务异常
//     */
//    @ExceptionHandler(BusinessException.class)
//    public Result<String> handleServiceException(BusinessException e, HttpServletRequest request) {
//        log.info("请求地址{}, 异常{}", request.getRequestURI(), e.getMessage());
//        return Result.fail(e.getCode(), e.getMessage());
//    }
//
//    /**
//     * 拦截未知的运行时异常
//     */
//    @ExceptionHandler(RuntimeException.class)
//    public Result<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
//        String requestURI = request.getRequestURI();
//        log.error("请求地址'{}',发生未知异常.", requestURI, e);
//        // 异步发
//        return Result.fail("服务器异常!" + e.getMessage());
//    }
//}
