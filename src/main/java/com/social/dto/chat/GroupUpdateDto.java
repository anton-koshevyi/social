package com.social.dto.chat;

import lombok.Getter;
import lombok.Setter;

import com.social.constraint.GroupChatName;

@Getter
@Setter
public class GroupUpdateDto {

  @GroupChatName
  private String name;

}
