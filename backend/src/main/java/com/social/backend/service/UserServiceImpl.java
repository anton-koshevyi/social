package com.social.backend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.dto.user.CreateDto;
import com.social.backend.dto.user.DeleteDto;
import com.social.backend.dto.user.PasswordDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UpdateDto;
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
    public User create(CreateDto dto) {
        User entity = new User()
                .setEmail(dto.getEmail())
                .setUsername(dto.getUsername())
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(entity);
    }
    
    @Override
    public User update(Long id, UpdateDto dto) {
        User entity = this.findById(id);
        entity.setEmail(dto.getEmail())
                .setUsername(dto.getUsername())
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setPublicity(dto.getPublicity());
        return userRepository.save(entity);
    }
    
    @Override
    public User updateRole(Long id, RoleDto dto) {
        User entity = this.findById(id);
        entity.setModer(dto.getModer());
        return userRepository.save(entity);
    }
    
    @Override
    public void changePassword(Long id, PasswordDto dto) {
        User entity = this.findById(id);
        
        if (!passwordEncoder.matches(dto.getActual(), entity.getPassword())) {
            throw new WrongCredentialsException("wrongCredentials.password");
        }
        
        entity.setPassword(passwordEncoder.encode(dto.getChange()));
        userRepository.save(entity);
    }
    
    @Override
    public void delete(Long id, DeleteDto dto) {
        User entity = this.findById(id);
        
        if (!passwordEncoder.matches(dto.getPassword(), entity.getPassword())) {
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
    
        entity.getFriends().add(target);
        target.getFriends().add(entity);
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
    
        removeFriend(entity, target);
        removeFriend(target, entity);
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
    private static void removeFriend(User user, User target) {
        List<User> friends = user.getFriends();
        
        for (int i = 0; i < friends.size(); i++) {
            User friend = friends.get(i);
            
            if (Objects.equals(friend.getId(), target.getId())) {
                friends.remove(i);
                return;
            }
        }
    }
}
