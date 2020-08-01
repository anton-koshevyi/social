package com.social.backend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.social.backend.model.user.Publicity;

public class PublicityValidator
    implements ConstraintValidator<com.social.backend.constraint.Publicity, Integer> {
  
  @Override
  public boolean isValid(Integer publicity, ConstraintValidatorContext context) {
    return publicity == null
        || Publicity.PUBLIC == publicity
        || Publicity.INTERNAL == publicity
        || Publicity.PRIVATE == publicity;
  }
  
}
