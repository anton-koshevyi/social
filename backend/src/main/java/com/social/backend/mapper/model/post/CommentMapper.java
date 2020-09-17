package com.social.backend.mapper.model.post;

import java.util.Objects;

import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.reply.CommentDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public class CommentMapper extends AbstractMapper<Comment> {

  private final AbstractMapper<User> userMapper;
  private final AbstractMapper<Post> postMapper;

  public CommentMapper(AbstractMapper<User> userMapper, AbstractMapper<Post> postMapper) {
    Objects.requireNonNull(userMapper, "User mapper must not be null");
    Objects.requireNonNull(postMapper, "Chat mapper must not be null");
    this.userMapper = userMapper;
    this.postMapper = postMapper;
  }

  @Override
  public <R> R map(Comment model, Class<R> dtoType) {
    logger.debug("Mapping Comment to CommentDto by default");
    CommentDto dto = new CommentDto();
    dto.setId(model.getId());
    dto.setCreatedAt(model.getCreatedAt());
    dto.setUpdatedAt(model.getUpdatedAt());
    dto.setBody(model.getBody());
    dto.setAuthor(userMapper.map(model.getAuthor(), UserDto.class));
    dto.setPost(postMapper.map(model.getPost(), PostDto.class));
    return (R) dto;
  }

}
