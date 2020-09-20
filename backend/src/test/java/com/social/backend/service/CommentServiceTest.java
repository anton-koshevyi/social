package com.social.backend.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepositoryImpl;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import({CommentServiceImpl.class, CommentRepositoryImpl.class})
public class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void create_exception_whenPostOfPrivateAuthor_andCommentNotOfPostAuthor() {
    User postAuthor = entityManager.persist(TestEntity
        .user()
        .setPublicity(Publicity.PRIVATE));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));
    User author = entityManager.persist(TestEntity
        .user()
        .setEmail("commentAuthor@mail.com")
        .setUsername("commentAuthor"));

    Assertions
        .assertThatThrownBy(() -> commentService.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.comment.privatePost"});
  }

  @Test
  public void create_exception_whenPostOfInternalAuthor_andCommentNotOfFriend() {
    User postAuthor = entityManager.persist(TestEntity
        .user()
        .setPublicity(Publicity.INTERNAL));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));
    User author = entityManager.persist(TestEntity
        .user()
        .setEmail("author@mail.com")
        .setUsername("author"));

    Assertions
        .assertThatThrownBy(() -> commentService.create(post, author, "body"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.internalPost"});
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
    User postAuthor = entityManager.persist(TestEntity
        .user()
        .setPublicity(Publicity.PRIVATE));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));

    commentService.create(post, postAuthor, "body");

    Assertions
        .assertThat(entityManager.find(Comment.class, 1L))
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
    User postAuthor = entityManager.persist(TestEntity
        .user()
        .setEmail("postAuthor@mail.com")
        .setUsername("postAuthor")
        .setPublicity(Publicity.INTERNAL));
    User author = entityManager.persist(TestEntity
        .user()
        .setEmail("author@mail.com")
        .setUsername("author"));
    postAuthor.setFriends(Sets.newHashSet(author));
    author.setFriends(Sets.newHashSet(postAuthor));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));

    commentService.create(post, author, "body");

    Assertions
        .assertThat(entityManager.find(Comment.class, 1L))
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
    User postAuthor = entityManager.persist(TestEntity
        .user()
        .setEmail("postAuthor@mail.com")
        .setUsername("postAuthor")
        .setPublicity(Publicity.PUBLIC));
    User author = entityManager.persist(TestEntity
        .user()
        .setEmail("author@mail.com")
        .setUsername("author"));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));

    commentService.create(post, author, "body");

    Assertions
        .assertThat(entityManager.find(Comment.class, 1L))
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
  public void update_exception_whenNoEntityWithIdAndAuthor() {
    User author = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> commentService.update(0L, author, "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User postAuthor = entityManager.persist(TestEntity.user());
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));
    entityManager.persist(new Comment()
        .setPost(post)
        .setBody("comment body")
        .setAuthor(postAuthor));

    commentService.update(1L, postAuthor, "new body");

    Assertions
        .assertThat(entityManager.find(Comment.class, 1L))
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
  public void delete_exception_whenNoEntityWithIdAndAuthor() {
    User author = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> commentService.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User postAuthor = entityManager.persist(TestEntity.user());
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));
    entityManager.persist(TestEntity
        .comment()
        .setPost(post)
        .setAuthor(postAuthor));

    commentService.delete(1L, postAuthor);

    Assertions
        .assertThat(entityManager.find(Comment.class, 1L))
        .isNull();
  }

  @Test
  public void findAll_byPost() {
    User postAuthor = entityManager.persist(TestEntity.user());
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(postAuthor));
    entityManager.persist(TestEntity
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
