package com.social.backend.dto.reply;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.ReplyBody;

@Getter
@Setter
public class ContentDto {
  
  @NotNull
  @ReplyBody
  private String body;
  
}
