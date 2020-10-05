package com.social.backend.service;

import java.util.Optional;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.comment.CommentType;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

  private @Mock CommentRepository repository;
  private CommentService service;

  @BeforeEach
  public void setUp() {
    service = new CommentServiceImpl(repository);
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentNotOfPostAuthor_expectException() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PRIVATE);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "Like"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.privatePost"});
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentNotOfFriend_expectException() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.INTERNAL);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "Like"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.internalPost"});
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PRIVATE);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, postAuthor, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(new Comment()
            .setPost(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setBody("Like")
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.INTERNAL);
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setFriends(Sets.newHashSet(postAuthor));
    postAuthor.setFriends(Sets.newHashSet(author));
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, author, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(new Comment()
            .setPost(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
                    .setPublicity(Publicity.INTERNAL)))
            .setId(1L)
            .setBody("Like")
            .setAuthor(ModelFactory
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)));
  }

  @Test
  public void create_whenPostOfPublicAuthor() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PUBLIC);
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, author, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(new Comment()
            .setPost(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
                    .setPublicity(Publicity.PUBLIC)))
            .setId(1L)
            .setBody("Like")
            .setAuthor(ModelFactory
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.update(0L, author, "Like"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.findByIdAndAuthor(1L, postAuthor))
        .thenReturn(Optional.of((Comment) ModelFactory
            .createModel(CommentType.BADLY)
            .setPost(post)
            .setId(1L)
            .setBody("Badly")
            .setAuthor(postAuthor)));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(1L, postAuthor, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(
            NotNullComparator.leftNotNull(), "createdAt", "updatedAt")
        .isEqualTo(ModelFactory
            .createModel(CommentType.BADLY)
            .setPost(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setBody("Like")
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Comment entity = (Comment) ModelFactory
        .createModel(CommentType.LIKE)
        .setPost(post)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.findByIdAndAuthor(1L, postAuthor))
        .thenReturn(Optional.of(entity));

    service.delete(1L, postAuthor);

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void findAll_byPost() {
    User postAuthor = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = ModelFactory
        .createModel(PostType.READING)
        .setId(1L)
        .setAuthor(postAuthor);
    Mockito
        .when(repository.findAllByPost(post, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList((Comment) ModelFactory
                .createModel(CommentType.LIKE)
                .setPost(post)
                .setId(1L)
                .setAuthor(postAuthor))
        ));

    Assertions
        .assertThat(service.findAll(post, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForElementFieldsWithNames(
            NotNullComparator.leftNotNull(), "createdAt")
        .containsExactly((Comment) ModelFactory
            .createModel(CommentType.LIKE)
            .setPost(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(ModelFactory
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

}
