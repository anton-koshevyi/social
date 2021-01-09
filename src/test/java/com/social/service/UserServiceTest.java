package com.social.service;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.social.exception.IllegalActionException;
import com.social.exception.NotFoundException;
import com.social.exception.WrongCredentialsException;
import com.social.model.user.Publicity;
import com.social.model.user.User;
import com.social.repository.UserRepository;
import com.social.test.comparator.ComparatorFactory;
import com.social.test.model.factory.ModelFactory;
import com.social.test.model.mutator.UserMutators;
import com.social.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private @Mock UserRepository repository;
  private @Mock PasswordEncoder passwordEncoder;
  private UserService service;

  @BeforeEach
  public void setUp() {
    service = new UserServiceImpl(repository, passwordEncoder);
  }

  @Test
  public void create() {
    Mockito
        .when(passwordEncoder.encode("password"))
        .thenReturn("{encoded}password");
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          User entity = i.getArgument(0);
          UserMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            "password"
        ))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createModelMutating(UserType.RAW,
                UserMutators.id(1L),
                UserMutators.email("johnsmith@example.com"),
                UserMutators.username("johnsmith"),
                UserMutators.firstName("John"),
                UserMutators.lastName("Smith"),
                UserMutators.publicity(Publicity.PRIVATE),
                UserMutators.password("{encoded}password")
            ));
  }

  @Test
  public void update_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.update(
            2L,
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            Publicity.PUBLIC
        ))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void update() {
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.FRED_BLOGGS,
                UserMutators.email("fredbloggs@example.com"),
                UserMutators.username("fredbloggs"),
                UserMutators.firstName("Fred"),
                UserMutators.lastName("Bloggs"),
                UserMutators.publicity(Publicity.PRIVATE))
        ));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(
            2L,
            "johnsmith@example.com",
            "johnsmith",
            "John",
            "Smith",
            Publicity.PUBLIC
        ))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createModelMutating(UserType.FRED_BLOGGS,
                UserMutators.email("johnsmith@example.com"),
                UserMutators.username("johnsmith"),
                UserMutators.firstName("John"),
                UserMutators.lastName("Smith"),
                UserMutators.publicity(Publicity.PUBLIC)
            ));
  }

  @Test
  public void updateRole_whenNoEntityWithId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.updateRole(1L, true))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{1L});
  }

  @Test
  public void updateRole() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.moder(false))
        ));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.updateRole(1L, true))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.moder(true)
            ));
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
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.password("{encoded}actual"))
        ));

    Assertions
        .assertThatThrownBy(() -> service.changePassword(1L, "wrong", "change"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void changePassword() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.password("{encoded}actual"))
        ));
    Mockito
        .when(passwordEncoder.matches("actual", "{encoded}actual"))
        .thenReturn(true);
    Mockito
        .when(passwordEncoder.encode("change"))
        .thenReturn("{encoded}change");

    service.changePassword(1L, "actual", "change");

    Mockito
        .verify(repository)
        .save(Mockito.refEq(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.password("{encoded}change"))
        ));
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
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.password("{encoded}password"))
        ));

    Assertions
        .assertThatThrownBy(() -> service.delete(1L, "wrong"))
        .isExactlyInstanceOf(WrongCredentialsException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"wrongCredentials.password"});
  }

  @Test
  public void delete() {
    User entity = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.password("{encoded}password"));
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(entity));
    Mockito
        .when(passwordEncoder.matches("password", "{encoded}password"))
        .thenReturn(true);

    service.delete(1L, "password");

    Mockito
        .verify(repository)
        .delete(entity);
  }

  @Test
  public void addFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addHimself"});
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
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.JOHN_SMITH)));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenPrivateTargetEntity_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.JOHN_SMITH)));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.FRED_BLOGGS,
                UserMutators.publicity(Publicity.PRIVATE))));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addPrivate"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend_whenFriendAlreadyPresent_expectException() {
    User user = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.PRIVATE));
    User target = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC),
            UserMutators.friends(user));
    UserMutators.friends(target).accept(user);
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));

    Assertions
        .assertThatThrownBy(() -> service.addFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.addPresent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void addFriend() {
    User user = ModelFactory
        .createModelMutating(UserType.JOHN_SMITH,
            UserMutators.publicity(Publicity.PRIVATE));
    User target = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC));
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    service.addFriend(1L, 2L);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    Mockito
        .verify(repository, Mockito.times(2))
        .save(captor.capture());
    List<User> captures = captor.getAllValues();
    Assertions
        .assertThat(captures.get(0))
        .describedAs("Should add target to entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.publicity(Publicity.PRIVATE),
                UserMutators.friends(ModelFactory
                    .createModelMutating(UserType.FRED_BLOGGS,
                        UserMutators.publicity(Publicity.PUBLIC)))
            ));
    Assertions
        .assertThat(captures.get(1))
        .describedAs("Should add entity to target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .ignoringFields("friends.friends", "friendFor")
        .isEqualTo(ModelFactory
            .createModelMutating(UserType.FRED_BLOGGS,
                UserMutators.publicity(Publicity.PUBLIC),
                UserMutators.friends(ModelFactory
                    .createModelMutating(UserType.JOHN_SMITH,
                        UserMutators.publicity(Publicity.PRIVATE)))
            ));
  }

  @Test
  public void removeFriend_whenEqualUserIdAndTargetId_expectException() {
    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 1L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.removeHimself"});
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
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.JOHN_SMITH)));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.user.byId"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend_whenNoTargetInFriends_expectException() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.JOHN_SMITH)));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.FRED_BLOGGS)));

    Assertions
        .assertThatThrownBy(() -> service.removeFriend(1L, 2L))
        .isExactlyInstanceOf(IllegalActionException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"illegalAction.user.removeAbsent"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L});
  }

  @Test
  public void removeFriend() {
    User user = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User target = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.friends(user));
    UserMutators.friends(target).accept(user);
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(user));
    Mockito
        .when(repository.findById(2L))
        .thenReturn(Optional.of(target));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    service.removeFriend(1L, 2L);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    Mockito
        .verify(repository, Mockito.times(2))
        .save(captor.capture());
    List<User> captures = captor.getAllValues();
    Assertions
        .assertThat(captures.get(0))
        .describedAs("Should remove target from entity friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(ModelFactory
            .createModel(UserType.JOHN_SMITH));
    Assertions
        .assertThat(captures.get(1))
        .describedAs("Should remove entity from target friends")
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(ModelFactory
            .createModel(UserType.FRED_BLOGGS));
  }

  @Test
  public void getFriends() {
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.friends(ModelFactory
                    .createModel(UserType.FRED_BLOGGS)))
        ));

    Assertions
        .assertThat(service.getFriends(1L, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(User.class))
        .containsExactly(ModelFactory
            .createModel(UserType.FRED_BLOGGS));
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
    Mockito
        .when(repository.findById(1L))
        .thenReturn(Optional.of(ModelFactory
            .createModel(UserType.JOHN_SMITH)));

    Assertions
        .assertThat(service.find(1L))
        .usingComparator(ComparatorFactory.getComparator(User.class))
        .isEqualTo(ModelFactory
            .createModel(UserType.JOHN_SMITH));
  }

  @Test
  public void findAll() {
    Mockito
        .when(repository.findAll(Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(UserType.JOHN_SMITH))
        ));

    Assertions
        .assertThat(service.findAll(Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(User.class))
        .containsExactly(ModelFactory
            .createModel(UserType.JOHN_SMITH));
  }

}
