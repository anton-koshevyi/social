package com.social.backend.dto.reply;

import com.social.backend.model.post.Post;

public class CommentDto extends ReplyDto {
  
  private Post post;
  
  public CommentDto setPost(Post post) {
    this.post = post;
    return this;
  }
  
  public Post getPost() {
    return post;
  }
  
}
