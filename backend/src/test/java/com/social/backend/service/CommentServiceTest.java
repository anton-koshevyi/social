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
import com.social.backend.repository.PostRepository;
import com.social.backend.repository.UserRepository;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;
import com.social.backend.test.stub.repository.CommentRepositoryStub;
import com.social.backend.test.stub.repository.PostRepositoryStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class CommentServiceTest {

  private IdentificationContext<Comment> commentIdentification;
  private IdentificationContext<Post> postIdentification;
  private IdentificationContext<User> userIdentification;
  private CommentRepositoryStub commentRepository;
  private PostRepository postRepository;
  private UserRepository userRepository;
  private CommentService commentService;

  @BeforeEach
  public void setUp() {
    commentIdentification = new IdentificationContext<>();
    postIdentification = new IdentificationContext<>();
    userIdentification = new IdentificationContext<>();
    commentRepository = new CommentRepositoryStub(commentIdentification);
    postRepository = new PostRepositoryStub(postIdentification);
    userRepository = new UserRepositoryStub(userIdentification);

    commentService = new CommentServiceImpl(commentRepository);
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentNotOfPostAuthor_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity
        .user()
        .setPublicity(Publicity.PRIVATE));
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User author = userRepository.save(TestEntity
        .user()
        .setEmail("commentAuthor@mail.com")
        .setUsername("commentAuthor"));
    commentIdentification.setStrategy(entity -> entity.setId(1L));

    Assertions
        .assertThatThrownBy(() -> commentService.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.privatePost"});
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentNotOfFriend_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity
        .user()
        .setPublicity(Publicity.INTERNAL));
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User author = userRepository.save(TestEntity
        .user()
        .setEmail("commentAuthor@mail.com")
        .setUsername("commentAuthor"));
    commentIdentification.setStrategy(entity -> entity.setId(1L));

    Assertions
        .assertThatThrownBy(() -> commentService.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.internalPost"});
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity
        .user()
        .setPublicity(Publicity.PRIVATE));
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));

    commentService.create(post, postAuthor, "body");

    Assertions
        .assertThat(commentRepository.find(1L))
        .usingComparator(TestComparator
            .commentComparator())
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(TestEntity
                    .user()
                    .setId(1L)))
            .setId(1L)
            .setBody("body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity
        .user()
        .setEmail("postAuthor@mail.com")
        .setUsername("postAuthor")
        .setPublicity(Publicity.INTERNAL));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User author = userRepository.save(TestEntity
        .user()
        .setEmail("author@mail.com")
        .setUsername("author"));
    postAuthor.setFriends(Sets.newHashSet(author));
    author.setFriends(Sets.newHashSet(postAuthor));
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));

    commentService.create(post, author, "body");

    Assertions
        .assertThat(commentRepository.find(1L))
        .usingComparator(TestComparator
            .commentComparator())
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(TestEntity
                    .user()
                    .setId(1L)
                    .setEmail("postAuthor@mail.com")
                    .setUsername("postAuthor")
                    .setPublicity(Publicity.INTERNAL)))
            .setId(1L)
            .setBody("body")
            .setAuthor(TestEntity
                .user()
                .setId(2L)
                .setEmail("author@mail.com")
                .setUsername("author")));
  }

  @Test
  public void create_whenPostOfPublicAuthor() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity
        .user()
        .setEmail("postAuthor@mail.com")
        .setUsername("postAuthor")
        .setPublicity(Publicity.PUBLIC));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User author = userRepository.save(TestEntity
        .user()
        .setEmail("author@mail.com")
        .setUsername("author"));
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));

    commentService.create(post, author, "body");

    Assertions
        .assertThat(commentRepository.find(1L))
        .usingComparator(TestComparator
            .commentComparator())
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(TestEntity
                    .user()
                    .setId(1L)
                    .setEmail("postAuthor@mail.com")
                    .setUsername("postAuthor")
                    .setPublicity(Publicity.PUBLIC)))
            .setId(1L)
            .setBody("body")
            .setAuthor(TestEntity
                .user()
                .setId(2L)
                .setEmail("author@mail.com")
                .setUsername("author")));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User author = userRepository.save(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> commentService.update(0L, author, "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity.user());
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));
    commentRepository.save((Comment) new Comment()
        .setPost(post)
        .setBody("comment body")
        .setAuthor(postAuthor));

    commentService.update(1L, postAuthor, "new body");

    Assertions
        .assertThat(commentRepository.find(1L))
        .usingComparator(TestComparator
            .commentComparator())
        .usingComparatorForFields(TestComparator
            .notNullFirst(), "updated")
        .isEqualTo(new Comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(TestEntity
                    .user()
                    .setId(1L)))
            .setId(1L)
            .setBody("new body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User author = userRepository.save(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> commentService.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity.user());
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));
    commentRepository.save((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(postAuthor));

    commentService.delete(1L, postAuthor);

    Assertions
        .assertThat(commentRepository.find(1L))
        .isNull();
  }

  @Test
  public void findAll_byPost() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User postAuthor = userRepository.save(TestEntity.user());
    postIdentification.setStrategy(entity -> entity.setId(1L));
    Post post = postRepository.save(TestEntity
        .post()
        .setAuthor(postAuthor));
    commentIdentification.setStrategy(entity -> entity.setId(1L));
    commentRepository.save((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(postAuthor));

    Assertions
        .assertThat(commentService.findAll(post, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .commentComparator(), Comment.class)
        .containsExactly((Comment) TestEntity
            .comment()
            .setPost(TestEntity
                .post()
                .setId(1L)
                .setAuthor(TestEntity
                    .user()
                    .setId(1L)))
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

}
