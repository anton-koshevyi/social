package com.social.backend.dto.chat;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupUpdateDto {
  
  @NotNull
  private String name;
  
}
