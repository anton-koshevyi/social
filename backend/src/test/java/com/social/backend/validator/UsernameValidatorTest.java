package com.social.backend.validator;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ValidationAutoConfiguration.class)
public class UsernameValidatorTest {
  
  @MockBean
  private UserRepository userRepository;
  
  @Autowired
  private Validator validator;
  
  @Test
  public void when_nullField_then_noViolations() {
    Assertions
        .assertThat(validator.validate(new Target(null)))
        .isEmpty();
  }
  
  @Test
  public void when_existentUsername_then_fieldViolation() {
    Mockito
        .when(userRepository.existsByUsername("username"))
        .thenReturn(true);
    
    Assertions
        .assertThat(validator.validate(new Target("username")))
        .extracting("getPropertyPath.toString",
            "getMessage")
        .containsExactly(new Tuple("field",
            "username already exists"));
  }
  
  @Test
  public void when_nonexistentUsername_then_noViolations() {
    Assertions
        .assertThat(validator.validate(new Target("username")))
        .isEmpty();
  }
  
  private static class Target {
    
    @Username
    private final String field;
    
    private Target(String field) {
      this.field = field;
    }
    
  }
  
}
