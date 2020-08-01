package com.social.backend.dto.reply;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
public abstract class ReplyDto {
  
  private Long id;
  private ZonedDateTime creationDate;
  private ZonedDateTime updateDate;
  private Boolean updated;
  private String body;
  private User author;
  
}
