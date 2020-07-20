package com.social.backend.model.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.social.backend.model.user.User;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "creation_milli", nullable = false)
    private Long creationMilli;
    
    @Column(name = "update_milli")
    private Long updateMilli;
    
    @Column(name = "body", nullable = false)
    private String body;
    
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(creationMilli, post.creationMilli)
                && Objects.equals(updateMilli, post.updateMilli)
                && Objects.equals(body, post.body)
                && Objects.equals(author, post.author);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creationMilli, updateMilli, body, author);
    }
    
    public Post setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Post setCreationMilli(Long creationMilli) {
        this.creationMilli = creationMilli;
        return this;
    }
    
    public Post setUpdateMilli(Long updateMilli) {
        this.updateMilli = updateMilli;
        return this;
    }
    
    public Post setBody(String body) {
        this.body = body;
        return this;
    }
    
    public Post setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }
    
    public Post setAuthor(User author) {
        this.author = author;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getCreationMilli() {
        return creationMilli;
    }
    
    public Long getUpdateMilli() {
        return updateMilli;
    }
    
    public String getBody() {
        return body;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public User getAuthor() {
        return author;
    }
}
