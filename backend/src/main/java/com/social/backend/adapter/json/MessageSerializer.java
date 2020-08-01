package com.social.backend.adapter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.EntityMapper;
import com.social.backend.dto.reply.MessageDto;
import com.social.backend.model.chat.Message;

@JsonComponent
public class MessageSerializer extends AbstractSerializer<Message> {
  
  private final EntityMapper<Message, MessageDto> entityMapper;
  
  @Autowired
  public MessageSerializer(EntityMapper<Message, MessageDto> entityMapper) {
    this.entityMapper = entityMapper;
  }
  
  @Override
  public Object beforeSerialize(Message message) {
    return entityMapper.map(message);
  }
  
}
