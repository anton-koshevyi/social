package com.social.backend.dto.reply;

import java.time.ZonedDateTime;

import com.social.backend.model.user.User;

public abstract class ReplyDto {
  
  private Long id;
  private ZonedDateTime creationDate;
  private ZonedDateTime updateDate;
  private Boolean updated;
  private String body;
  private User author;
  
  public ReplyDto setId(Long id) {
    this.id = id;
    return this;
  }
  
  public ReplyDto setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }
  
  public ReplyDto setUpdateDate(ZonedDateTime updateDate) {
    this.updateDate = updateDate;
    return this;
  }
  
  public ReplyDto setUpdated(Boolean updated) {
    this.updated = updated;
    return this;
  }
  
  public ReplyDto setBody(String body) {
    this.body = body;
    return this;
  }
  
  public ReplyDto setAuthor(User author) {
    this.author = author;
    return this;
  }
  
  public Long getId() {
    return id;
  }
  
  public ZonedDateTime getCreationDate() {
    return creationDate;
  }
  
  public ZonedDateTime getUpdateDate() {
    return updateDate;
  }
  
  public Boolean getUpdated() {
    return updated;
  }
  
  public String getBody() {
    return body;
  }
  
  public User getAuthor() {
    return author;
  }
  
}
