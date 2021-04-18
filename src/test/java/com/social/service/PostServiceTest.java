package com.social.service;

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

import com.social.exception.NotFoundException;
import com.social.model.post.Post;
import com.social.model.user.User;
import com.social.repository.PostRepository;
import com.social.test.comparator.ComparatorFactory;
import com.social.test.comparator.NotNullComparator;
import com.social.test.model.factory.ModelFactory;
import com.social.test.model.mutator.PostMutators;
import com.social.test.model.type.PostType;
import com.social.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

  private @Mock PostRepository postRepository;
  private PostService postService;

  @BeforeEach
  public void setUp() {
    postService = new PostServiceImpl(postRepository);
  }

  @Test
  public void create() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(postRepository.save(Mockito.any()))
        .then(i -> {
          Post entity = i.getArgument(0);
          PostMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(postService.create(
            author, "Favorite books", "My personal must-read fiction"))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(PostType.RAW,
                PostMutators.id(1L),
                PostMutators.title("Favorite books"),
                PostMutators.body("My personal must-read fiction"),
                PostMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() ->
            postService.update(0L, author, "Favorite books", "My personal must-read fiction"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(postRepository.findByIdAndAuthor(2L, author))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(PostType.COOKING,
                PostMutators.author(author))
        ));
    Mockito
        .when(postRepository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(postService.update(
            2L, author, "Favorite books", "My personal must-read fiction"))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForFields(
            NotNullComparator.leftNotNull(), "createdAt", "updatedAt")
        .isEqualTo(ModelFactory
            .createModelMutating(PostType.COOKING,
                PostMutators.title("Favorite books"),
                PostMutators.body("My personal must-read fiction"),
                PostMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> postService.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Post entity = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(author));
    Mockito
        .when(postRepository.findByIdAndAuthor(1L, author))
        .thenReturn(Optional.of(entity));

    postService.delete(1L, author);

    Mockito
        .verify(postRepository)
        .delete(entity);
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
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(postRepository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(PostType.READING,
                PostMutators.author(author))
        ));

    Assertions
        .assertThat(postService.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(PostType.READING,
                PostMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void findAll() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(postRepository.findAll(Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(PostType.READING,
                    PostMutators.author(author)))
        ));

    Assertions
        .assertThat(postService.findAll(Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForElementFieldsWithNames(
            NotNullComparator.leftNotNull(), "createdAt")
        .containsExactly(ModelFactory
            .createModelMutating(PostType.READING,
                PostMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

  @Test
  public void findAll_byAuthor() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(postRepository.findAllByAuthor(author, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(PostType.READING,
                    PostMutators.author(author)))
        ));

    Assertions
        .assertThat(postService.findAll(author, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Post.class))
        .usingComparatorForElementFieldsWithNames(
            NotNullComparator.leftNotNull(), "createdAt")
        .containsExactly(ModelFactory
            .createModelMutating(PostType.READING,
                PostMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH))
            ));
  }

}
