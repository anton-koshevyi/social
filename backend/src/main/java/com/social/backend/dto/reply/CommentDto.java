package com.social.backend.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.model.post.Post;

@Getter
@Setter
public class CommentDto extends ReplyDto {
  
  private Post post;
  
}
