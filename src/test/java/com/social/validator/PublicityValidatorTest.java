package com.social.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.social.model.user.Publicity;

public class PublicityValidatorTest {

  private final Validator validator =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void whenNullField_expectNoViolations() {
    Assertions
        .assertThat(validator.validate(new Target(null)))
        .isEmpty();
  }

  @Test
  public void whenNonexistentPublicityValue_expectFieldViolation() {
    Assertions
        .assertThat(validator.validate(new Target(Integer.MAX_VALUE)))
        .extracting(
            "getPropertyPath.toString",
            "getMessage"
        )
        .containsExactly(new Tuple(
            "field",
            "invalid publicity value"
        ));
  }

  @ParameterizedTest
  @ValueSource(ints = {Publicity.PUBLIC, Publicity.INTERNAL, Publicity.PRIVATE})
  public void whenExistentPublicityValue_expectNoViolations(int publicity) {
    Assertions
        .assertThat(validator.validate(new Target(publicity)))
        .isEmpty();
  }


  private static class Target {

    @com.social.constraint.Publicity
    private final Integer field;

    private Target(Integer field) {
      this.field = field;
    }

  }

}
