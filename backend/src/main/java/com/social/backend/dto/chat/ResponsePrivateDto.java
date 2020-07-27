package com.social.backend.dto.chat;

import java.util.List;

import com.social.backend.model.user.User;

public class ResponsePrivateDto {
    private Long id;
    private List<User> members;
    
    public ResponsePrivateDto setId(Long id) {
        this.id = id;
        return this;
    }
    
    public ResponsePrivateDto setMembers(List<User> members) {
        this.members = members;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public List<User> getMembers() {
        return members;
    }
}
