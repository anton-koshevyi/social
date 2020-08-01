package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.Email;
import com.social.backend.constraint.FieldMatch;
import com.social.backend.constraint.FirstName;
import com.social.backend.constraint.LastName;
import com.social.backend.constraint.Password;
import com.social.backend.constraint.Username;

@Getter
@Setter
@FieldMatch(field = "password", compared = "confirm")
public class CreateDto {
  
  @NotNull
  @Email
  private String email;
  
  @NotNull
  @Username
  private String username;
  
  @NotNull
  @FirstName
  private String firstName;
  
  @NotNull
  @LastName
  private String lastName;
  
  @NotNull
  @Password
  private String password;
  private String confirm;
  
}
