package com.social.service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.exception.IllegalActionException;
import com.social.exception.NotFoundException;
import com.social.exception.WrongCredentialsException;
import com.social.model.user.User;
import com.social.repository.UserRepository;
import com.social.util.NullableUtils;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
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
    return userRepository.save(entity);
  }

  @Transactional
  @Override
  public User update(Long id,
                     String email,
                     String username,
                     String firstName,
                     String lastName,
                     Integer publicity) {
    User entity = this.find(id);
    NullableUtils.set(entity::setEmail, email);
    NullableUtils.set(entity::setUsername, username);
    NullableUtils.set(entity::setFirstName, firstName);
    NullableUtils.set(entity::setLastName, lastName);
    NullableUtils.set(entity::setPublicity, publicity);
    return userRepository.save(entity);
  }

  @Transactional
  @Override
  public User updateRole(Long id, Boolean moder) {
    User entity = this.find(id);
    NullableUtils.set(entity::setModer, moder);
    return userRepository.save(entity);
  }

  @Transactional
  @Override
  public void changePassword(Long id, String actual, String change) {
    User entity = this.find(id);

    if (!passwordEncoder.matches(actual, entity.getPassword())) {
      throw new WrongCredentialsException("wrongCredentials.password");
    }

    entity.setPassword(passwordEncoder.encode(change));
    userRepository.save(entity);
  }

  @Transactional
  @Override
  public void delete(Long id, String password) {
    User entity = this.find(id);

    if (!passwordEncoder.matches(password, entity.getPassword())) {
      throw new WrongCredentialsException("wrongCredentials.password");
    }

    userRepository.delete(entity);
  }

  @Transactional
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

    entity.addFriend(target);
    target.addFriend(entity);
    userRepository.save(entity);
    userRepository.save(target);
  }

  @Transactional
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

    entity.removeFriend(target);
    target.removeFriend(entity);
    userRepository.save(entity);
    userRepository.save(target);
  }

  @Override
  public Page<User> getFriends(Long id, Pageable pageable) {
    Set<User> friends = this.find(id).getFriends();
    return new PageImpl<>(new ArrayList<>(friends), pageable, friends.size());
  }

  @Override
  public User find(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("notFound.user.byId", id));
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

}
