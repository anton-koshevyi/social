package com.social.backend.dto.chat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.dto.user.UserDto;

@Getter
@Setter
public class PrivateDto extends ChatDto {

  private List<UserDto> members;

}
