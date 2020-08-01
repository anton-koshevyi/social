package com.social.backend.dto.chat;

import com.social.backend.model.user.User;

public class GroupDto extends ChatDto {
  
  private String name;
  private Integer members;
  private User owner;
  
  public GroupDto setName(String name) {
    this.name = name;
    return this;
  }
  
  public GroupDto setMembers(Integer members) {
    this.members = members;
    return this;
  }
  
  public GroupDto setOwner(User owner) {
    this.owner = owner;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  public Integer getMembers() {
    return members;
  }
  
  public User getOwner() {
    return owner;
  }
  
}
