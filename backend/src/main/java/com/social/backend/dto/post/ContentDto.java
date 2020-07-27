package com.social.backend.dto.post;

import javax.validation.constraints.NotNull;

import com.social.backend.constraint.PostBody;

public class ContentDto {
    @NotNull
    @PostBody
    private String body;
    
    public ContentDto setBody(String body) {
        this.body = body;
        return this;
    }
    
    public String getBody() {
        return body;
    }
}
