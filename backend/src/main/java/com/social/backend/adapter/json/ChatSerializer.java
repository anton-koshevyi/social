package com.social.backend.adapter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.EntityMapper;
import com.social.backend.dto.chat.ChatDto;
import com.social.backend.model.chat.Chat;

@JsonComponent
public class ChatSerializer extends AbstractSerializer<Chat> {
  
  private final EntityMapper<Chat, ChatDto> entityMapper;
  
  @Autowired
  public ChatSerializer(EntityMapper<Chat, ChatDto> entityMapper) {
    this.entityMapper = entityMapper;
  }
  
  @Override
  public Object beforeSerialize(Chat chat) {
    return entityMapper.map(chat);
  }
  
}
