package com.social.backend.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.social.backend.model.user.Publicity;

public class PublicityValidatorTest {
  
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();
  
  @Test
  public void when_nullField_then_noViolations() {
    Assertions
        .assertThat(validator.validate(new Target(null)))
        .isEmpty();
  }
  
  @Test
  public void when_nonExistentPublicityValue_then_fieldViolation() {
    Assertions
        .assertThat(validator.validate(new Target(Integer.MAX_VALUE)))
        .extracting("getPropertyPath.toString",
            "getMessage")
        .containsExactly(new Tuple("field",
            "invalid publicity value"));
  }
  
  @ParameterizedTest
  @ValueSource(ints = {Publicity.PUBLIC, Publicity.INTERNAL, Publicity.PRIVATE})
  public void when_existentPublicityValue_then_noViolations(int publicity) {
    Assertions
        .assertThat(validator.validate(new Target(publicity)))
        .isEmpty();
  }
  
  private static class Target {
    
    @com.social.backend.constraint.Publicity
    private final Integer field;
    
    private Target(Integer field) {
      this.field = field;
    }
    
  }
  
}
