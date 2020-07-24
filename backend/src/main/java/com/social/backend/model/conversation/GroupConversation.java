package com.social.backend.model.conversation;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.social.backend.model.user.User;

@Entity
@DiscriminatorValue(ConversationType.GROUP)
public class GroupConversation extends Conversation {
    @Column(name = "name", nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    
    @Transient
    public boolean hasMember(User member) {
        Long id = member.getId();
        return super.getMembers().stream()
                .map(User::getId)
                .anyMatch(id::equals);
    }
    
    @Transient
    public boolean isOwner(User user) {
        return Objects.equals(owner.getId(), user.getId());
    }
    
    public GroupConversation setName(String name) {
        this.name = name;
        return this;
    }
    
    public GroupConversation setOwner(User creator) {
        this.owner = creator;
        return this;
    }
    
    public String getName() {
        return name;
    }
    
    public User getOwner() {
        return owner;
    }
}
