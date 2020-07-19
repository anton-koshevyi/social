package com.social.backend.dto.user;

public class UpdateDto {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private Integer publicity;
    
    public UpdateDto setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UpdateDto setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UpdateDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public UpdateDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UpdateDto setPublicity(Integer publicity) {
        this.publicity = publicity;
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
    
    public Integer getPublicity() {
        return publicity;
    }
}
