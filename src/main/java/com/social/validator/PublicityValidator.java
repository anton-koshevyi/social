package com.social.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.social.model.user.Publicity;

public class PublicityValidator
    implements ConstraintValidator<com.social.constraint.Publicity, Integer> {

  @Override
  public boolean isValid(Integer publicity, ConstraintValidatorContext context) {
    return publicity == null
        || publicity == Publicity.PUBLIC
        || publicity == Publicity.INTERNAL
        || publicity == Publicity.PRIVATE;
  }

}
