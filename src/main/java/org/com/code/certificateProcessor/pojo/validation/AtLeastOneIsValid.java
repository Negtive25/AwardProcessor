package org.com.code.certificateProcessor.pojo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.com.code.certificateProcessor.pojo.validation.validator.AtLeastOneIsValidValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 作用于类上
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneIsValidValidator.class) // 指定校验器
public @interface AtLeastOneIsValid {
    String message() default "至少一个字段满足约束条件";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fieldNames(); // 接收要检查的字段名
}
