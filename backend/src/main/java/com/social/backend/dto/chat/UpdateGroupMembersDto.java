package com.social.backend.dto.chat;

import java.util.List;
import javax.validation.constraints.NotNull;

public class UpdateGroupMembersDto {
    @NotNull
    private List<@NotNull Long> memberIds;
    
    public UpdateGroupMembersDto setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
        return this;
    }
    
    public List<Long> getMemberIds() {
        return memberIds;
    }
}
