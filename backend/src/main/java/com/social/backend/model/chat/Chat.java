package com.social.backend.model.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.Table;

import com.social.backend.model.user.User;

@Entity
@Table(name = "chats")
@Inheritance
@DiscriminatorColumn
public abstract class Chat {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  
  @ManyToMany
  @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "chat_id"))
  private Set<User> members = new HashSet<>();
  
  @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
  private List<Message> messages = new ArrayList<>();
  
  public Chat setId(Long id) {
    this.id = id;
    return this;
  }
  
  public Chat setMembers(Set<User> members) {
    this.members = members;
    return this;
  }
  
  public Chat setMessages(List<Message> messages) {
    this.messages = messages;
    return this;
  }
  
  public Long getId() {
    return id;
  }
  
  public Set<User> getMembers() {
    return members;
  }
  
  public List<Message> getMessages() {
    return messages;
  }
  
}
