package com.social.backend.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.social.backend.TestComparator.notNullFirst;
import static com.social.backend.TestComparator.postComparator;
import static com.social.backend.TestEntity.post;
import static com.social.backend.TestEntity.user;

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
        User author = entityManager.persist(user());
    
        postService.create(author, "body");
    
        assertThat(entityManager.find(Post.class, 1L))
                .usingComparator(postComparator())
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body"));
    }
    
    @Test
    public void update_exception_whenNoPostWithIdAndAuthorId() {
        assertThatThrownBy(() -> postService.update(1L, 2L, "body"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.post.byIdAndAuthorId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L, 2L});
    }
    
    @Test
    public void update() {
        User author = entityManager.persist(user());
        entityManager.persist(new Post()
                .setBody("body")
                .setAuthor(author));
    
        postService.update(1L, 1L, "new body");
    
        assertThat(entityManager.find(Post.class, 1L))
                .usingComparator(postComparator())
                .usingComparatorForFields(notNullFirst(), "updated")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("new body"));
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
        User author = entityManager.persist(user());
        entityManager.persist(post()
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
        User author = entityManager.persist(user());
        entityManager.persist(post()
                .setAuthor(author));
    
        assertThat(postService.findById(1L))
                .usingComparator(postComparator())
                .isEqualTo(post()
                        .setId(1L));
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
        User author = entityManager.persist(user());
        entityManager.persist(post()
                .setAuthor(author));
    
        assertThat(postService.findByIdAndAuthorId(1L, 1L))
                .usingComparator(postComparator())
                .isEqualTo(post()
                        .setId(1L));
    }
    
    @Test
    public void findAll_exception_onNull() {
        assertThatThrownBy(() -> postService.findAll(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAll() {
        User author = entityManager.persist(user());
        entityManager.persist(post()
                .setAuthor(author));
    
        assertThat(postService.findAll(Pageable.unpaged()))
                .usingComparatorForType(postComparator(), Post.class)
                .containsExactly(post()
                        .setId(1L));
    }
    
    @Test
    public void findAllByAuthorId_exception_onNullPageable() {
        assertThatThrownBy(() -> postService.findAllByAuthorId(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAllByAuthorId() {
        User author = entityManager.persist(user());
        entityManager.persist(post()
                .setAuthor(author));
    
        assertThat(postService.findAllByAuthorId(1L, Pageable.unpaged()))
                .usingComparatorForType(postComparator(), Post.class)
                .containsExactly(post()
                        .setId(1L));
    }
}
