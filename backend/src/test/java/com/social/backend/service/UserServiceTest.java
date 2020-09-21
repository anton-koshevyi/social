package com.social.backend.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
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
import com.social.backend.repository.UserRepositoryImpl;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Import({UserServiceImpl.class, UserRepositoryImpl.class})
public class UserServiceTest {

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserService userService;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void create() {
    Mockito
        .when(passwordEncoder.encode("password"))
        .thenReturn("encoded");

    userService.create(
        "email@mail.com",
        "username",
        "first",
        "last",
        "password"
    );

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(new User()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPassword("encoded"));
  }

  @Test
  public void update_whenEntityUserWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.update(
            1L,
            "email@mail.com",
            "username",
            "first",
            "last",
            Publicity.PUBLIC
        ))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void update() {
    entityManager.persist(TestEntity.user());

    userService.update(
        1L,
        "new@mail.com",
        "new username",
        "new first",
        "new last",
        Publicity.INTERNAL
    );

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .usingComparator(TestComparator
            .userComparator())
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
  public void updateRole_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.updateRole(1L, false))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void updateRole() {
    entityManager.persist(TestEntity
        .user()
        .setModer(false));

    userService.updateRole(1L, true);

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setModer(true));
  }

  @Test
  public void changePassword_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.changePassword(1L, "password", "change"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void changePassword_whenWrongActualPassword_expectException() {
    entityManager.persist(TestEntity
        .user()
        .setPassword("wrongActual"));

    Assertions
        .assertThatThrownBy(() -> userService.changePassword(1L, "password", "change"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void changePassword() {
    entityManager.persist(TestEntity
        .user()
        .setPassword("encodedOld"));
    Mockito
        .when(passwordEncoder.matches("password", "encodedOld"))
        .thenReturn(true);
    Mockito
        .when(passwordEncoder.encode("change"))
        .thenReturn("encodedNew");

    userService.changePassword(1L, "password", "change");

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setPassword("encodedNew"));
  }

  @Test
  public void delete_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.delete(1L, "password"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void delete_whenWrongActualPassword_expectException() {
    entityManager.persist(TestEntity
        .user()
        .setPassword("wrongActual"));

    Assertions
        .assertThatThrownBy(() -> userService.delete(1L, "password"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void delete() {
    entityManager.persist(TestEntity.user());
    Mockito
        .when(passwordEncoder.matches("password", "encoded"))
        .thenReturn(true);

    userService.delete(1L, "password");

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .isNull();
  }

  @Test
  public void addFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addHimself"});
  }

  @Test
  public void addFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void addFriend_whenNoTargetEntityWithId_expectException() {
    entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenPrivateTargetEntity_expectException() {
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PRIVATE));

    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPrivate"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenFriendAlreadyPresent_expectException() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPresent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend() {
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));

    userService.addFriend(1L, 2L);

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .describedAs("Should add target to entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setEmail("user@mail.com")
            .setUsername("user")
            .setPublicity(Publicity.PRIVATE)
            .setFriends(Sets
                .newHashSet(TestEntity
                    .user()
                    .setId(2L)
                    .setEmail("target@mail.com")
                    .setUsername("target")
                    .setPublicity(Publicity.PUBLIC))
            ));
    Assertions
        .assertThat(entityManager.find(User.class, 2L))
        .describedAs("Should add entity to target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(TestEntity
            .user()
            .setId(2L)
            .setEmail("target@mail.com")
            .setUsername("target")
            .setPublicity(Publicity.PUBLIC)
            .setFriends(Sets
                .newHashSet(TestEntity
                    .user()
                    .setId(1L)
                    .setEmail("user@mail.com")
                    .setUsername("user")
                    .setPublicity(Publicity.PRIVATE))
            ));
  }

  @Test
  public void removeFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.removeFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeHimself"});
  }

  @Test
  public void removeFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void removeFriend_whenNoTargetWithId_expectException() {
    entityManager.persist(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend_whenNoTargetInFriends_expectException() {
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target"));

    Assertions
        .assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeAbsent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target"));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    userService.removeFriend(1L, 2L);

    Assertions
        .assertThat(entityManager.find(User.class, 1L))
        .describedAs("Should remove target from entity friends")
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setEmail("user@mail.com")
            .setUsername("user"));
    Assertions
        .assertThat(entityManager.find(User.class, 2L))
        .describedAs("Should remove entity from target friends")
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(2L)
            .setEmail("target@mail.com")
            .setUsername("target"));
  }

  @Test
  public void getFriends() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target"));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    Assertions
        .assertThat(userService.getFriends(1L, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .userComparator(), User.class)
        .containsExactly(TestEntity
            .user()
            .setId(2L)
            .setEmail("target@mail.com")
            .setUsername("target"));
  }

  @Test
  public void find_byId_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> userService.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    entityManager.persist(TestEntity.user());

    Assertions
        .assertThat(userService.find(1L))
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L));
  }

  @Test
  public void findAll() {
    entityManager.persist(TestEntity.user());

    Assertions
        .assertThat(userService.findAll(Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .userComparator(), User.class)
        .containsExactly(TestEntity
            .user()
            .setId(1L));
  }

}
