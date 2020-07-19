package com.social.backend.dto.user;

public class DeleteDto {
    private String password;
    
    public DeleteDto setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public String getPassword() {
        return password;
    }
}
