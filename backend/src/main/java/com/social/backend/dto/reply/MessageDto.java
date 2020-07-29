package com.social.backend.dto.reply;

import com.social.backend.dto.chat.ChatDto;

public class MessageDto extends ReplyDto {
    private ChatDto chat;
    
    public MessageDto setChat(ChatDto chat) {
        this.chat = chat;
        return this;
    }
    
    public ChatDto getChat() {
        return chat;
    }
}
