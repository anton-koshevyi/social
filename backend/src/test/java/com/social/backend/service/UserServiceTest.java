package com.social.backend.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.exception.WrongCredentialsException;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class UserServiceTest {

  private IdentificationContext<User> userIdentification;
  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    userIdentification = new IdentificationContext<>();
    userRepository = new UserRepositoryStub(userIdentification);
    userService = new UserServiceImpl(userRepository, PasswordEncoderStub.getInstance());
  }

  @Test
  public void create() {
    userIdentification.setStrategy(entity -> entity.setId(1L));

    userService.create(
        "email@mail.com",
        "username",
        "first",
        "last",
        "password"
    );

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(new User()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPassword("{encoded}password"));
  }

  @Test
  public void update_whenNoEntityWithId_expectException() {
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setPassword("{encoded}password"));

    userService.update(
        1L,
        "new@mail.com",
        "new username",
        "new first",
        "new last",
        Publicity.INTERNAL
    );

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(new User()
            .setId(1L)
            .setEmail("new@mail.com")
            .setUsername("new username")
            .setFirstName("new first")
            .setLastName("new last")
            .setPublicity(Publicity.INTERNAL)
            .setPassword("{encoded}password"));
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setModer(false));

    userService.updateRole(1L, true);

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
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
        .assertThatThrownBy(() -> userService.changePassword(1L, "actual", "change"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void changePassword_whenWrongActualPassword_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setPassword("{encoded}actual"));

    Assertions
        .assertThatThrownBy(() -> userService.changePassword(1L, "wrong", "change"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void changePassword() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setPassword("{encoded}actual"));

    userService.changePassword(1L, "actual", "change");

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setPassword("{encoded}change"));
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setPassword("{encoded}password"));

    Assertions
        .assertThatThrownBy(() -> userService.delete(1L, "wrong"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void delete() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setPassword("{encoded}password"));

    userService.delete(1L, "password");

    Assertions
        .assertThat(userRepository.findById(1L))
        .isEmpty();
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> userService.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenPrivateTargetEntity_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    userRepository.save(TestEntity
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User user = userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User target = userRepository.save(TestEntity
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    userRepository.save(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));

    userService.addFriend(1L, 2L);

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
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
        .assertThat(userRepository.findById(2L))
        .get()
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity.user());

    Assertions
        .assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend_whenNoTargetInFriends_expectException() {
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    userRepository.save(TestEntity
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User user = userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User target = userRepository.save(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target"));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    userService.removeFriend(1L, 2L);

    Assertions
        .assertThat(userRepository.findById(1L))
        .get()
        .describedAs("Should remove target from entity friends")
        .usingComparator(TestComparator
            .userComparator())
        .isEqualTo(TestEntity
            .user()
            .setId(1L)
            .setEmail("user@mail.com")
            .setUsername("user"));
    Assertions
        .assertThat(userRepository.findById(2L))
        .get()
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    User user = userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    userIdentification.setStrategy(entity -> entity.setId(2L));
    User target = userRepository.save(TestEntity
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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity.user());

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
    userIdentification.setStrategy(entity -> entity.setId(1L));
    userRepository.save(TestEntity.user());

    Assertions
        .assertThat(userService.findAll(Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .userComparator(), User.class)
        .containsExactly(TestEntity
            .user()
            .setId(1L));
  }

}
