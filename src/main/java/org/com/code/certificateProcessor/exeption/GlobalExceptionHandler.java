package org.com.code.certificateProcessor.exeption;


import org.com.code.certificateProcessor.responseHandler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 专门用来处理 @Valid 抛出的 MethodArgumentNotValidException 异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
    @ResponseBody
    public ResponseHandler handleValidationExceptions(MethodArgumentNotValidException ex) {
        ex.printStackTrace();

        Map<String, String> errors = new HashMap<>();
        // 遍历所有字段的验证错误
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField(); // 获取出错的字段名
            String errorMessage = error.getDefaultMessage(); // 获取 DTO 中定义的 message
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("message", "请求参数验证失败");
        responseBody.put("errors", errors); // 包含所有字段的错误信息

        return new ResponseHandler(ResponseHandler.BAD_REQUEST, "请求参数验证失败", responseBody);
    }

    @ExceptionHandler(StudentException.class)
    public ResponseHandler handleDatabaseException(StudentException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR, "数据库异常", ex.getMessage());
    }

    @ExceptionHandler(AdminException.class)
    public ResponseHandler handleAdminException(AdminException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR, "管理员异常", ex.getMessage());
    }

    @ExceptionHandler(CredentialsException.class)
    public ResponseHandler handleCreDentialsException(CredentialsException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.UNAUTHORIZED, "身份验证失败",ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseHandler handleResourceNotFoundException(ResourceNotFoundException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.NOT_FOUND, "资源未找到",ex.getMessage());
    }

    @ExceptionHandler(RocketmqException.class)
    public ResponseHandler handleRocketmqException(RocketmqException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR,"RocketMQ异常",ex.getMessage());
    }
    @ExceptionHandler(OSSException.class)
    public ResponseHandler handleOSSException(OSSException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR,"对象存储服务异常",ex.getMessage());
    }

    @ExceptionHandler(getAdaptiveCompressedUrl.class)
    public ResponseHandler handlegetAdaptiveCompressedUrl(getAdaptiveCompressedUrl ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR,"图片压缩服务异常",ex.getMessage());
    }

    @ExceptionHandler(ElasticSearchException.class)
    public ResponseHandler handleElasticSearchException(ElasticSearchException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR,"ElasticSearch服务异常",ex.getMessage());
    }

    @ExceptionHandler(AIModelException.class)
    public ResponseHandler handleAIModelException(AIModelException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.SERVER_ERROR,"AI模型服务异常",ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseHandler handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ex.printStackTrace();
        return new ResponseHandler(ResponseHandler.BAD_REQUEST,"上传文件大小超出限制",ex.getMessage());
    }

}
