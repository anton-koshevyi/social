package com.social.backend.dto.chat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
public class PrivateDto extends ChatDto {
  
  private List<User> members;
  
}
