package org.com.code.certificateProcessor.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.com.code.certificateProcessor.validation.ValidEnum;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 或 空字符串的情况，交由 @NotBlank 去校验，这里返回 true 表示不干预
        if (value == null || value.isBlank()) {
            return true;
        }

        // 遍历枚举类中所有的常量，判断是否匹配
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value)); // 忽略大小写匹配
    }
}
