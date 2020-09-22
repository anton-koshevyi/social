package com.social.backend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;
import com.social.backend.test.stub.repository.PostRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class PostServiceTest {

  private IdentificationContext<Post> postIdentification;
  private PostRepository postRepository;
  private PostService postService;

  @BeforeEach
  public void setUp() {
    postIdentification = new IdentificationContext<>();
    postRepository = new PostRepositoryStub(postIdentification);

    postService = new PostServiceImpl(postRepository);
  }

  @Test
  public void create() {
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));

    postService.create(author, "title", "body");

    Assertions
        .assertThat(postRepository.findById(1L))
        .get()
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
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> postService.update(0L, author, "title", "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));
    postRepository.save(new Post()
        .setTitle("title")
        .setBody("body")
        .setAuthor(author));

    postService.update(1L, author, "new title", "new body");

    Assertions
        .assertThat(postRepository.findById(1L))
        .get()
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
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> postService.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));
    postRepository.save(TestEntity
        .post()
        .setAuthor(author));

    postService.delete(1L, author);

    Assertions
        .assertThat(postRepository.findById(1L))
        .isEmpty();
  }

  @Test
  public void find_byId_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> postService.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));
    postRepository.save(TestEntity
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
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));
    postRepository.save(TestEntity
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
    User author = TestEntity
        .user()
        .setId(1L);
    postIdentification.setStrategy(entity -> entity.setId(1L));
    postRepository.save(TestEntity
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
