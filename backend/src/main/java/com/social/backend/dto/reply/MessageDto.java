package com.social.backend.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.dto.chat.ChatDto;

@Getter
@Setter
public class MessageDto extends ReplyDto {

  private ChatDto chat;

}
