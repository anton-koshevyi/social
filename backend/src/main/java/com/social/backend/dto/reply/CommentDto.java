package com.social.backend.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.backend.dto.post.PostDto;

@Getter
@Setter
public class CommentDto extends ReplyDto {

  private PostDto post;

}
