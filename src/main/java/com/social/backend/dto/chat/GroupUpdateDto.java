package com.social.backend.dto.chat;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.GroupChatName;

@Getter
@Setter
public class GroupUpdateDto {

  @GroupChatName
  private String name;

}
