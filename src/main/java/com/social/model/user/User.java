package com.social.model.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import com.social.model.chat.Chat;
import com.social.model.chat.GroupChat;
import com.social.model.chat.Message;
import com.social.model.post.Comment;
import com.social.model.post.Post;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "publicity", nullable = false)
  private int publicity = Publicity.PRIVATE;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "role_moder", nullable = false)
  private boolean moder;

  @Column(name = "role_admin", nullable = false)
  private boolean admin;

  @ManyToMany
  private Set<User> friends = new HashSet<>();

  @ManyToMany(mappedBy = "friends", cascade = CascadeType.REMOVE)
  private Set<User> friendFor = new HashSet<>();

  @ManyToMany(mappedBy = "members")
  private List<Chat> chats = new ArrayList<>();

  @OneToMany(mappedBy = "owner")
  private List<GroupChat> ownedChats = new ArrayList<>();

  @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
  private List<Post> posts = new ArrayList<>();

  @OneToMany(mappedBy = "author")
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "author")
  private List<Message> messages = new ArrayList<>();

  public void addFriend(User user) {
    friends.add(user);
  }

  public void removeFriend(User user) {
    friends.remove(user);
  }

  @Transient
  public boolean hasFriendship(User user) {
    return friends.contains(user);
  }

  @Transient
  public boolean isPublic() {
    return Publicity.PUBLIC == publicity;
  }

  @Transient
  public boolean isInternal() {
    return Publicity.INTERNAL == publicity;
  }

  @Transient
  public boolean isPrivate() {
    return Publicity.PRIVATE == publicity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(email, user.email)
        && Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, username);
  }

}
