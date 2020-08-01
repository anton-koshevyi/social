package com.social.backend.dto;

import java.time.ZonedDateTime;

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
    
    ZonedDateTime updateDate = source.getUpdated();
    PostDto dto = new PostDto();
    dto.setId(source.getId());
    dto.setCreationDate(source.getCreated());
    dto.setUpdateDate(updateDate);
    dto.setUpdated(updateDate != null);
    dto.setBody(source.getBody());
    dto.setComments(source.getComments().size());
    dto.setAuthor(source.getAuthor());
    return dto;
  }
  
}
