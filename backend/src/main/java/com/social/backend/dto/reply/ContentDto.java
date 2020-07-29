package com.social.backend.dto.reply;

import javax.validation.constraints.NotNull;

import com.social.backend.constraint.ReplyBody;

public class ContentDto {
    @NotNull
    @ReplyBody
    private String body;
    
    public ContentDto setBody(String body) {
        this.body = body;
        return this;
    }
    
    public String getBody() {
        return body;
    }
}
