package com.social.backend.repository;

import java.time.ZonedDateTime;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
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
    public void findByIdAndAuthorId_emptyOptional_onNulls() {
        assertThat(postRepository.findByIdAndAuthorId(null, null))
                .isEmpty();
    }
    
    @Test
    public void findByIdAndAuthorId_emptyOptional_whenNoPostWithIdAndAuthorId() {
        assertThat(postRepository.findByIdAndAuthorId(1L, 2L))
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
                .setBody("body")
                .setAuthor(new User(1L)));
        
        assertThat(postRepository.findByIdAndAuthorId(1L, 1L))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .withComparatorForFields(notNullActual(), "created")
                .isEqualTo(new Post()
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(new User(1L)));
    }
    
    @Test
    public void findAllByAuthorId_empty_onNullAuthorId() {
        assertThat(postRepository.findAllByAuthorId(null, PageRequest.of(0, 1)))
                .isEmpty();
    }
    
    @Test
    public void findAllByAuthorId() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new Post()
                .setCreated(ZonedDateTime.now())
                .setBody("body")
                .setAuthor(new User(1L)));
        
        assertThat(postRepository.findAllByAuthorId(1L, PageRequest.of(0, 1)))
                .usingRecursiveFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithNames(notNullActual(), "created")
                .isEqualTo(ImmutableList.of(new Post()
                        .setId(1L)
                        .setBody("body")
                        .setAuthor(new User(1L))));
    }
    
    private static <T> Comparator<T> notNullActual() {
        return (T actual, T expected) -> (actual != null) ? 0 : 1;
    }
}
