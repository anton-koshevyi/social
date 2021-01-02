package com.social.dto.post;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.constraint.PostBody;
import com.social.constraint.PostTitle;

@Getter
@Setter
public class ContentDto {

  @NotNull(groups = CreateGroup.class)
  @PostTitle(groups = {CreateGroup.class, UpdateGroup.class})
  private String title;

  @NotNull(groups = CreateGroup.class)
  @PostBody(groups = {CreateGroup.class, UpdateGroup.class})
  private String body;

  public interface CreateGroup {
  }

  public interface UpdateGroup {
  }

}
