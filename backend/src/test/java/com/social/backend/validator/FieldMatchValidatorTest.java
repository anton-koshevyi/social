package com.social.backend.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import com.social.backend.constraint.FieldMatch;

public class FieldMatchValidatorTest {
  
  private static final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();
  
  @Test
  public void given_falseNotMatch_when_equalFields_then_noViolations() {
    Assertions
        .assertThat(validator.validate(new Match(1, 1)))
        .isEmpty();
  }
  
  @Test
  public void given_falseNotMatch_when_notEqualFields_then_singleViolationsOnlyForField() {
    Assertions
        .assertThat(validator.validate(new Match(1, 2)))
        .extracting("getPropertyPath.toString",
            "getMessage")
        .containsExactly(new Tuple("field",
            "fields 'field' and 'compared' must be equal"));
  }
  
  @Test
  public void given_trueNotMatch_when_notEqualFields_then_noViolations() {
    Assertions
        .assertThat(validator.validate(new Not(1, 2)))
        .isEmpty();
  }
  
  @Test
  public void given_trueNotMatch_when_equalFields_then_singleViolationsOnlyForField() {
    Assertions
        .assertThat(validator.validate(new Not(1, 1)))
        .extracting("getPropertyPath.toString",
            "getMessage")
        .containsExactly(new Tuple("field",
            "fields 'field' and 'compared' must not be equal"));
  }
  
  @FieldMatch(field = "field", compared = "compared")
  private static class Match {
    
    private final Object field;
    private final Object compared;
    
    private Match(Object field, Object compared) {
      this.field = field;
      this.compared = compared;
    }
    
  }
  
  @FieldMatch(notMatch = true, field = "field", compared = "compared")
  private static class Not {
    
    private final Object field;
    private final Object compared;
    
    private Not(Object field, Object compared) {
      this.field = field;
      this.compared = compared;
    }
    
  }
  
}
