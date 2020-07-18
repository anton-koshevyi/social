package com.social.backend.model.invite;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "invites")
@Inheritance
@DiscriminatorColumn
public abstract class Invite<R extends Invitable, S extends Invitable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "creation_milli", nullable = false)
    private Long creationMilli;
    
    @ManyToOne
    private R receiver;
    
    @ManyToOne
    private S sender;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invite<?, ?> invite = (Invite<?, ?>) o;
        return Objects.equals(creationMilli, invite.creationMilli)
                && Objects.equals(receiver, invite.receiver)
                && Objects.equals(sender, invite.sender);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creationMilli, receiver, sender);
    }
    
    public Invite<R, S> setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Invite<R, S> setCreationMilli(Long creationMilli) {
        this.creationMilli = creationMilli;
        return this;
    }
    
    public Invite<R, S> setReceiver(R receiver) {
        this.receiver = receiver;
        return this;
    }
    
    public Invite<R, S> setSender(S sender) {
        this.sender = sender;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getCreationMilli() {
        return creationMilli;
    }
    
    public R getReceiver() {
        return receiver;
    }
    
    public S getSender() {
        return sender;
    }
}
