package com.social.validator;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.social.constraint.FieldMatch;

public class FieldMatchValidatorTest {

  private final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void givenAnyNotMatch_whenExceptionOnFieldReading_thenException()
      throws IllegalAccessException {
    try (MockedStatic<FieldUtils> ignored = Mockito.mockStatic(FieldUtils.class)) {
      Match target = new Match(1, 1);
      Mockito
          .when(FieldUtils.readField(target, "field", true))
          .thenThrow(new IllegalAccessException("Cause"));

      Assertions
          .assertThatThrownBy(() -> validator.validate(target))
          .isExactlyInstanceOf(ValidationException.class)
          .hasCause(new RuntimeException("Unable to read field: field"));
    }
  }

  @Test
  public void givenFalseNotMatch_whenEqualFields_thenNoViolations() {
    Assertions
        .assertThat(validator.validate(new Match(1, 1)))
        .isEmpty();
  }

  @Test
  public void givenFalseNotMatch_whenNotEqualFields_thenSingleViolationsOnlyForField() {
    Assertions
        .assertThat(validator.validate(new Match(1, 2)))
        .extracting(
            "getPropertyPath.toString",
            "getMessage"
        )
        .containsExactly(new Tuple(
            "field",
            "fields 'field' and 'compared' must be equal"
        ));
  }

  @Test
  public void givenTrueNotMatch_whenNotEqualFields_thenNoViolations() {
    Assertions
        .assertThat(validator.validate(new Not(1, 2)))
        .isEmpty();
  }

  @Test
  public void givenTrueNotMatch_whenEqualFields_thenSingleViolationsOnlyForField() {
    Assertions
        .assertThat(validator.validate(new Not(1, 1)))
        .extracting(
            "getPropertyPath.toString",
            "getMessage"
        )
        .containsExactly(new Tuple(
            "field",
            "fields 'field' and 'compared' must not be equal"
        ));
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
