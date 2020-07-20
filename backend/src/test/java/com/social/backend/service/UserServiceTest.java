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
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.dto.user.CreateDto;
import com.social.backend.dto.user.DeleteDto;
import com.social.backend.dto.user.PasswordDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UpdateDto;
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
    public void create_encodePassword_andSetPublicityToPrivate() {
        Mockito.when(passwordEncoder.encode(
                "password"
        )).thenReturn("encoded");
        
        userService.create(new CreateDto()
                .setEmail("email@mail.com")
                .setUsername("username")
                .setFirstName("first")
                .setLastName("last")
                .setPassword("password"));
        
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded"));
    }
    
    @Test
    public void update_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.update(1L, new UpdateDto()))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        UpdateDto dto = new UpdateDto()
                .setEmail("new@mail.com")
                .setUsername("new")
                .setFirstName("new")
                .setLastName("new")
                .setPublicity(Publicity.INTERNAL);
        userService.update(1L, dto);
        
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("new@mail.com")
                        .setUsername("new")
                        .setFirstName("new")
                        .setLastName("new")
                        .setPublicity(Publicity.INTERNAL)
                        .setPassword("encoded"));
    }
    
    @Test
    public void updateRole_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.updateRole(1L, new RoleDto()))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded")
                .setModer(false));
        
        RoleDto dto = new RoleDto().setModer(true);
        userService.updateRole(1L, dto);
        
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded")
                        .setModer(true));
    }
    
    @Test
    public void changePassword_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.changePassword(1L, new PasswordDto()))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        PasswordDto dto = new PasswordDto().setActual("password");
        assertThatThrownBy(() -> userService.changePassword(1L, dto))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encodedOld"));
        Mockito.when(passwordEncoder.matches(
                "password",
                "encodedOld"
        )).thenReturn(true);
        Mockito.when(passwordEncoder.encode(
                "change"
        )).thenReturn("encodedNew");
        
        PasswordDto dto = new PasswordDto()
                .setActual("password")
                .setChange("change");
        userService.changePassword(1L, dto);
        
        assertThat(entityManager.find(User.class, 1L))
                .usingRecursiveComparison()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encodedNew"));
    }
    
    @Test
    public void delete_exception_whenNoUserWithId() {
        assertThatThrownBy(() -> userService.delete(1L, new DeleteDto()))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        DeleteDto dto = new DeleteDto().setPassword("password");
        assertThatThrownBy(() -> userService.delete(1L, dto))
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        Mockito.when(passwordEncoder.matches(
                "password",
                "encoded"
        )).thenReturn(true);
        
        DeleteDto dto = new DeleteDto().setPassword("password");
        userService.delete(1L, dto);
        
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
                .setPublicity(Publicity.PRIVATE)
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new User()
                .setEmail("email2@mail.com")
                .setUsername("username2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isExactlyInstanceOf(IllegalActionException.class)
                .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPrivate"})
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
                .ignoringFields("friendFor")
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
                .ignoringFields("friendFor")
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
                .setPublicity(Publicity.PRIVATE)
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
                        .setPassword("encoded")))));
        entityManager.persist(new User()
                .setEmail("email_2@mail.com")
                .setUsername("username_2")
                .setFirstName("first")
                .setLastName("last")
                .setPublicity(Publicity.PUBLIC)
                .setPassword("encoded")
                .setFriends(new ArrayList<>(ImmutableList.of(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded")))));
        
        userService.removeFriend(1L, 2L);
        
        assertThat(entityManager.find(User.class, 1L))
                .describedAs("Should remove target from entity friends")
                .usingRecursiveComparison()
                .ignoringFields("friendFor")
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email_1@mail.com")
                        .setUsername("username_1")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded"));
        assertThat(entityManager.find(User.class, 2L))
                .describedAs("Should remove entity from target friends")
                .usingRecursiveComparison()
                .ignoringFields("friendFor")
                .isEqualTo(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
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
        
        assertThat(userService.getFriends(1L, PageRequest.of(0, 1)))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new User()
                        .setId(2L)
                        .setEmail("email_2@mail.com")
                        .setUsername("username_2")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PUBLIC)
                        .setPassword("encoded"));
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        assertThat(userService.findById(1L))
                .usingRecursiveComparison()
                .isEqualTo(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
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
                .setPublicity(Publicity.PRIVATE)
                .setPassword("encoded"));
        
        assertThat(userService.findAll(PageRequest.of(0, 1)))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new User()
                        .setId(1L)
                        .setEmail("email@mail.com")
                        .setUsername("username")
                        .setFirstName("first")
                        .setLastName("last")
                        .setPublicity(Publicity.PRIVATE)
                        .setPassword("encoded"));
    }
}
