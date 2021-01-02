package com.social.backend.dto.reply;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.ReplyBody;

@Getter
@Setter
public class ContentDto {

  @NotNull(groups = CreateGroup.class)
  @ReplyBody(groups = {CreateGroup.class, UpdateGroup.class})
  private String body;

  public interface CreateGroup {
  }

  public interface UpdateGroup {
  }

}
