package com.social.model.post;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.social.model.user.User;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  
  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();
  
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;
  
  @Column(name = "title", nullable = false)
  private String title;
  
  @Column(name = "body", nullable = false)
  private String body;
  
  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
  private List<Comment> comments = new ArrayList<>();
  
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User author;
  
}
