package com.social.backend.dto.chat;

public class ResponseGroupDto {
    private Long id;
    private String name;
    private Integer members;
    
    public ResponseGroupDto setId(Long id) {
        this.id = id;
        return this;
    }
    
    public ResponseGroupDto setName(String name) {
        this.name = name;
        return this;
    }
    
    public ResponseGroupDto setMembers(Integer members) {
        this.members = members;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getMembers() {
        return members;
    }
}
