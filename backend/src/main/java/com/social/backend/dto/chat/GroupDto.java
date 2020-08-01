package com.social.backend.dto.chat;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
public class GroupDto extends ChatDto {
  
  private String name;
  private Integer members;
  private User owner;
  
}
