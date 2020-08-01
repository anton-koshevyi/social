package com.social.backend.model.user;

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

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;

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
  
  public User setId(Long id) {
    this.id = id;
    return this;
  }
  
  public User setEmail(String email) {
    this.email = email;
    return this;
  }
  
  public User setUsername(String username) {
    this.username = username;
    return this;
  }
  
  public User setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }
  
  public User setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }
  
  public User setPublicity(int publicity) {
    this.publicity = publicity;
    return this;
  }
  
  public User setPassword(String password) {
    this.password = password;
    return this;
  }
  
  public User setModer(boolean moder) {
    this.moder = moder;
    return this;
  }
  
  public User setAdmin(boolean admin) {
    this.admin = admin;
    return this;
  }
  
  public User setFriends(Set<User> friends) {
    this.friends = friends;
    return this;
  }
  
  public User setFriendFor(Set<User> friendOf) {
    this.friendFor = friendOf;
    return this;
  }
  
  public User setChats(List<Chat> chats) {
    this.chats = chats;
    return this;
  }
  
  public User setOwnedChats(List<GroupChat> ownedChats) {
    this.ownedChats = ownedChats;
    return this;
  }
  
  public User setPosts(List<Post> posts) {
    this.posts = posts;
    return this;
  }
  
  public User setComments(List<Comment> comments) {
    this.comments = comments;
    return this;
  }
  
  public User setMessages(List<Message> messages) {
    this.messages = messages;
    return this;
  }
  
  public Long getId() {
    return id;
  }
  
  public String getEmail() {
    return email;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public int getPublicity() {
    return publicity;
  }
  
  public String getPassword() {
    return password;
  }
  
  public boolean isModer() {
    return moder;
  }
  
  public boolean isAdmin() {
    return admin;
  }
  
  public Set<User> getFriends() {
    return friends;
  }
  
  public Set<User> getFriendFor() {
    return friendFor;
  }
  
  public List<Chat> getChats() {
    return chats;
  }
  
  public List<GroupChat> getOwnedChats() {
    return ownedChats;
  }
  
  public List<Post> getPosts() {
    return posts;
  }
  
  public List<Comment> getComments() {
    return comments;
  }
  
  public List<Message> getMessages() {
    return messages;
  }
  
}
