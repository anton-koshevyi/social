package com.social.backend.mapper.model.post;

import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
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

  private AbstractMapper<Comment> mapper = new CommentMapper(
      Mockito.mock(AbstractMapper.class, inv -> new UserDto()),
      Mockito.mock(AbstractMapper.class, inv -> new PostDto())
  );

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
