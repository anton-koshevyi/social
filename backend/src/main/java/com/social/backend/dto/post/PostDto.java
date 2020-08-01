package com.social.backend.dto.post;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
public class PostDto {
  
  private Long id;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private String body;
  private Integer comments;
  private User author;
  
}
