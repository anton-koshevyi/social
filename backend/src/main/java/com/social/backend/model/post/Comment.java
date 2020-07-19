package com.social.backend.model.post;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.social.backend.model.user.User;

@Entity
@Table(name = "comments")
public class Comment {
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
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
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
        Comment comment = (Comment) o;
        return Objects.equals(creationMilli, comment.creationMilli)
                && Objects.equals(updateMilli, comment.updateMilli)
                && Objects.equals(body, comment.body)
                && Objects.equals(post, comment.post)
                && Objects.equals(author, comment.author);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creationMilli, updateMilli, body, post, author);
    }
    
    public Comment setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Comment setCreationMilli(Long creationMilli) {
        this.creationMilli = creationMilli;
        return this;
    }
    
    public Comment setUpdateMilli(Long updateMilli) {
        this.updateMilli = updateMilli;
        return this;
    }
    
    public Comment setBody(String body) {
        this.body = body;
        return this;
    }
    
    public Comment setPost(Post post) {
        this.post = post;
        return this;
    }
    
    public Comment setAuthor(User author) {
        this.author = author;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getCreationMilli() {
        return creationMilli;
    }
    
    public String getBody() {
        return body;
    }
    
    public Post getPost() {
        return post;
    }
    
    public User getAuthor() {
        return author;
    }
}
