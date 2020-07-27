package com.social.backend.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

import com.social.backend.constraint.FieldMatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class FieldMatchValidatorTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    public void given_falseNotMatch_when_fieldsAreEqual_then_noViolations() {
        assertThat(validator.validate(new Match(1, 1)))
                .isEmpty();
    }
    
    @Test
    public void given_falseNotMatch_when_fieldsAreNotEquals_then_singleViolationsOnlyForField() {
        assertThat(validator.validate(new Match(1, 2)))
                .extracting("getPropertyPath.toString", "getMessage")
                .containsExactly(tuple("field", "fields 'field' and 'compared' must be equal"));
    }
    
    @Test
    public void given_trueNotMatch_when_fieldsAreNotEqual_then_noViolations() {
        assertThat(validator.validate(new Not(1, 2)))
                .isEmpty();
    }
    
    @Test
    public void given_trueNotMatch_when_fieldsAreEqual_then_singleViolationsOnlyForField() {
        assertThat(validator.validate(new Not(1, 1)))
                .extracting("getPropertyPath.toString", "getMessage")
                .containsExactly(tuple("field", "fields 'field' and 'compared' must not be equal"));
    }
    
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @FieldMatch(field = "field", compared = "compared")
    private static class Match {
        private final Object field;
        private final Object compared;
        
        private Match(Object field, Object compared) {
            this.field = field;
            this.compared = compared;
        }
    }
    
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
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
