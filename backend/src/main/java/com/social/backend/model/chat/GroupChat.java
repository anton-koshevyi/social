package com.social.backend.model.chat;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.social.backend.model.user.User;

@Entity
@DiscriminatorValue("group")
public class GroupChat extends Chat {
    @Column(name = "name")
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
    
    public GroupChat setName(String name) {
        this.name = name;
        return this;
    }
    
    public GroupChat setOwner(User creator) {
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
