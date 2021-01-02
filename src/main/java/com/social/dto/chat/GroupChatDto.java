package com.social.dto.chat;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.user.UserDto;

@Getter
@Setter
public class GroupChatDto extends ChatDto {

  private String name;
  private Integer members;
  private UserDto owner;

}
