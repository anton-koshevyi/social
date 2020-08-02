package com.social.backend.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Size(min = 1, max = 20)
@Constraint(validatedBy = {})
public @interface GroupChatName {
  
  String message() default "invalid group chat name";
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
  
}
