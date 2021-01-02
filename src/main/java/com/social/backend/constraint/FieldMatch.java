package com.social.backend.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.social.backend.validator.FieldMatchValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(FieldMatch.List.class)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {
  
  String field();
  
  String compared();
  
  String message() default "fields '{field}' and '{compared}'"
      + " must ${notMatch ? 'not ' : ''}be equal";
  
  boolean notMatch() default false;
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface List {
  
    FieldMatch[] value();
  
  }
  
}
