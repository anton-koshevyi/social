package com.social.backend.model.invite;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Invite<R extends Invitable, S extends Invitable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "creation_milli", nullable = false)
    private Long creationMilli;
    
    public abstract Invite<R, S> setReceiver(R receiver);
    
    public abstract R getReceiver();
    
    public abstract Invite<R, S> setSender(S sender);
    
    public abstract S getSender();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invite<?, ?> invite = (Invite<?, ?>) o;
        return Objects.equals(creationMilli, invite.creationMilli);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creationMilli);
    }
    
    public Invite<R, S> setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Invite<R, S> setCreationMilli(Long creationMilli) {
        this.creationMilli = creationMilli;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getCreationMilli() {
        return creationMilli;
    }
}
