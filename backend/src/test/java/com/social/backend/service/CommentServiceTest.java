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
import com.social.backend.test.TestEntity;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactoryProducer;
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
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PRIVATE);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.privatePost"});
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentNotOfFriend_expectException() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.INTERNAL);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.internalPost"});
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PRIVATE);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, postAuthor, "body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setBody("body")
            .setAuthor(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.INTERNAL);
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setFriends(Sets.newHashSet(postAuthor));
    postAuthor.setFriends(Sets.newHashSet(author));
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, author, "body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
                    .setPublicity(Publicity.INTERNAL)))
            .setId(1L)
            .setBody("body")
            .setAuthor(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)));
  }

  @Test
  public void create_whenPostOfPublicAuthor() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L)
        .setPublicity(Publicity.PUBLIC);
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));

    service.create(post, author, "body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
                    .setPublicity(Publicity.PUBLIC)))
            .setId(1L)
            .setBody("body")
            .setAuthor(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L)));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.update(0L, author, "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) new Comment()
        .setPost(post)
        .setBody("comment body")
        .setAuthor(postAuthor));

    service.update(1L, postAuthor, "new body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "updatedAt")
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setBody("new body")
            .setAuthor(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(postAuthor));

    service.delete(1L, postAuthor);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void findAll_byPost() {
    User postAuthor = ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(postAuthor);
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(postAuthor));

    Assertions
        .assertThat(service.findAll(post, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Comment.class), Comment.class)
        .containsExactly((Comment) TestEntity
            .comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)))
            .setId(1L)
            .setAuthor(ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

}
