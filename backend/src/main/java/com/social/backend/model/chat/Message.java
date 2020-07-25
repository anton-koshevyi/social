package com.social.backend.model.chat;

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
    private ZonedDateTime created = ZonedDateTime.now();
    
    @Column(name = "updated")
    private ZonedDateTime updated;
    
    @Column(name = "body", nullable = false)
    private String body;
    
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    
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
    
    public Message setChat(Chat chat) {
        this.chat = chat;
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
    
    public Chat getChat() {
        return chat;
    }
    
    public User getAuthor() {
        return author;
    }
}
