package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMembersDto {
  
  @NotNull
  private List<@NotNull Long> members;
  
}
