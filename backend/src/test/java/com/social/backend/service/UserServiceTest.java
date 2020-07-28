package com.social.backend.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.exception.WrongCredentialsException;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.list;

import static com.social.backend.TestComparator.userComparator;
import static com.social.backend.TestEntity.user;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import(UserServiceImpl.class)
public class UserServiceTest {
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void create_encodePassword() {
        Mockito.when(passwordEncoder.encode(
                "password"
        )).thenReturn("encoded");
    
        userService.create("email@mail.com", "username", "first", "last", "password");
    
        assertThat(entityManager.find(User.class, 1L))
                .usingComparator(userComparator())
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"));
    }
    
    @Test
    public void update_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.update(1L, "email@mail.com", "usrnm", "first", "last", Publicity.PUBLIC))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void update() {
        entityManager.persist(user());
    
        userService.update(1L, "new@mail.com", "new username", "new first", "new last", Publicity.INTERNAL);
    
        assertThat(entityManager.find(User.class, 1L))
                .usingComparator(userComparator())
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("new@mail.com")
                        .setUsername("new username")
                        .setFirstName("new first")
                        .setLastName("new last")
                        .setPublicity(Publicity.INTERNAL)
                        .setPassword("encoded"));
    }
    
    @Test
    public void updateRole_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.updateRole(1L, false))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void updateRole() {
        entityManager.persist(user()
                .setModer(false));
    
        userService.updateRole(1L, true);
    
        assertThat(entityManager.find(User.class, 1L))
                .usingComparator(userComparator())
                .isEqualTo(user()
                        .setId(1L)
                        .setModer(true));
    }
    
    @Test
    public void changePassword_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.changePassword(1L, "password", "change"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void changePassword_exception_whenActualPasswordIsWrong() {
        entityManager.persist(user()
                .setPassword("wrongActual"));
    
        assertThatThrownBy(() -> userService.changePassword(1L, "password", "change"))
                .isExactlyInstanceOf(WrongCredentialsException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
    }
    
    @Test
    public void changePassword_encodeAndSetNew() {
        entityManager.persist(user()
                .setPassword("encodedOld"));
        Mockito.when(passwordEncoder.matches(
                "password",
                "encodedOld"
        )).thenReturn(true);
        Mockito.when(passwordEncoder.encode(
                "change"
        )).thenReturn("encodedNew");
    
        userService.changePassword(1L, "password", "change");
    
        assertThat(entityManager.find(User.class, 1L))
                .usingComparator(userComparator())
                .isEqualTo(user()
                        .setId(1L)
                        .setPassword("encodedNew"));
    }
    
    @Test
    public void delete_exception_whenNoEntityWithId() {
        assertThatThrownBy(() -> userService.delete(1L, "password"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void delete_exception_whenActualPasswordIsWrong() {
        entityManager.persist(user()
                .setPassword("wrongActual"));
    
        assertThatThrownBy(() -> userService.delete(1L, "password"))
                .isExactlyInstanceOf(WrongCredentialsException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
    }
    
    @Test
    public void delete() {
        entityManager.persist(user());
        Mockito.when(passwordEncoder.matches(
                "password",
                "encoded"
        )).thenReturn(true);
    
        userService.delete(1L, "password");
        
        assertThat(entityManager.find(User.class, 1L))
                .isNull();
    }
    
    @Test
    public void addFriend_exception_whenUserAndTargetIdAreEquals() {
        assertThatThrownBy(() -> userService.addFriend(1L, 1L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addHimself"});
    }
    
    @Test
    public void addFriend_exception_whenNoEntityWithId() {
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void addFriend_exception_whenNoTargetEntityWithId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend_exception_whenTargetEntityIsPrivate() {
        entityManager.persist(user()
                .setEmail("email1@mail.com")
                .setUsername("username1"));
        entityManager.persist(user()
                .setEmail("email2@mail.com")
                .setUsername("username2"));
        
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPrivate"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend_exception_whenFriendAlreadyPresent() {
        entityManager.persist(user()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setPublicity(Publicity.PRIVATE)
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setPublicity(Publicity.PUBLIC))));
        entityManager.persist(user()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setPublicity(Publicity.PUBLIC)
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setPublicity(Publicity.PRIVATE))));
    
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPresent"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend() {
        entityManager.persist(user()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setPublicity(Publicity.PRIVATE));
        entityManager.persist(user()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setPublicity(Publicity.PUBLIC));
    
        userService.addFriend(1L, 2L);
    
        assertThat(entityManager.find(User.class, 1L))
                .describedAs("Should add target to entity friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friends.friends", "friendFor")
                .isEqualTo(user()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setPublicity(Publicity.PRIVATE)
                        .setFriends(list(user()
                                .setId(2L)
                                .setEmail("email_2@mail.com")
                                .setUsername("username_2")
                                .setPublicity(Publicity.PUBLIC))));
        assertThat(entityManager.find(User.class, 2L))
                .describedAs("Should add entity to target friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friends.friends", "friendFor")
                .isEqualTo(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setPublicity(Publicity.PUBLIC)
                        .setFriends(list(user()
                                .setId(1L)
                                .setEmail("email_1@mail.com")
                                .setUsername("username_1")
                                .setPublicity(Publicity.PRIVATE))));
    }
    
    @Test
    public void removeFriend_exception_whenUserIdAndTargetIdAreEquals() {
        assertThatThrownBy(() -> userService.removeFriend(1L, 1L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeHimself"});
    }
    
    @Test
    public void removeFriend_exception_whenNoEntityWithId() {
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void removeFriend_exception_whenNoTargetWithId() {
        entityManager.persist(user());
        
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void removeFriend_exception_whenNoTargetInFriends() {
        entityManager.persist(user()
                .setEmail("email_1@mail.com")
                .setUsername("username_1"));
        entityManager.persist(user()
                .setEmail("email_2@mail.com")
                .setUsername("username_2"));
    
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeAbsent"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void removeFriend() {
        entityManager.persist(user()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2"))));
        entityManager.persist(user()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1"))));
    
        userService.removeFriend(1L, 2L);
    
        assertThat(entityManager.find(User.class, 1L))
                .describedAs("Should remove target from entity friends")
                .usingComparator(userComparator())
                .isEqualTo(user()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1"));
        assertThat(entityManager.find(User.class, 2L))
                .describedAs("Should remove entity from target friends")
                .usingComparator(userComparator())
                .isEqualTo(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2"));
    }
    
    @Test
    public void getFriends() {
        entityManager.persist(user()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFriends(list(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2"))));
        entityManager.persist(user()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFriends(list(user()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1"))));
    
        assertThat(userService.getFriends(1L, Pageable.unpaged()))
                .usingComparatorForType(userComparator(), User.class)
                .containsExactly(user()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2"));
    }
    
    @Test
    public void findById_exception_whenNoEntityWithId() {
        assertThatThrownBy(() -> userService.findById(1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void findById() {
        entityManager.persist(user());
    
        assertThat(userService.findById(1L))
                .usingComparator(userComparator())
                .isEqualTo(user()
                        .setId(1L));
    }
    
    @Test
    public void findAll() {
        entityManager.persist(user());
    
        assertThat(userService.findAll(Pageable.unpaged()))
                .usingComparatorForType(userComparator(), User.class)
                .containsExactly(user()
                        .setId(1L));
    }
}
