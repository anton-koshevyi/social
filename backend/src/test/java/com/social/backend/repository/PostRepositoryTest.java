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

import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void findByIdAndAuthorId_emptyOptional_whenNoPostWithIdAndAuthorId() {
        assertThat(postRepository.findByIdAndAuthorId(1L, 2L))
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
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(author));
    
        assertThat(postRepository.findByIdAndAuthorId(1L, 1L))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created", "author")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body"));
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
    
        assertThat(postRepository.findAllByAuthorId(1L, Pageable.unpaged()))
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
