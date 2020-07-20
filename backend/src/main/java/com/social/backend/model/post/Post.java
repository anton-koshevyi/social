package com.social.backend.model.post;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
    
    @Column(name = "created", nullable = false)
    private ZonedDateTime created;
    
    @Column(name = "updated")
    private ZonedDateTime updated;
    
    @Column(name = "body", nullable = false)
    private String body;
    
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    
    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) {
    //         return true;
    //     }
    //     if (o == null || getClass() != o.getClass()) {
    //         return false;
    //     }
    //     Post post = (Post) o;
    //     return Objects.equals(created, post.created)
    //             && Objects.equals(updated, post.updated)
    //             && Objects.equals(body, post.body)
    //             && Objects.equals(comments, post.comments)
    //             && Objects.equals(author, post.author);
    // }
    
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", created=" + created +
                ", updated=" + updated +
                ", body='" + body + '\'' +
                ", comments=" + comments +
                ", author=" + author +
                '}';
    }
    
    // @Override
    // public int hashCode() {
    //     return Objects.hash(created, updated, body,comments, author);
    // }
    
    public Post setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Post setCreated(ZonedDateTime created) {
        this.created = created;
        return this;
    }
    
    public Post setUpdated(ZonedDateTime updated) {
        this.updated = updated;
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
    
    public ZonedDateTime getCreated() {
        return created;
    }
    
    public ZonedDateTime getUpdated() {
        return updated;
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
