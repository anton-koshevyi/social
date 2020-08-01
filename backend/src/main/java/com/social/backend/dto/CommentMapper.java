package com.social.backend.dto;

import java.time.ZonedDateTime;

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
    
    ZonedDateTime updateDate = source.getUpdated();
    CommentDto dto = new CommentDto();
    dto.setId(source.getId());
    dto.setCreationDate(source.getCreated());
    dto.setUpdateDate(updateDate);
    dto.setUpdated(updateDate != null);
    dto.setBody(source.getBody());
    dto.setPost(source.getPost());
    dto.setAuthor(source.getAuthor());
    return dto;
  }
  
}
