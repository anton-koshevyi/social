package com.social.backend.dto.user;

public class CreateDto {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String confirm;
    
    public CreateDto setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public CreateDto setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public CreateDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public CreateDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public CreateDto setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public CreateDto setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getConfirm() {
        return confirm;
    }
}
