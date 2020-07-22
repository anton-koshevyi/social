package com.social.backend.repository;

import java.time.ZonedDateTime;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void findByIdAndAuthorId_emptyOptional_whenNoPostWithIdAndAuthorId() {
        assertThat(commentRepository.findByIdAndAuthorId(1L, 1L))
                .isEmpty();
    }
    
    @Test
    public void findByIdAndAuthorId() {
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
        
        assertThat(commentRepository.findByIdAndAuthorId(1L, 1L))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("comment body")
                        .setPost(new Post(1L))
                        .setAuthor(new User(1L)));
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
    
        assertThat(commentRepository.findAllByPostId(1L, Pageable.unpaged()))
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
