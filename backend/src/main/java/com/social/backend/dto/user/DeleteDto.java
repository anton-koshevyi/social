package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import com.social.backend.constraint.Password;

public class DeleteDto {
    @NotNull
    @Password
    private String password;
    
    public DeleteDto setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public String getPassword() {
        return password;
    }
}
