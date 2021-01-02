package com.social.backend.service;

import java.util.Optional;

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
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.mutator.CommentMutators;
import com.social.backend.test.model.mutator.PostMutators;
import com.social.backend.test.model.mutator.UserMutators;
import com.social.backend.test.model.type.CommentType;
import com.social.backend.test.model.type.PostType;
import com.social.backend.test.model.type.UserType;

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
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.PRIVATE));
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS);

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "Like"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.privatePost"});
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentNotOfFriend_expectException() {
    User postAuthor = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.INTERNAL));
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS);

    Assertions
        .assertThatThrownBy(() -> service.create(post, author, "Like"))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.comment.internalPost"});
  }

  @Test
  public void create_whenPostOfPrivateAuthor_andCommentOfPostAuthor() {
    User postAuthor = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.PRIVATE));
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          CommentMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, postAuthor, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(CommentType.RAW,
                CommentMutators.id(1L),
                CommentMutators.body("Like"),
                CommentMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                CommentMutators.post(ModelFactory
                    .createModelMutating(PostType.READING,
                        PostMutators.author(ModelFactory
                            .createModelMutating(UserType.JOHN_SMITH,
                                UserMutators.publicity(Publicity.PRIVATE)))
                    ))
            ));
  }

  @Test
  public void create_whenPostOfInternalAuthor_andCommentOfFriend() {
    User postAuthor = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.INTERNAL));
    User author = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.friends(postAuthor));
    UserMutators.friends(author).accept(postAuthor);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          CommentMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, author, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(CommentType.RAW,
                CommentMutators.id(1L),
                CommentMutators.body("Like"),
                CommentMutators.author(ModelFactory
                    .createModel(UserType.FRED_BLOGGS)),
                CommentMutators.post(ModelFactory
                    .createModelMutating(PostType.READING,
                        PostMutators.author(ModelFactory
                            .createModelMutating(UserType.JOHN_SMITH,
                                UserMutators.publicity(Publicity.INTERNAL)))
                    ))
            ));
  }

  @Test
  public void create_whenPostOfPublicAuthor() {
    User postAuthor = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.PUBLIC));
    User author = ModelFactory
        .createModel(UserType.FRED_BLOGGS);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Comment entity = i.getArgument(0);
          CommentMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(post, author, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(CommentType.RAW,
                CommentMutators.id(1L),
                CommentMutators.body("Like"),
                CommentMutators.author(ModelFactory
                    .createModel(UserType.FRED_BLOGGS)),
                CommentMutators.post(ModelFactory
                    .createModelMutating(PostType.READING,
                        PostMutators.author(ModelFactory
                            .createModelMutating(UserType.JOHN_SMITH,
                                UserMutators.publicity(Publicity.PUBLIC)))
                    ))
            ));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Mockito
        .when(repository.findByIdAndAuthor(2L, postAuthor))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(CommentType.BADLY,
                CommentMutators.author(postAuthor),
                CommentMutators.post(post))
        ));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(2L, postAuthor, "Like"))
        .usingComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForFields(
            NotNullComparator.leftNotNull(), "createdAt", "updatedAt")
        .isEqualTo(ModelFactory
            .createModelMutating(CommentType.BADLY,
                CommentMutators.body("Like"),
                CommentMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                CommentMutators.post(ModelFactory
                    .createModelMutating(PostType.READING,
                        PostMutators.author(ModelFactory
                            .createModel(UserType.JOHN_SMITH))
                    ))
            ));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Comment entity = ModelFactory
        .createModelMutating(CommentType.LIKE,
            CommentMutators.author(postAuthor),
            CommentMutators.post(post));
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
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(postAuthor));
    Mockito
        .when(repository.findAllByPost(post, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(CommentType.LIKE,
                    CommentMutators.author(postAuthor),
                    CommentMutators.post(post)))
        ));

    Assertions
        .assertThat(service.findAll(post, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Comment.class))
        .usingComparatorForElementFieldsWithNames(
            NotNullComparator.leftNotNull(), "createdAt")
        .containsExactly(ModelFactory
            .createModelMutating(CommentType.LIKE,
                CommentMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                CommentMutators.post(ModelFactory
                    .createModelMutating(PostType.READING,
                        PostMutators.author(ModelFactory
                            .createModel(UserType.JOHN_SMITH))
                    ))
            ));
  }

}
