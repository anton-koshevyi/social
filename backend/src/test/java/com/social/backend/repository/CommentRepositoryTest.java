package com.social.backend.repository;

import java.util.Collections;
import java.util.Comparator;

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
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setBody("post body")
                .setAuthor(author));
        entityManager.persist(new Comment()
                .setBody("comment body")
                .setPost(post)
                .setAuthor(author));
    
        assertThat(commentRepository.findByIdAndAuthorId(1L, 1L))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .ignoringFields("post", "author")
                .isEqualTo(new Comment()
                        .setId(1L)
                        .setBody("comment body"));
    }
    
    @Test
    public void findAllByPostId() {
        User author = entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        Post post = entityManager.persist(new Post()
                .setBody("post body")
                .setAuthor(author));
        entityManager.persist(new Comment()
                .setBody("comment body")
                .setPost(post)
                .setAuthor(author));
    
        assertThat(commentRepository.findAllByPostId(1L, Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created")
                .usingElementComparatorIgnoringFields("post", "author")
                .isEqualTo(Collections.singletonList(new Comment()
                        .setId(1L)
                        .setBody("comment body")));
    }
    
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    private static <T> Comparator<T> notNullActual() {
        return (T actual, T expected) -> (actual != null) ? 0 : 1;
    }
}
