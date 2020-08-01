package com.social.backend.dto;

import org.springframework.stereotype.Component;

import com.social.backend.dto.reply.MessageDto;
import com.social.backend.model.chat.Message;

@Component
public class MessageMapper implements EntityMapper<Message, MessageDto> {
  
  @Override
  public MessageDto map(Message source) {
    if (source == null) {
      return null;
    }
  
    MessageDto dto = new MessageDto();
    dto.setId(source.getId());
    dto.setCreatedAt(source.getCreatedAt());
    dto.setUpdatedAt(source.getUpdatedAt());
    dto.setBody(source.getBody());
    dto.setChat(source.getChat());
    dto.setAuthor(source.getAuthor());
    return dto;
  }
  
}
