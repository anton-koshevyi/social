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
        assertThat(userRepository.findByEmail("email"))
                .isEmpty();
    }
    
    @Test
    public void findByEmail() {
        entityManager.persist(new User()
                .setEmail("email")
                .setUsername("username")
                .setPassword("password")
                .setPublicity(Publicity.PUBLIC)
                .setFirstName("first")
                .setLastName("last"));
        
        assertThat(userRepository.findByEmail("email"))
                .contains(new User()
                        .setEmail("email")
                        .setUsername("username")
                        .setPassword("password")
                        .setPublicity(Publicity.PUBLIC)
                        .setFirstName("first")
                        .setLastName("last"));
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
                .setEmail("email")
                .setUsername("username")
                .setPassword("password")
                .setPublicity(Publicity.PUBLIC)
                .setFirstName("first")
                .setLastName("last"));
        
        assertThat(userRepository.findByUsername("username"))
                .contains(new User()
                        .setEmail("email")
                        .setUsername("username")
                        .setPassword("password")
                        .setPublicity(Publicity.PUBLIC)
                        .setFirstName("first")
                        .setLastName("last"));
    }
    
    @Test
    public void existsByEmail_false_onNull() {
        assertThat(userRepository.existsByEmail(null))
                .isFalse();
    }
    
    @Test
    public void existsByEmail_false_whenNoEntityWithEmail() {
        assertThat(userRepository.existsByEmail("email"))
                .isFalse();
    }
    
    @Test
    public void existsByEmail() {
        entityManager.persist(new User()
                .setEmail("email")
                .setUsername("username")
                .setPassword("password")
                .setPublicity(Publicity.PUBLIC)
                .setFirstName("first")
                .setLastName("last"));
        
        assertThat(userRepository.existsByEmail("email"))
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
                .setEmail("email")
                .setUsername("username")
                .setPassword("password")
                .setPublicity(Publicity.PUBLIC)
                .setFirstName("first")
                .setLastName("last"));
        
        assertThat(userRepository.existsByUsername("username"))
                .isTrue();
    }
}
