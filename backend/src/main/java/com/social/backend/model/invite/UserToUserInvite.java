package com.social.backend.model.invite;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.social.backend.model.user.User;

@Entity
@Table(name = "invites_user_to_user")
public class UserToUserInvite extends Invite<User, User> {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User receiver;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;
    
    @Override
    public Invite<User, User> setReceiver(User receiver) {
        this.receiver = receiver;
        return this;
    }
    
    @Override
    public User getReceiver() {
        return receiver;
    }
    
    @Override
    public Invite<User, User> setSender(User sender) {
        this.sender = sender;
        return this;
    }
    
    @Override
    public User getSender() {
        return sender;
    }
    
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
        UserToUserInvite that = (UserToUserInvite) o;
        return Objects.equals(receiver, that.receiver)
                && Objects.equals(sender, that.sender);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), receiver, sender);
    }
}
