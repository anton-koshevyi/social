package com.social.backend.model;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
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
  
}
