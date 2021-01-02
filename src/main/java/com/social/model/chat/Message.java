package com.social.model.chat;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.social.model.Reply;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message extends Reply {
  
  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;
  
}
