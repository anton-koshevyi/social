package com.social.backend.dto;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.social.backend.dto.chat.ChatDto;
import com.social.backend.dto.chat.GroupDto;
import com.social.backend.dto.chat.PrivateDto;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;

@Component
public class ChatMapper implements EntityMapper<Chat, ChatDto> {
  
  private static final String TYPE_PRIVATE = "private";
  private static final String TYPE_GROUP = "group";
  
  @Override
  public ChatDto map(Chat source) {
    if (source instanceof PrivateChat) {
      PrivateDto dto = new PrivateDto();
      dto.setId(source.getId());
      dto.setType(TYPE_PRIVATE);
      dto.setMembers(new ArrayList<>(source.getMembers()));
      return dto;
    }
    
    if (source instanceof GroupChat) {
      GroupChat groupChat = (GroupChat) source;
      GroupDto dto = new GroupDto();
      dto.setId(source.getId());
      dto.setType(TYPE_GROUP);
      dto.setName(groupChat.getName());
      dto.setMembers(source.getMembers().size());
      dto.setOwner(groupChat.getOwner());
      return dto;
    }
    
    return null;
  }
  
}
