package org.com.code.certificateProcessor.pojo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.com.code.certificateProcessor.pojo.validation.validator.EnumValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//只要注解写在泛型尖括号 <...> 里面，就需要 ElementType.TYPE_USE
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
    // 添加自定义参数不合理返回的消息
    String message() default "必须是有效的枚举值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}
