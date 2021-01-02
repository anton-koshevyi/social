package com.social.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.exception.IllegalActionException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.exception.WrongCredentialsException;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;
import com.social.backend.util.NullableUtil;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public User create(String email,
                     String username,
                     String firstName,
                     String lastName,
                     String password) {
    User entity = new User();
    entity.setEmail(email);
    entity.setUsername(username);
    entity.setFirstName(firstName);
    entity.setLastName(lastName);
    entity.setPassword(passwordEncoder.encode(password));
    return repository.save(entity);
  }

  @Override
  public User update(Long id,
                     String email,
                     String username,
                     String firstName,
                     String lastName,
                     Integer publicity) {
    User entity = this.find(id);
    NullableUtil.set(entity::setEmail, email);
    NullableUtil.set(entity::setUsername, username);
    NullableUtil.set(entity::setFirstName, firstName);
    NullableUtil.set(entity::setLastName, lastName);
    NullableUtil.set(entity::setPublicity, publicity);
    return repository.save(entity);
  }

  @Override
  public User updateRole(Long id, Boolean moder) {
    User entity = this.find(id);
    NullableUtil.set(entity::setModer, moder);
    return repository.save(entity);
  }

  @Override
  public void changePassword(Long id, String actual, String change) {
    User entity = this.find(id);

    if (!passwordEncoder.matches(actual, entity.getPassword())) {
      throw new WrongCredentialsException("wrongCredentials.password");
    }

    entity.setPassword(passwordEncoder.encode(change));
    repository.save(entity);
  }

  @Override
  public void delete(Long id, String password) {
    User entity = this.find(id);

    if (!passwordEncoder.matches(password, entity.getPassword())) {
      throw new WrongCredentialsException("wrongCredentials.password");
    }

    repository.delete(entity);
  }

  @Override
  public void addFriend(Long id, Long targetId) {
    if (Objects.equals(id, targetId)) {
      throw new IllegalActionException("illegalAction.user.addHimself");
    }

    User entity = this.find(id);
    User target = this.find(targetId);

    if (target.isPrivate()) {
      throw new IllegalActionException("illegalAction.user.addPrivate", targetId);
    }

    if (entity.hasFriendship(target)) {
      throw new IllegalActionException("illegalAction.user.addPresent", targetId);
    }

    entity.setFriends(addFriend(entity, target));
    target.setFriends(addFriend(target, entity));
    repository.save(entity);
    repository.save(target);
  }

  @Override
  public void removeFriend(Long id, Long targetId) {
    if (Objects.equals(id, targetId)) {
      throw new IllegalActionException("illegalAction.user.removeHimself", targetId);
    }

    User entity = this.find(id);
    User target = this.find(targetId);

    if (!entity.hasFriendship(target)) {
      throw new IllegalActionException("illegalAction.user.removeAbsent", targetId);
    }

    entity.setFriends(removeFriend(entity, target));
    target.setFriends(removeFriend(target, entity));
    repository.save(entity);
    repository.save(target);
  }

  @Override
  public Page<User> getFriends(Long id, Pageable pageable) {
    Set<User> friends = this.find(id).getFriends();
    return new PageImpl<>(new ArrayList<>(friends), pageable, friends.size());
  }

  @Override
  public User find(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NotFoundException("notFound.user.byId", id));
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  private static Set<User> addFriend(User user, User target) {
    Set<User> friends = new HashSet<>(user.getFriends());
    friends.add(target);
    return friends;
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  private static Set<User> removeFriend(User user, User target) {
    Set<User> friends = new HashSet<>(user.getFriends());
    friends.remove(target);
    return friends;
  }

}
