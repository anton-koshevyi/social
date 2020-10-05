package com.social.backend.service;

import java.util.Optional;

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  private @Mock PostRepository repository;
  private PostService service;

  @BeforeEach
  public void setUp() {
    service = new PostServiceImpl(repository);
  }

  @Test
  public void create() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Post entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.create(
            author, "Favorite books", "My personal must-read fiction"))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .isEqualTo(new Post()
            .setId(1L)
            .setTitle("Favorite books")
            .setBody("My personal must-read fiction")
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() ->
            service.update(0L, author, "Favorite books", "My personal must-read fiction"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findByIdAndAuthor(1L, author))
        .thenReturn(Optional.of(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setAuthor(author)));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(
            1L, author, "Favorite books", "My personal must-read fiction"))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "updatedAt")
        .isEqualTo(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setTitle("Favorite books")
            .setBody("My personal must-read fiction")
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
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Post entity = ModelFactory
        .createModel(PostType.COOKING)
        .setId(1L)
        .setAuthor(author);
    Mockito
        .when(repository.findByIdAndAuthor(1L, author))
        .thenReturn(Optional.of(entity));

    service.delete(1L, author);

    Mockito
        .verify(repository)
        .delete(entity);
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
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setAuthor(author)));

    Assertions
        .assertThat(service.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .isEqualTo(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void findAll() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findAll(Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(PostType.COOKING)
                .setId(1L)
                .setAuthor(author))
        ));

    Assertions
        .assertThat(service.findAll(Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Post.class), Post.class)
        .containsExactly(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void findAll_byAuthor() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(repository.findAllByAuthor(author, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(PostType.COOKING)
                .setId(1L)
                .setAuthor(author))
        ));

    Assertions
        .assertThat(service.findAll(author, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Post.class), Post.class)
        .containsExactly(ModelFactory
            .createModel(PostType.COOKING)
            .setId(1L)
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

}
