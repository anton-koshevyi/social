package com.social.backend.dto;

import org.springframework.stereotype.Component;

import com.social.backend.dto.post.PostDto;
import com.social.backend.model.post.Post;

@Component
public class PostMapper implements EntityMapper<Post, PostDto> {
  
  @Override
  public PostDto map(Post source) {
    if (source == null) {
      return null;
    }
  
    PostDto dto = new PostDto();
    dto.setId(source.getId());
    dto.setCreatedAt(source.getCreatedAt());
    dto.setUpdatedAt(source.getUpdatedAt());
    dto.setTitle(source.getTitle());
    dto.setBody(source.getBody());
    dto.setComments(source.getComments().size());
    dto.setAuthor(source.getAuthor());
    return dto;
  }
  
}
