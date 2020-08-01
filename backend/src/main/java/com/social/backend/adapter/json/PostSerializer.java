package com.social.backend.adapter.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import com.social.backend.dto.EntityMapper;
import com.social.backend.dto.post.PostDto;
import com.social.backend.model.post.Post;

@JsonComponent
public class PostSerializer extends AbstractSerializer<Post> {
  
  private final EntityMapper<Post, PostDto> entityMapper;
  
  @Autowired
  public PostSerializer(EntityMapper<Post, PostDto> entityMapper) {
    this.entityMapper = entityMapper;
  }
  
  @Override
  public Object beforeSerialize(Post post) {
    return entityMapper.map(post);
  }
  
}
