package com.social.backend.dto.chat;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.dto.user.UserDto;

@Getter
@Setter
public class GroupChatDto extends ChatDto {

  private String name;
  private Integer members;
  private UserDto owner;

}
