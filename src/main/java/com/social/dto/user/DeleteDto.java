package com.social.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.constraint.Password;

@Getter
@Setter
public class DeleteDto {
  
  @NotNull
  @Password
  private String password;
  
}
