package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.dto.comment.ContentDto;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(CommentServiceImpl.class)
public class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(new User(1L)));
        
        ContentDto dto = new ContentDto().setBody("body");
        commentService.create(1L, 1L, dto);
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("body")
                        .setPost(new Post(1L))
                        .setAuthor(new User(1L)));
    }
    
    @Test
    public void update_exception_whenNoCommentWithIdAndAuthorId() {
        assertThatThrownBy(() -> commentService.update(1L, 2L, new ContentDto()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void update() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(new User(1L)));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(new Post(1L))
                .setAuthor(new User(1L)));
        
        ContentDto dto = new ContentDto().setBody("new");
        commentService.update(1L, 1L, dto);
        
        assertThat(entityManager.find(Comment.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "updated")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("new")
                        .setPost(new Post(1L))
                        .setAuthor(new User(1L)));
    }
    
    @Test
    public void delete_exception_whenNoCommentWithIdAndAuthorId() {
        assertThatThrownBy(() -> commentService.delete(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.comment.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void delete() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(new User(1L)));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(new Post(1L))
                .setAuthor(new User(1L)));
        
        commentService.delete(1L, 1L);
        
        assertThat(entityManager.find(Comment.class, 1L))
                .isNull();
    }
    
    @Test
    public void findAllByPostId_exception_onNullPageable() {
        assertThatThrownBy(() -> commentService.findAllByPostId(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByPostId() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("post body")
                .setAuthor(new User(1L)));
        entityManager.persist(new Comment()
                .setCreated(ZonedDateTime.now())
                .setBody("comment body")
                .setPost(new Post(1L))
                .setAuthor(new User(1L)));
        
        assertThat(commentService.findAllByPostId(1L, PageRequest.of(0, 1)))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created")
                .isEqualTo(ImmutableList.of(new Comment()
                        .setId(1L)
                        .setBody("comment body")
                        .setPost(new Post(1L))
                        .setAuthor(new User(1L))));
    }
    
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    private static <T> Comparator<T> notNullActual() {
        return (T actual, T expected) -> (actual != null) ? 0 : 1;
    }
}
