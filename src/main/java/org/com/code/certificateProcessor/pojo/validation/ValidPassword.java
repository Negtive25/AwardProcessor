package org.com.code.certificateProcessor.pojo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.com.code.certificateProcessor.pojo.validation.validator.PasswordValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义验证注解：用于验证密码是否符合复杂度要求
 */
@Documented
// @Constraint 关键注解，指定这个注解的验证逻辑由哪个类（Validator）来处理
@Constraint(validatedBy = PasswordValidator.class)

/**
 * 常见的 @Target 取值对照表：
 * ElementType	说明
 * TYPE	类、接口（包括注解类型）、枚举
 * FIELD	成员变量（包括枚举常量）
 * METHOD	方法
 * PARAMETER	方法参数
 * CONSTRUCTOR	构造函数
 * LOCAL_VARIABLE	局部变量
 * ANNOTATION_TYPE	注解类型
 * PACKAGE	包
 * TYPE_USE	任意类型使用的地方（Java 8+）
 */
// @Target 指定这个注解可以用在哪些地方（FIELD = 字段上）
@Target({ FIELD, PARAMETER })
// @Retention 指定注解在什么时候生效（RUNTIME = 运行时）
@Retention(RUNTIME)
public @interface ValidPassword {

    // 验证失败时的默认错误信息
    String message() default "密码必须包含至少一个数字、一个小写字母和一个大写字母";

    // 这两个是 JSR-303 规范要求的固定写法，用于分组和负载
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
