package com.social.backend.model;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.social.backend.model.user.User;

@MappedSuperclass
public abstract class Reply {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  
  @Column(name = "created", nullable = false)
  private ZonedDateTime created = ZonedDateTime.now();
  
  @Column(name = "updated")
  private ZonedDateTime updated;
  
  @Column(name = "body", nullable = false)
  private String body;
  
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User author;
  
  public Reply setId(Long id) {
    this.id = id;
    return this;
  }
  
  public Reply setCreated(ZonedDateTime created) {
    this.created = created;
    return this;
  }
  
  public Reply setUpdated(ZonedDateTime updated) {
    this.updated = updated;
    return this;
  }
  
  public Reply setBody(String body) {
    this.body = body;
    return this;
  }
  
  public Reply setAuthor(User author) {
    this.author = author;
    return this;
  }
  
  public Long getId() {
    return id;
  }
  
  public ZonedDateTime getCreated() {
    return created;
  }
  
  public ZonedDateTime getUpdated() {
    return updated;
  }
  
  public String getBody() {
    return body;
  }
  
  public User getAuthor() {
    return author;
  }
  
}
