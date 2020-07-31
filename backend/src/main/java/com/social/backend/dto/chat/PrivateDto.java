package com.social.backend.dto.chat;

import java.util.List;

import com.social.backend.model.user.User;

public class PrivateDto extends ChatDto {
    private List<User> members;
    
    public PrivateDto setMembers(List<User> members) {
        this.members = members;
        return this;
    }
    
    public List<User> getMembers() {
        return members;
    }
}
