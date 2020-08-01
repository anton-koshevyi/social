package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
  
  @NotNull
  private Boolean moder;
  
}
