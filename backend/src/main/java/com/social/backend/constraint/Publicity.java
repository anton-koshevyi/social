package com.social.backend.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.social.backend.validator.PublicityValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PublicityValidator.class)
public @interface Publicity {
    String message() default "invalid publicity value";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
