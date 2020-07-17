package com.social.backend.model.conversation;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.social.backend.model.user.User;

@Entity(name = "conversations")
@Inheritance
@DiscriminatorColumn(name = "type")
public abstract class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "conversation_id"))
    private List<User> members;
    
    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Conversation that = (Conversation) o;
        return Objects.equals(members, that.members)
                && Objects.equals(messages, that.messages);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(members, messages);
    }
    
    public Conversation setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Conversation setMembers(List<User> members) {
        this.members = members;
        return this;
    }
    
    public Conversation setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public List<User> getMembers() {
        return members;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
}
