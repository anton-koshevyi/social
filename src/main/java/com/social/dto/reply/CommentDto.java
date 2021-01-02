package com.social.dto.reply;

import lombok.Getter;
import lombok.Setter;

import com.social.dto.post.PostDto;

@Getter
@Setter
public class CommentDto extends ReplyDto {

  private PostDto post;

}
