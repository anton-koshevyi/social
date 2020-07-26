package com.social.backend.model.conversation;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "creation_milli", nullable = false)
    private Long creationMilli;
    
    @Column(name = "body", nullable = false)
    private String body;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(creationMilli, message.creationMilli)
                && Objects.equals(body, message.body)
                && Objects.equals(conversation, message.conversation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creationMilli, body, conversation);
    }
    
    public Message setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Message setCreationMilli(Long creationMilli) {
        this.creationMilli = creationMilli;
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
    
    public Long getId() {
        return id;
    }
    
    public Long getCreationMilli() {
        return creationMilli;
    }
    
    public String getBody() {
        return body;
    }
    
    public Conversation getConversation() {
        return conversation;
    }
}
