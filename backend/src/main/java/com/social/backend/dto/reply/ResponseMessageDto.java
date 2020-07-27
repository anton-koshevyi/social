package com.social.backend.dto.reply;

import com.social.backend.model.chat.Chat;

public class ResponseMessageDto extends ResponseDto {
    private Chat chat;
    
    public ResponseMessageDto setChat(Chat chat) {
        this.chat = chat;
        return this;
    }
    
    public Chat getChat() {
        return chat;
    }
}
