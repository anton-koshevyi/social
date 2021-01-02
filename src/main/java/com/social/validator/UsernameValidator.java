package com.social.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.social.constraint.Username;
import com.social.repository.UserRepository;

@Component
public class UsernameValidator implements ConstraintValidator<Username, String> {
  
  private final UserRepository userRepository;
  
  @Autowired
  public UsernameValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @Override
  public boolean isValid(String username, ConstraintValidatorContext context) {
    return username == null || !userRepository.existsByUsername(username);
  }
  
}
