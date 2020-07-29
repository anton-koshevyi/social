package com.social.backend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.social.backend.constraint.Publicity;

import static com.social.backend.model.user.Publicity.INTERNAL;
import static com.social.backend.model.user.Publicity.PRIVATE;
import static com.social.backend.model.user.Publicity.PUBLIC;

public class PublicityValidator implements ConstraintValidator<Publicity, Integer> {
    @Override
    public boolean isValid(Integer publicity, ConstraintValidatorContext context) {
        return publicity == null
                || PUBLIC == publicity
                || INTERNAL == publicity
                || PRIVATE == publicity;
    }
}
