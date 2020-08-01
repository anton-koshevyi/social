package com.social.backend.dto.reply;

import com.social.backend.model.chat.Chat;

public class MessageDto extends ReplyDto {
  
  private Chat chat;
  
  public MessageDto setChat(Chat chat) {
    this.chat = chat;
    return this;
  }
  
  public Chat getChat() {
    return chat;
  }
  
}
