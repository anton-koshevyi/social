package com.social.dto.chat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.user.UserDto;

@Getter
@Setter
public class PrivateChatDto extends ChatDto {

  private List<UserDto> members;

}
