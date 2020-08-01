package com.social.backend.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChatDto {
  
  private Long id;
  private String type;
  
}
