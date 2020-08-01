package com.social.backend.dto.post;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.PostBody;

@Getter
@Setter
public class ContentDto {
  
  @NotNull
  @PostBody
  private String body;
  
}
