package com.social.backend.dto.reply;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
public abstract class ReplyDto {
  
  private Long id;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private String body;
  private User author;
  
}
