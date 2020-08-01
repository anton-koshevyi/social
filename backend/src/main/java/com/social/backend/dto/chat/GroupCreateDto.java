package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

public class GroupCreateDto {
  
  @NotNull
  private String name;
  
  @NotNull
  private List<@NotNull Long> memberIds;
  
  public GroupCreateDto setName(String name) {
    this.name = name;
    return this;
  }
  
  public GroupCreateDto setMemberIds(List<Long> memberIds) {
    this.memberIds = memberIds;
    return this;
  }
  
  public String getName() {
    return name;
  }
  
  public List<Long> getMemberIds() {
    return memberIds;
  }
  
}
