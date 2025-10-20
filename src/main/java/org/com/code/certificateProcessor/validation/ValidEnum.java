package org.com.code.certificateProcessor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.com.code.certificateProcessor.validation.validator.EnumValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
    String message() default "无效的枚举值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}
