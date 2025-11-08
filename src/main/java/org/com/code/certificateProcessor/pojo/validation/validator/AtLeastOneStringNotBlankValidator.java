package org.com.code.certificateProcessor.pojo.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.com.code.certificateProcessor.pojo.validation.AtLeastOneStringNotBlank;

import java.lang.reflect.Field;

public class AtLeastOneStringNotBlankValidator implements ConstraintValidator<AtLeastOneStringNotBlank, Object> {

    private String[] fieldNames;

    @Override
    public void initialize(AtLeastOneStringNotBlank constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fieldNames();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try {
            for (String fieldName : fieldNames) {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true); // 允许访问私有字段

                Object subObject = field.get(object);
                if(subObject != null&&!((String)subObject).isEmpty()){
                    return true;
                }

            }
        } catch (Exception e) {
            return false;
        }
        return false; // 校验失败
    }
}
