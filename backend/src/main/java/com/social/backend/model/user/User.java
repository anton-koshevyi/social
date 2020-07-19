package com.social.backend.model.user;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.social.backend.model.conversation.Conversation;
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
    private Integer publicity;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @ManyToMany(mappedBy = "members")
    private List<Conversation> conversations;
    
    @ManyToOne
    @JoinColumn(name = "instance", referencedColumnName = "id")
    private User instance;
    
    @OneToMany(mappedBy = "instance")
    private List<User> friends;
    
    @OneToMany(mappedBy = "author")
    private List<Post> posts;
    
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;
    
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
                && Objects.equals(username, user.username)
                && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName)
                && Objects.equals(publicity, user.publicity);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email, username, firstName, lastName, publicity);
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
    
    public User setPublicity(Integer publicity) {
        this.publicity = publicity;
        return this;
    }
    
    public User setPassword(String password) {
        this.password = password;
        return this;
    }
    
    public User setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        return this;
    }
    
    public User setInstance(User instance) {
        this.instance = instance;
        return this;
    }
    
    public User setFriends(List<User> friends) {
        this.friends = friends;
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
    
    public Integer getPublicity() {
        return publicity;
    }
    
    public String getPassword() {
        return password;
    }
    
    public List<Conversation> getConversations() {
        return conversations;
    }
    
    public User getInstance() {
        return instance;
    }
    
    public List<User> getFriends() {
        return friends;
    }
    
    public List<Post> getPosts() {
        return posts;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
}
