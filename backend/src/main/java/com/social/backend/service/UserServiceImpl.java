package com.social.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.social.backend.dto.user.CreateDto;
import com.social.backend.dto.user.DeleteDto;
import com.social.backend.dto.user.PasswordDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UpdateDto;
import com.social.backend.exception.NotAvailableException;
import com.social.backend.exception.NotFoundException;
import com.social.backend.exception.WrongCredentialsException;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;

@Service
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
                .setPublicity(Publicity.PRIVATE)
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
        User entity = this.findById(id);
        User target = this.findById(targetId);
    
        if (target.getPublicity() == Publicity.PRIVATE) {
            throw new NotAvailableException("notAvailable.user.privateAccount", targetId);
        }
    
        List<User> friends = entity.getFriends();
        friends.add(target);
        userRepository.save(entity);
    }
    
    @Override
    public void removeFriend(Long id, Long targetId) {
        User entity = this.findById(id);
        User target = this.findById(targetId);
        List<User> friends = entity.getFriends();
        
        if (!friends.contains(target)) {
            throw new NotAvailableException("notAvailable.user.absentFriend", targetId);
        }
        
        friends.remove(target);
        userRepository.save(entity);
    }
    
    @Override
    public Page<User> getFriends(Long id, Pageable pageable) {
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
        return userRepository.findAll(pageable);
    }
}
