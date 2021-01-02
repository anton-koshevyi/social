package com.social.backend.model.chat;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
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
    return super.getMembers().contains(member);
  }
  
  @Transient
  public boolean isOwner(User user) {
    return owner.equals(user);
  }
  
}
