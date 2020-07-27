package com.social.backend.validator;

import java.util.Objects;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.social.backend.constraint.FieldMatch;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String compared;
    private boolean notMatch;
    
    @Override
    public void initialize(FieldMatch annotation) {
        this.field = annotation.field();
        this.compared = annotation.compared();
        this.notMatch = annotation.notMatch();
    }
    
    @Override
    public boolean isValid(Object source, ConstraintValidatorContext context) {
        Object fieldValue = readField(source, field);
        Object comparedValue = readField(source, compared);
        boolean valid = (notMatch != Objects.equals(fieldValue, comparedValue));
        
        if (!valid) {
            String message = context.getDefaultConstraintMessageTemplate();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        
        return valid;
    }
    
    private static Object readField(Object source, String field) {
        try {
            return FieldUtils.readField(source, field, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read field: " + field + ", source: " + source, e);
        }
    }
}
