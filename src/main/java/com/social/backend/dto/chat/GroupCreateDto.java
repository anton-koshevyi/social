package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.constraint.GroupChatName;

@Getter
@Setter
public class GroupCreateDto {
  
  @NotNull
  @GroupChatName
  private String name;
  
  @NotNull
  private List<@NotNull Long> members;
  
}
