package com.social.backend.dto.post;

import java.time.ZonedDateTime;

import com.social.backend.model.user.User;

public class PostDto {
    private Long id;
    private ZonedDateTime creationDate;
    private ZonedDateTime updateDate;
    private Boolean updated;
    private String body;
    private User author;
    private Integer comments;
    
    public PostDto setId(Long id) {
        this.id = id;
        return this;
    }
    
    public PostDto setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }
    
    public PostDto setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
        return this;
    }
    
    public PostDto setUpdated(Boolean updated) {
        this.updated = updated;
        return this;
    }
    
    public PostDto setBody(String body) {
        this.body = body;
        return this;
    }
    
    public PostDto setAuthor(User author) {
        this.author = author;
        return this;
    }
    
    public PostDto setComments(Integer comments) {
        this.comments = comments;
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
    
    public Integer getComments() {
        return comments;
    }
}
