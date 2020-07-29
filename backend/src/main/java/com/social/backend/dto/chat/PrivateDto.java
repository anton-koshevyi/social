package com.social.backend.dto.chat;

import java.util.List;

import com.social.backend.dto.user.UserDto;

public class PrivateDto extends ChatDto {
    private List<UserDto> members;
    
    public PrivateDto setMembers(List<UserDto> members) {
        this.members = members;
        return this;
    }
    
    public List<UserDto> getMembers() {
        return members;
    }
}
