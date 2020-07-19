package com.social.backend.dto.user;

public class PasswordDto {
    private String actual;
    private String change;
    private String confirm;
    
    public PasswordDto setActual(String actual) {
        this.actual = actual;
        return this;
    }
    
    public PasswordDto setChange(String change) {
        this.change = change;
        return this;
    }
    
    public PasswordDto setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }
    
    public String getActual() {
        return actual;
    }
    
    public String getChange() {
        return change;
    }
    
    public String getConfirm() {
        return confirm;
    }
}
