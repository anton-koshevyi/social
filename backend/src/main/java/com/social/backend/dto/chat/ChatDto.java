package com.social.backend.dto.chat;

public abstract class ChatDto {
  
  private Long id;
  private String type;
  
  public ChatDto setId(Long id) {
    this.id = id;
    return this;
  }
  
  public ChatDto setType(String type) {
    this.type = type;
    return this;
  }
  
  public Long getId() {
    return id;
  }
  
  public String getType() {
    return type;
  }
  
}
