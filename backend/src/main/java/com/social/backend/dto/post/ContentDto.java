package com.social.backend.dto.post;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.PostBody;
import com.social.backend.constraint.PostTitle;

@Getter
@Setter
public class ContentDto {
  
  @NotNull
  @PostTitle
  private String title;
  
  @NotNull
  @PostBody
  private String body;
  
}
