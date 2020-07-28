package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

public class CreateGroupDto {
    @NotNull
    private String name;
    
    @NotNull
    private List<@NotNull Long> memberIds;
    
    public CreateGroupDto setName(String name) {
        this.name = name;
        return this;
    }
    
    public CreateGroupDto setMemberIds(List<Long> memberIds) {
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
