package com.social.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User create(String email, String username, String firstName, String lastName, String password) {
        User entity = new User();
        entity.setEmail(email);
        entity.setUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setPassword(passwordEncoder.encode(password));
        return userRepository.save(entity);
    }
    
    @Override
    public User update(Long id, String email, String username, String firstName, String lastName, Integer publicity) {
        User entity = this.findById(id);
        entity.setEmail(email);
        entity.setUsername(username);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setPublicity(publicity);
        return userRepository.save(entity);
    }
    
    @Override
    public User updateRole(Long id, Boolean moder) {
        User entity = this.findById(id);
        entity.setModer(moder);
        return userRepository.save(entity);
    }
    
    @Override
    public void changePassword(Long id, String actual, String change) {
        User entity = this.findById(id);
        
        if (!passwordEncoder.matches(actual, entity.getPassword())) {
            throw new WrongCredentialsException("wrongCredentials.password");
        }
        
        entity.setPassword(passwordEncoder.encode(change));
        userRepository.save(entity);
    }
    
    @Override
    public void delete(Long id, String password) {
        User entity = this.findById(id);
        
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new WrongCredentialsException("wrongCredentials.password");
        }
        
        userRepository.delete(entity);
    }
    
    @Override
    public void addFriend(Long id, Long targetId) {
        if (Objects.equals(id, targetId)) {
            throw new IllegalActionException("illegalAction.user.addHimself");
        }
        
        User entity = this.findById(id);
        User target = this.findById(targetId);
        
        if (target.isPrivate()) {
            throw new IllegalActionException("illegalAction.user.addPrivate", targetId);
        }
        
        if (entity.hasFriendship(target)) {
            throw new IllegalActionException("illegalAction.user.addPresent", targetId);
        }
        
        entity.setFriends(addFriend(entity, target));
        target.setFriends(addFriend(target, entity));
        userRepository.save(entity);
        userRepository.save(target);
    }
    
    @Override
    public void removeFriend(Long id, Long targetId) {
        if (Objects.equals(id, targetId)) {
            throw new IllegalActionException("illegalAction.user.removeHimself", targetId);
        }
        
        User entity = this.findById(id);
        User target = this.findById(targetId);
        
        if (!entity.hasFriendship(target)) {
            throw new IllegalActionException("illegalAction.user.removeAbsent", targetId);
        }
        
        entity.setFriends(removeFriend(entity, target));
        target.setFriends(removeFriend(target, entity));
        userRepository.save(entity);
        userRepository.save(target);
    }
    
    @Override
    public Page<User> getFriends(Long id, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        List<User> friends = this.findById(id).getFriends();
        return new PageImpl<>(friends, pageable, friends.size());
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("notFound.user.byId", id));
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        return userRepository.findAll(pageable);
    }
    
    @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
    private static List<User> addFriend(User user, User target) {
        List<User> friends = new ArrayList<>(user.getFriends());
        friends.add(target);
        return friends;
    }
    
    @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
    private static List<User> removeFriend(User user, User target) {
        List<User> friends = new ArrayList<>(user.getFriends());
        
        for (int i = 0; i < friends.size(); i++) {
            User friend = friends.get(i);
            
            if (Objects.equals(friend.getId(), target.getId())) {
                friends.remove(i);
                break;
            }
        }
        
        return friends;
    }
}
