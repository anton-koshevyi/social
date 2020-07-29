package com.social.backend.dto.chat;

public abstract class ChatDto {
    private Long id;
    
    public ChatDto setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Long getId() {
        return id;
    }
}
