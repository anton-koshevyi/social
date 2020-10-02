package com.social.backend.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.comment.CommentType;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.repository.CommentRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class CommentServiceTest {

  private IdentificationContext<Comment> identification;
  private CommentRepositoryStub repository;
  private CommentService service;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new CommentRepositoryStub(identification);
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
    identification.setStrategy(e -> e.setId(1L));

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
    identification.setStrategy(e -> e.setId(1L));

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
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, postAuthor, "Like");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
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
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, author, "Like");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
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
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, author, "Like");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) ModelFactory
        .createModel(CommentType.BADLY)
        .setPost(post)
        .setAuthor(postAuthor));

    service.update(1L, postAuthor, "Like");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "updatedAt")
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) ModelFactory
        .createModel(CommentType.LIKE)
        .setPost(post)
        .setAuthor(postAuthor));

    service.delete(1L, postAuthor);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) ModelFactory
        .createModel(CommentType.LIKE)
        .setPost(post)
        .setAuthor(postAuthor));

    Assertions
        .assertThat(service.findAll(post, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Comment.class), Comment.class)
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
