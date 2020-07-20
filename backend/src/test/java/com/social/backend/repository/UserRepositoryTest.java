package com.social.backend.repository;

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
    public void findByEmail_emptyOptional_onNull() {
        assertThat(userRepository.findByEmail(null))
                .isEmpty();
    }
    
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
    public void findByUsername_emptyOptional_onNull() {
        assertThat(userRepository.findByUsername(null))
                .isEmpty();
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
    public void existsByEmail_false_onNull() {
        assertThat(userRepository.existsByEmail(null))
                .isFalse();
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
    public void existsByUsername_false_onNull() {
        assertThat(userRepository.existsByUsername(null))
                .isFalse();
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
}
