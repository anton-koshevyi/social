package com.social.backend.model.post;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.social.backend.model.Reply;

@Entity
@Table(name = "comments")
public class Comment extends Reply {
  
  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;
  
  public Comment setPost(Post post) {
    this.post = post;
    return this;
  }
  
  public Post getPost() {
    return post;
  }
  
}
