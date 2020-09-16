package com.social.backend.dto.post;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.dto.user.UserDto;

@Getter
@Setter
public class PostDto {

  private Long id;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private String title;
  private String body;
  private Integer comments;
  private UserDto author;

}
