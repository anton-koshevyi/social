package com.social.backend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.social.backend.constraint.Email;
import com.social.backend.repository.UserRepository;

@Component
public class EmailValidator implements ConstraintValidator<Email, String> {
    private final UserRepository userRepository;
    
    @Autowired
    public EmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email == null || !userRepository.existsByEmail(email);
    }
}
