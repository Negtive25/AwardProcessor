package org.com.code.certificateProcessor.pojo.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.com.code.certificateProcessor.pojo.validation.ValidPassword;

// 1. 实现 ConstraintValidator 接口
// 2. 泛型参数：<我们创建的注解, 要验证的数据类型>
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // 你可以在这里定义正则表达式
    // 这个表达式的含义：
    // (?=.*[0-9])   - 至少包含一个数字
    // (?=.*[a-z])   - 至少包含一个小写字母
    // (?=.*[A-Z])   - 至少包含一个大写字母
    // .* - 匹配任意字符
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$";

    /**
     * 核心验证逻辑
     *
     * @param password 客户端传过来的密码值
     * @param context  验证上下文
     * @return true = 验证通过, false = 验证失败
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        // 如果密码为 null 或为空，我们不在这里处理。
        // 让 @NotBlank 或 @NotEmpty 去处理“必须存在”的逻辑。
        if (password == null || password.isEmpty()) {
            return true;
        }

        // 使用正则表达式进行匹配
        return password.matches(PASSWORD_PATTERN);
    }
}