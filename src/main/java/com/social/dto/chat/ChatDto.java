package com.social.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChatDto {

  private Long id;
  private String type;

}
