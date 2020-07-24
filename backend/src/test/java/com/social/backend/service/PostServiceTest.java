package com.social.backend.service;

import java.time.ZonedDateTime;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.dto.post.ContentDto;
import com.social.backend.exception.NotFoundException;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(PostServiceImpl.class)
public class PostServiceTest {
    @Autowired
    private PostService postService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        ContentDto dto = new ContentDto().setBody("body");
        postService.create(author, dto);
    
        assertThat(entityManager.find(Post.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "author")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void update_exception_whenNoPostWithIdAndAuthorId() {
        assertThatThrownBy(() -> postService.update(1L, 2L, new ContentDto()))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void update() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
        
        ContentDto dto = new ContentDto().setBody("new");
        postService.update(1L, 1L, dto);
    
        assertThat(entityManager.find(Post.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "updated", "author")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("new"));
    }
    
    @Test
    public void delete_exception_whenNoPostWithIdAndAuthorId() {
        assertThatThrownBy(() -> postService.delete(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void delete() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
        
        postService.delete(1L, 1L);
        
        assertThat(entityManager.find(Post.class, 1L))
                .isNull();
    }
    
    @Test
    public void findById_exception_whenNoPostWithId() {
        assertThatThrownBy(() -> postService.findById(1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void findById() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
    
        assertThat(postService.findById(1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "author")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void findByIdAndAuthorId_exception_whenNoPostWithIdAndAuthorId() {
        assertThatThrownBy(() -> postService.findByIdAndAuthorId(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void findByIdAndAuthorId() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
    
        assertThat(postService.findByIdAndAuthorId(1L, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "author")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void findAll_exception_onNull() {
        assertThatThrownBy(() -> postService.findAll(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAll() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
    
        assertThat(postService.findAll(Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created", "author")
                .isEqualTo(ImmutableList.of(new Post()
                        .setId(1L)
                        .setBody("body")));
    }
    
    @Test
    public void findAllByAuthorId_exception_onNullPageable() {
        assertThatThrownBy(() -> postService.findAllByAuthorId(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByAuthorId() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
    
        assertThat(postService.findAllByAuthorId(1L, Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created", "author")
                .isEqualTo(ImmutableList.of(new Post()
                        .setId(1L)
                        .setBody("body")));
    }
    
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    private static <T> Comparator<T> notNullActual() {
        return (T actual, T expected) -> (actual != null) ? 0 : 1;
    }
}
