package com.social.backend.dto.user;

public class RoleDto {
    private Boolean moder;
    
    public RoleDto setModer(Boolean moder) {
        this.moder = moder;
        return this;
    }
    
    public Boolean getModer() {
        return moder;
    }
}
