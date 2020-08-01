package com.social.backend.dto.user;

import javax.validation.constraints.NotNull;

import com.social.backend.constraint.Email;
import com.social.backend.constraint.FirstName;
import com.social.backend.constraint.LastName;
import com.social.backend.constraint.Publicity;
import com.social.backend.constraint.Username;

public class UpdateDto {
  
  @NotNull
  @Email
  private String email;
  
  @NotNull
  @Username
  private String username;
  
  @NotNull
  @FirstName
  private String firstName;
  
  @NotNull
  @LastName
  private String lastName;
  
  @NotNull
  @Publicity
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
