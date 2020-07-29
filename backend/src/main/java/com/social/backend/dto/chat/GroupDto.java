package com.social.backend.dto.chat;

public class GroupDto extends ChatDto {
    private String name;
    private Integer members;
    
    public GroupDto setName(String name) {
        this.name = name;
        return this;
    }
    
    public GroupDto setMembers(Integer members) {
        this.members = members;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getMembers() {
        return members;
    }
}
