package org.com.code.certificateProcessor.pojo.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.com.code.certificateProcessor.pojo.validation.ValidEnum;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null ，交由 @NotNull 去校验，这里返回 true 表示不干预
        if (value == null) {
            return true;
        }

        // 遍历枚举类中所有的常量，判断是否匹配
        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value)); // 忽略大小写匹配

        if (isValid) {
            return true;
        } else {
            // 校验失败，开始自定义消息

            // 1. 禁用默认消息 (即注解上 "message() default ...")
            context.disableDefaultConstraintViolation();

            // 2. 在 Java 代码中动态构建您的新消息
            String validValuesString = String.join(", ", Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .toArray(String[]::new));
            String dynamicMessage = String.format(
                    "您提供的值 '%s' 是无效的。有效值必须是: [%s]",
                    value, // 动态插入非法值
                    validValuesString  // 动态插入所有可能值
            );

            // 3. 使用新消息模板构建并添加违规
            context.buildConstraintViolationWithTemplate(dynamicMessage)
                    .addConstraintViolation();

            // 4. 必须返回 false
            return false;
        }
    }
}
