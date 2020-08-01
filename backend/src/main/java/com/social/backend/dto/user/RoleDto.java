package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

public class RoleDto {
  
  @NotNull
  private Boolean moder;
  
  public RoleDto setModer(Boolean moder) {
    this.moder = moder;
    return this;
  }
  
  public Boolean getModer() {
    return moder;
  }
  
}
