package com.social.backend.dto;

import org.springframework.stereotype.Component;

import com.social.backend.dto.reply.CommentDto;
import com.social.backend.model.post.Comment;

@Component
public class CommentMapper implements EntityMapper<Comment, CommentDto> {
  
  @Override
  public CommentDto map(Comment source) {
    if (source == null) {
      return null;
    }
  
    CommentDto dto = new CommentDto();
    dto.setId(source.getId());
    dto.setCreatedAt(source.getCreatedAt());
    dto.setUpdatedAt(source.getUpdatedAt());
    dto.setBody(source.getBody());
    dto.setPost(source.getPost());
    dto.setAuthor(source.getAuthor());
    return dto;
  }
  
}
