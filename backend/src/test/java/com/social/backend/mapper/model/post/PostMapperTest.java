package com.social.backend.mapper.model.post;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.social.backend.TestComparator;
import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

public class PostMapperTest {

  private AbstractMapper<Post> mapper;

  @BeforeEach
  public void setUp() {
    AbstractMapper<User> userMapper = Mockito.mock(AbstractMapper.class);
    mapper = new PostMapper(userMapper);

    Mockito
        .when(userMapper.map(
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
        .thenReturn(new UserDto());
  }

  @Test
  public void map() {
    Post post = new Post();
    post.setId(1L);
    post.setCreatedAt(ZonedDateTime.now());
    post.setUpdatedAt(ZonedDateTime.now());
    post.setTitle("title");
    post.setBody("body");
    post.setComments(Collections.emptyList());
    post.setAuthor(new User());

    Assertions
        .assertThat(mapper.map(post, PostDto.class))
        .usingRecursiveComparison()
        .withComparatorForType(TestComparator.notNullFirst(), ZonedDateTime.class)
        .isEqualTo(new PostDto()
            .setId(1L)
            .setTitle("title")
            .setBody("body")
            .setComments(0)
            .setAuthor(new UserDto()));
  }

}

