package com.social.dto.post;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.user.UserDto;

@Getter
@Setter
public class PostDto {

  private Long id;
  private Date createdAt;
  private Date updatedAt;
  private String title;
  private String body;
  private Integer comments;
  private UserDto author;

}
