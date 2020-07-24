package com.social.backend.service;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
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
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
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
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        userService.update(1L, "new@mail.com", "new username", "new first", "new last", Publicity.INTERNAL);
    
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
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
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setModer(false));
    
        userService.updateRole(1L, true);
    
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")
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
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThatThrownBy(() -> userService.changePassword(1L, "password", "change"))
                .isExactlyInstanceOf(WrongCredentialsException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
    }
    
    @Test
    public void changePassword_encodeAndSetNew() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
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
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encodedNew"));
    }
    
    @Test
    public void delete_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.delete(1L, "password"))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void delete_exception_whenActualPasswordIsWrong() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThatThrownBy(() -> userService.delete(1L, "password"))
                .isExactlyInstanceOf(WrongCredentialsException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
    }
    
    @Test
    public void delete() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
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
    public void addFriend_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void addFriend_exception_whenNoTargetUserWithId() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend_exception_whenTargetUserPublicityIsPrivate() {
        entityManager.persist(new User()
                .setEmail("email1@mail.com")
                .setUsername("username1")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new User()
                .setEmail("email2@mail.com")
                .setUsername("username2")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPrivate"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend_exception_whenFriendAlreadyPresent() {
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
        entityManager.persist(new User()
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
        
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPresent"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void addFriend() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("encoded"));
        
        userService.addFriend(1L, 2L);
        
        assertThat(entityManager.find(User.class, 1L))
                .describedAs("Should add target to entity friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friends.friends", "friendFor")
                .isEqualTo(new User()
                        .setId(1L)
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
        assertThat(entityManager.find(User.class, 2L))
                .describedAs("Should add entity to target friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friends.friends", "friendFor")
                .isEqualTo(new User()
                        .setId(2L)
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
    }
    
    @Test
    public void removeFriend_exception_whenUserAndTargetIdAreEquals() {
        assertThatThrownBy(() -> userService.removeFriend(1L, 1L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeHimself"});
    }
    
    @Test
    public void removeFriend_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void removeFriend_exception_whenNoTargetUserWithId() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void removeFriend_exception_whenNoTargetInFriends() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
        
        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeAbsent"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
    }
    
    @Test
    public void removeFriend() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")))));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")))));
        
        userService.removeFriend(1L, 2L);
    
        assertThat(entityManager.find(User.class, 1L))
                .describedAs("Should remove target from entity friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friendFor")
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"));
        assertThat(entityManager.find(User.class, 2L))
                .describedAs("Should remove entity from target friends")
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .ignoringFields("friendFor")
                .isEqualTo(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"));
    }
    
    @Test
    public void getFriends_exception_onNull() {
        assertThatThrownBy(() -> userService.getFriends(1L, null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void getFriends() {
        entityManager.persist(new User()
                .setEmail("email_1@mail.com")
                .setUsername("username_1")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setFriends(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"))));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded")
                .setFriends(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"))));
    
        assertThat(userService.getFriends(1L, Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")));
    }
    
    @Test
    public void findById_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.findById(1L))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
                .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
    }
    
    @Test
    public void findById() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThat(userService.findById(1L))
                .usingRecursiveComparison()
                .ignoringAllOverriddenEquals()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded"));
    }
    
    @Test
    public void findAll_exception_onNull() {
        assertThatThrownBy(() -> userService.findAll(null))
                .isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("Pageable must not be null");
    }
    
    @Test
    public void findAll() {
        entityManager.persist(new User()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("encoded"));
    
        assertThat(userService.findAll(Pageable.unpaged()))
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPassword("encoded")));
    }
}
