package com.social.backend.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.social.backend.constraint.Publicity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import static com.social.backend.model.user.Publicity.INTERNAL;
import static com.social.backend.model.user.Publicity.PRIVATE;
import static com.social.backend.model.user.Publicity.PUBLIC;

public class PublicityValidatorTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    public void when_nullField_then_noViolations() {
        assertThat(validator.validate(new Target(null)))
                .isEmpty();
    }
    
    @Test
    public void when_noPublicityForValue_then_fieldViolation() {
        assertThat(validator.validate(new Target(Integer.MAX_VALUE)))
                .extracting("getPropertyPath.toString", "getMessage")
                .containsExactly(tuple("field", "invalid publicity value"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {PUBLIC, INTERNAL, PRIVATE})
    public void when_publicityForValueExists_then_noViolations(int publicity) {
        assertThat(validator.validate(new Target(publicity)))
                .isEmpty();
    }
    
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static class Target {
        @Publicity
        private final Integer field;
        
        private Target(Integer field) {
            this.field = field;
        }
    }
}
