package com.social.backend.dto.post;

public class ContentDto {
    private String body;
    
    public ContentDto setBody(String body) {
        this.body = body;
        return this;
    }
    
    public String getBody() {
        return body;
    }
}
