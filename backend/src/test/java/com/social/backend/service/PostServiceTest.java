package com.social.backend.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.test.TestEntity;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.stub.repository.PostRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class PostServiceTest {

  private IdentificationContext<Post> identification;
  private PostRepository repository;
  private PostService service;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new PostRepositoryStub(identification);
    service = new PostServiceImpl(repository);
  }

  @Test
  public void create() {
    User author = TestEntity
        .user()
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));

    service.create(author, "title", "body");

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(Post.class))
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
        .assertThatThrownBy(() -> service.update(0L, author, "title", "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = TestEntity
        .user()
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(new Post()
        .setTitle("title")
        .setBody("body")
        .setAuthor(author));

    service.update(1L, author, "new title", "new body");

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "updatedAt")
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
        .assertThatThrownBy(() -> service.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = TestEntity
        .user()
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .post()
        .setAuthor(author));

    service.delete(1L, author);

    Assertions
        .assertThat(repository.findById(1L))
        .isEmpty();
  }

  @Test
  public void find_byId_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    User author = TestEntity
        .user()
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(service.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(service.findAll(Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Post.class), Post.class)
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
    identification.setStrategy(e -> e.setId(1L));
    repository.save(TestEntity
        .post()
        .setAuthor(author));

    Assertions
        .assertThat(service.findAll(author, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Post.class), Post.class)
        .containsExactly(TestEntity
            .post()
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)));
  }

}
