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
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.model.ModelFactoryProducer;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class UserServiceTest {

  private IdentificationContext<User> identification;
  private UserRepository repository;
  private UserService service;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new UserRepositoryStub(identification);
    service = new UserServiceImpl(repository, PasswordEncoderStub.getInstance());
  }

  @Test
  public void create() {
    identification.setStrategy(e -> e.setId(1L));

    service.create(
        "johnsmith@example.com",
        "johnsmith",
        "John",
        "Smith",
        "password"
    );

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(new User()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPassword("{encoded}password"));
  }

  @Test
  public void update_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.update(
            1L,
            "fredbloggs@example.com",
            "fredbloggs",
            "Fred",
            "Bloggs",
            Publicity.PUBLIC
        ))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void update() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(new User()
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("{encoded}password"));

    service.update(
        1L,
        "fredbloggs@example.com",
        "fredbloggs",
        "Fred",
        "Bloggs",
        Publicity.PUBLIC
    );

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(new User()
            .setId(1L)
            .setEmail("fredbloggs@example.com")
            .setUsername("fredbloggs")
            .setFirstName("Fred")
            .setLastName("Bloggs")
            .setPublicity(Publicity.PUBLIC)
            .setPassword("{encoded}password"));
  }

  @Test
  public void updateRole_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.updateRole(1L, false))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void updateRole() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setModer(false));

    service.updateRole(1L, true);

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.JOHN_SMITH)
            .setId(1L)
            .setModer(true));
  }

  @Test
  public void changePassword_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.changePassword(1L, "actual", "change"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void changePassword_whenWrongActualPassword_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword("{encoded}actual"));

    Assertions
        .assertThatThrownBy(() -> service.changePassword(1L, "wrong", "change"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void changePassword() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword("{encoded}actual"));

    service.changePassword(1L, "actual", "change");

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.JOHN_SMITH)
            .setId(1L)
            .setPassword("{encoded}change"));
  }

  @Test
  public void delete_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.delete(1L, "password"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void delete_whenWrongActualPassword_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword("{encoded}password"));

    Assertions
        .assertThatThrownBy(() -> service.delete(1L, "wrong"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void delete() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword("{encoded}password"));

    service.delete(1L, "password");

    Assertions
        .assertThat(repository.findById(1L))
        .isEmpty();
  }

  @Test
  public void addFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addHimself"});
  }

  @Test
  public void addFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void addFriend_whenNoTargetEntityWithId_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenPrivateTargetEntity_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    identification.setStrategy(e -> e.setId(2L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PRIVATE));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPrivate"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenFriendAlreadyPresent_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    User user = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPublicity(Publicity.PRIVATE));
    identification.setStrategy(e -> e.setId(2L));
    User target = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.addPresent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPublicity(Publicity.PRIVATE));
    identification.setStrategy(e -> e.setId(2L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));

    service.addFriend(1L, 2L);

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .describedAs("Should add target to entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.JOHN_SMITH)
            .setId(1L)
            .setPublicity(Publicity.PRIVATE)
            .setFriends(Sets
                .newHashSet(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.FRED_BLOGGS)
                    .setId(2L)
                    .setPublicity(Publicity.PUBLIC))
            ));
    Assertions
        .assertThat(repository.findById(2L))
        .get()
        .describedAs("Should add entity to target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.FRED_BLOGGS)
            .setId(2L)
            .setPublicity(Publicity.PUBLIC)
            .setFriends(Sets
                .newHashSet(ModelFactoryProducer.getFactory(User.class)
                    .createModel(UserType.JOHN_SMITH)
                    .setId(1L)
                    .setPublicity(Publicity.PRIVATE))
            ));
  }

  @Test
  public void removeFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeHimself"});
  }

  @Test
  public void removeFriend_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void removeFriend_whenNoTargetWithId_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend_whenNoTargetInFriends_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    identification.setStrategy(e -> e.setId(2L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"illegalAction.user.removeAbsent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend() {
    identification.setStrategy(e -> e.setId(1L));
    User user = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    identification.setStrategy(e -> e.setId(2L));
    User target = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    service.removeFriend(1L, 2L);

    Assertions
        .assertThat(repository.findById(1L))
        .get()
        .describedAs("Should remove target from entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.JOHN_SMITH)
            .setId(1L));
    Assertions
        .assertThat(repository.findById(2L))
        .get()
        .describedAs("Should remove entity from target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.FRED_BLOGGS)
            .setId(2L));
  }

  @Test
  public void getFriends() {
    identification.setStrategy(e -> e.setId(1L));
    User user = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    identification.setStrategy(e -> e.setId(2L));
    User target = repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS));
    user.setFriends(Sets.newHashSet(target));
    target.setFriends(Sets.newHashSet(user));

    Assertions
        .assertThat(service.getFriends(1L, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(User.class), User.class)
        .containsExactly(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.FRED_BLOGGS)
            .setId(2L));
  }

  @Test
  public void find_byId_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.find(1L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void find_byId() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));

    Assertions
        .assertThat(service.find(1L))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactoryProducer.getFactory(User.class)
            .createModel(UserType.JOHN_SMITH)
            .setId(1L));
  }

  @Test
  public void findAll() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    identification.setStrategy(e -> e.setId(2L));
    repository.save(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.FRED_BLOGGS));

    Assertions
        .assertThat(service.findAll(Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(User.class), User.class)
        .containsExactlyInAnyOrder(
            ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.JOHN_SMITH)
                .setId(1L),
            ModelFactoryProducer.getFactory(User.class)
                .createModel(UserType.FRED_BLOGGS)
                .setId(2L));
  }

}
