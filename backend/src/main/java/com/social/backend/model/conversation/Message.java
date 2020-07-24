package com.social.backend.model.conversation;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.social.backend.model.user.User;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;
    
    @Column(name = "updated")
    private ZonedDateTime updated;
    
    @Column(name = "body", nullable = false)
    private String body;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    
    public Message setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Message setCreated(ZonedDateTime created) {
        this.created = created;
        return this;
    }
    
    public Message setUpdated(ZonedDateTime updated) {
        this.updated = updated;
        return this;
    }
    
    public Message setBody(String body) {
        this.body = body;
        return this;
    }
    
    public Message setConversation(Conversation conversation) {
        this.conversation = conversation;
        return this;
    }
    
    public Message setAuthor(User author) {
        this.author = author;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public ZonedDateTime getCreated() {
        return created;
    }
    
    public ZonedDateTime getUpdated() {
        return updated;
    }
    
    public String getBody() {
        return body;
    }
    
    public Conversation getConversation() {
        return conversation;
    }
    
    public User getAuthor() {
        return author;
    }
}
