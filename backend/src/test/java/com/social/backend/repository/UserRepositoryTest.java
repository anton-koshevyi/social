package com.social.backend.repository;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void findByEmail_emptyOptional_whenNoEntityWithEmail() {
        assertThat(userRepository.findByEmail("email@mail.com"))
                .isEmpty();
    }
    
    @Test
    public void findByEmail() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
    
        assertThat(userRepository.findByEmail("email@mail.com"))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
                        .setPassword("password"));
    }
    
    @Test
    public void findByUsername_emptyOptional_whenNoEntityWithUsername() {
        assertThat(userRepository.findByUsername("username"))
                .isEmpty();
    }
    
    @Test
    public void findByUsername() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
    
        assertThat(userRepository.findByUsername("username"))
                .get()
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
                        .setPassword("password"));
    }
    
    @Test
    public void existsByEmail_false_whenNoEntityWithEmail() {
        assertThat(userRepository.existsByEmail("email@mail.com"))
                .isFalse();
    }
    
    @Test
    public void existsByEmail() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
    
        assertThat(userRepository.existsByEmail("email@mail.com"))
                .isTrue();
    }
    
    @Test
    public void existsByUsername_false_whenNoEntityWithUsername() {
        assertThat(userRepository.existsByUsername("username"))
                .isFalse();
    }
    
    @Test
    public void existsByUsername() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
    
        assertThat(userRepository.existsByUsername("username"))
                .isTrue();
    }
    
    @Test
    public void existsByIdAndFriendsContaining_false_whenUsersAreNotFriends() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
        User user = entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("password"));
        
        assertThat(userRepository.existsByIdAndFriendsContaining(1L, user))
                .isFalse();
    }
    
    @Test
    public void existsByIdAndFriendsContaining() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded")
                .setFriends(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
                        .setPassword("encoded"))));
        User friend = entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("encoded")
                .setFriends(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded"))));
        
        assertThat(userRepository.existsByIdAndFriendsContaining(1L, friend))
                .isTrue();
    }
}
