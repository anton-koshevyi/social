package com.social.backend.dto.user;

public class ResponseDto {
    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private Integer publicity;
    private Boolean moder;
    private Boolean admin;
    
    public ResponseDto setId(Long id) {
        this.id = id;
        return this;
    }
    
    public ResponseDto setEmail(String email) {
        this.email = email;
        return this;
    }
    
    public ResponseDto setUsername(String username) {
        this.username = username;
        return this;
    }
    
    public ResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public ResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public ResponseDto setPublicity(Integer publicity) {
        this.publicity = publicity;
        return this;
    }
    
    public ResponseDto setModer(Boolean moder) {
        this.moder = moder;
        return this;
    }
    
    public ResponseDto setAdmin(Boolean admin) {
        this.admin = admin;
        return this;
    }
    
    public Long getId() {
        return id;
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
    
    public Boolean getModer() {
        return moder;
    }
    
    public Boolean getAdmin() {
        return admin;
    }
}
