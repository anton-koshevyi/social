package com.social.backend.adapter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.EntityMapper;
import com.social.backend.dto.reply.CommentDto;
import com.social.backend.model.post.Comment;

@JsonComponent
public class CommentSerializer extends AbstractSerializer<Comment> {
  
  private final EntityMapper<Comment, CommentDto> entityMapper;
  
  @Autowired
  public CommentSerializer(EntityMapper<Comment, CommentDto> entityMapper) {
    this.entityMapper = entityMapper;
  }
  
  @Override
  public Object beforeSerialize(Comment comment) {
    return entityMapper.map(comment);
  }
  
}
