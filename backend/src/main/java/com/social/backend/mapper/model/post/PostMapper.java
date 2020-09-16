package com.social.backend.mapper.model.post;

import java.util.Objects;

import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public class PostMapper extends AbstractMapper<Post> {

  private final AbstractMapper<User> userMapper;

  public PostMapper(AbstractMapper<User> userMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    this.userMapper = userMapper;
  }

  @Override
  public <R> R toDto(Post model, Class<R> dtoType) {
    logger.debug("Mapping Post to PostDto by default");
    PostDto dto = new PostDto();
    dto.setId(model.getId());
    dto.setCreatedAt(model.getCreatedAt());
    dto.setUpdatedAt(model.getUpdatedAt());
    dto.setTitle(model.getTitle());
    dto.setBody(model.getBody());
    dto.setComments(model.getComments().size());
    dto.setAuthor(userMapper.toDto(model.getAuthor(), UserDto.class));
    return (R) dto;
  }

}
