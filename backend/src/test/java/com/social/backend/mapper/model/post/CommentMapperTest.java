package com.social.backend.mapper.model.post;

import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.social.backend.TestComparator;
import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.reply.CommentDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public class CommentMapperTest {

  private AbstractMapper<Comment> mapper;

  @BeforeEach
  public void setUp() {
    AbstractMapper<User> userMapper = Mockito.mock(AbstractMapper.class);
    AbstractMapper<Post> postMapper = Mockito.mock(AbstractMapper.class);
    mapper = new CommentMapper(userMapper, postMapper);

    Mockito
        .when(userMapper.map(
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
        .thenReturn(new UserDto());
    Mockito
        .when(postMapper.map(
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
        .thenReturn(new PostDto());
  }

  @Test
  public void map() {
    Comment comment = new Comment();
    comment.setId(1L);
    comment.setCreatedAt(ZonedDateTime.now());
    comment.setUpdatedAt(ZonedDateTime.now());
    comment.setBody("body");
    comment.setAuthor(new User());
    comment.setPost(new Post());

    Assertions
        .assertThat(mapper.map(comment, CommentDto.class))
        .usingRecursiveComparison()
        .withComparatorForType(TestComparator.notNullFirst(), ZonedDateTime.class)
        .isEqualTo(new CommentDto()
            .setPost(new PostDto())
            .setId(1L)
            .setBody("body")
            .setAuthor(new UserDto()));
  }

}

