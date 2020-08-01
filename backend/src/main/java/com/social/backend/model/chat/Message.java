package com.social.backend.model.chat;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.social.backend.model.Reply;

@Entity
@Table(name = "messages")
public class Message extends Reply {
  
  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;
  
  public Message setChat(Chat chat) {
    this.chat = chat;
    return this;
  }
  
  public Chat getChat() {
    return chat;
  }
  
}
