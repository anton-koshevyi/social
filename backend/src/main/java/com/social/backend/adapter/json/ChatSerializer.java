package com.social.backend.adapter.json;

import java.util.ArrayList;

import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.chat.ChatDto;
import com.social.backend.dto.chat.GroupDto;
import com.social.backend.dto.chat.PrivateDto;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;

@JsonComponent
public class ChatSerializer
        extends AbstractSerializer<Chat>
        implements EntityMapper<Chat, ChatDto> {
    @Override
    public Object beforeSerialize(Chat chat) {
        return this.map(chat);
    }
    
    @Override
    public ChatDto map(Chat source) {
        if (source instanceof PrivateChat) {
            PrivateDto dto = new PrivateDto();
            dto.setId(source.getId());
            dto.setType("private");
            dto.setMembers(new ArrayList<>(source.getMembers()));
            return dto;
        }
        
        if (source instanceof GroupChat) {
            GroupChat groupChat = (GroupChat) source;
            GroupDto dto = new GroupDto();
            dto.setId(source.getId());
            dto.setType("group");
            dto.setName(groupChat.getName());
            dto.setMembers(source.getMembers().size());
            dto.setOwner(groupChat.getOwner());
            return dto;
        }
        
        return null;
    }
}
