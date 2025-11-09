package org.com.code.certificateProcessor.exeption;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * MethodArgumentNotValidException
     *
     * 触发条件：@RequestBody 对象校验失败（DTO 对象上的注解，如 @NotNull、@Size）。
     *
     * 可以通过 getBindingResult().getAllErrors() 获取每个字段的错误信息。
     *
     * ConstraintViolationException
     *
     * 触发条件：@Validated 注解在方法参数上（非 @RequestBody，例如 @RequestParam、@PathVariable）或者自定义注解校验失败。
     *
     * 可以通过 ex.getConstraintViolations() 遍历获取具体的参数错误。
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // 遍历所有校验错误
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        // 类级别错误（比如你的 @AtLeastOneIsValid）
        ex.getBindingResult().getGlobalErrors()
                .forEach(e -> errors.put("global", e.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            // 获取参数路径，例如方法名+参数名
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(StudentTableException.class)
    public ResponseEntity<Object> handleDatabaseException(StudentTableException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("数据库异常：" + ex.getMessage());
    }

    @ExceptionHandler(AwardSubmissionException.class)
    public ResponseEntity<Object> handleAwardSubmissionException(AwardSubmissionException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("奖状提交模块异常：" + ex.getMessage());
    }

    @ExceptionHandler(AdminTableException.class)
    public ResponseEntity<Object> handleAdminException(AdminTableException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("数据库异常：" + ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("资源未找到：" + ex.getMessage());
    }

    @ExceptionHandler(OSSException.class)
    public ResponseEntity<Object> handleOSSException(OSSException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("对象存储服务异常：" + ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("上传文件大小超出限制：" + ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("未授权：" + ex.getMessage());
    }

}
