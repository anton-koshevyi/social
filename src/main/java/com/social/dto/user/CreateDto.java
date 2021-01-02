package com.social.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.constraint.Email;
import com.social.constraint.FieldMatch;
import com.social.constraint.FirstName;
import com.social.constraint.LastName;
import com.social.constraint.Password;
import com.social.constraint.Username;

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
