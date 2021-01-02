package com.social.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.chat.ChatDto;

@Getter
@Setter
public class MessageDto extends ReplyDto {

  private ChatDto chat;

}
