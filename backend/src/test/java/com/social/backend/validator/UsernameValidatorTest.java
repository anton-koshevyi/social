package com.social.backend.validator;

import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.social.backend.constraint.Username;
import com.social.backend.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationAutoConfiguration.class)
public class UsernameValidatorTest {
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private Validator validator;
    
    @Test
    public void when_nullField_then_noViolations() {
        assertThat(validator.validate(new Target(null)))
                .isEmpty();
    }
    
    @Test
    public void when_usernameAlreadyExists_then_fieldViolation() {
        Mockito.when(userRepository.existsByUsername("username")).thenReturn(true);
        assertThat(validator.validate(new Target("username")))
                .extracting("getPropertyPath.toString", "getMessage")
                .containsExactly(tuple("field", "username already exists"));
    }
    
    @Test
    public void when_usernameDoesNotExist_then_noViolations() {
        assertThat(validator.validate(new Target("username")))
                .isEmpty();
    }
    
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static class Target {
        @Username
        private final String field;
        
        private Target(String field) {
            this.field = field;
        }
    }
}
