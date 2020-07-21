package com.social.backend.model.conversation;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(ConversationType.GROUP)
public class GroupConversation extends Conversation {
    @Column(name = "name", nullable = false)
    private String name;
    
    public GroupConversation setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return name;
    }
}
