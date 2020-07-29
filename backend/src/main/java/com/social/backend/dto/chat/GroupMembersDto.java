package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

public class GroupMembersDto {
    @NotNull
    private List<@NotNull Long> memberIds;
    
    public GroupMembersDto setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
        return this;
    }
    
    public List<Long> getMemberIds() {
        return memberIds;
    }
}
