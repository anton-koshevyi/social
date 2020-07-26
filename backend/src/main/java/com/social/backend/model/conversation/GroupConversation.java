package com.social.backend.model.conversation;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("group")
public class GroupConversation extends Conversation {
    @Column(name = "name", nullable = false)
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GroupConversation that = (GroupConversation) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
    
    public GroupConversation setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return name;
    }
}
