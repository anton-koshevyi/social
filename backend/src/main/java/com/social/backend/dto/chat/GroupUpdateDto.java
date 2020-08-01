package com.social.backend.dto.chat;

import javax.validation.constraints.NotNull;

public class GroupUpdateDto {
  
  @NotNull
  private String name;
  
  public GroupUpdateDto setName(String name) {
    this.name = name;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
}
