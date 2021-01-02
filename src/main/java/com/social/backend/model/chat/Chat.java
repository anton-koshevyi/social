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

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.user.User;

@Getter
@Setter
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
  
}
