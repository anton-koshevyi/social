package com.social.dto.reply;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.user.UserDto;

@Getter
@Setter
public abstract class ReplyDto {

  private Long id;
  private Date createdAt;
  private Date updatedAt;
  private String body;
  private UserDto author;

}
