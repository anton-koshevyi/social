package com.social.backend.dto.chat;

import javax.validation.constraints.NotNull;

public class UpdateGroupDto {
    @NotNull
    private String name;
    
    public UpdateGroupDto setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return name;
    }
}
