package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.FieldMatch;
import com.social.backend.constraint.Password;

@Getter
@Setter
@FieldMatch(notMatch = true, field = "actual", compared = "change")
@FieldMatch(field = "change", compared = "confirm")
public class PasswordDto {
  
  @NotNull
  @Password
  private String actual;
  
  @NotNull
  @Password
  private String change;
  private String confirm;
  
}
