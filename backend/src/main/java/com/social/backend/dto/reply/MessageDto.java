package com.social.backend.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.chat.Chat;

@Getter
@Setter
public class MessageDto extends ReplyDto {
  
  private Chat chat;
  
}
