package com.social.backend.service;

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

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepositoryImpl;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import({PostServiceImpl.class, PostRepositoryImpl.class})
public class PostServiceTest {

  @Autowired
  private PostService postService;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void create() {
    User author = entityManager.persist(TestEntity.user());

    postService.create(author, "title", "body");

    Assertions
        .assertThat(entityManager.find(Post.class, 1L))
        .usingComparator(TestComparator
            .postComparator())
        .isEqualTo(new Post()
            .setId(1L)
            .setTitle("title")
            .setBody("body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void update_exception_whenNoPostWithIdAndAuthor() {
    User author = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> postService.update(0L, author, "title", "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(new Post()
        .setTitle("title")
        .setBody("body")
        .setAuthor(author));

    postService.update(1L, author, "new title", "new body");

    Assertions
        .assertThat(entityManager.find(Post.class, 1L))
        .usingComparator(TestComparator
            .postComparator())
        .usingComparatorForFields(TestComparator
            .notNullFirst(), "updated")
        .isEqualTo(new Post()
            .setId(1L)
            .setTitle("new title")
            .setBody("new body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void delete_exception_whenNoPostWithIdAndAuthor() {
    User author = entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> postService.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));

    postService.delete(1L, author);

    Assertions
        .assertThat(entityManager.find(Post.class, 1L))
        .isNull();
  }

  @Test
  public void find_byId_exception_whenNoPostWithId() {
    Assertions
        .assertThatThrownBy(() -> postService.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(postService.find(1L))
        .usingComparator(TestComparator
            .postComparator())
        .isEqualTo(TestEntity
            .post()
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void findAll() {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(postService.findAll(Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .postComparator(), Post.class)
        .containsExactly(TestEntity
            .post()
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

  @Test
  public void findAll_byAuthor() {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(postService.findAll(author, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .postComparator(), Post.class)
        .containsExactly(TestEntity
            .post()
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

}
