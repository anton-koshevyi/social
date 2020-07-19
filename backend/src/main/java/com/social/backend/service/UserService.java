package com.social.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.social.backend.dto.user.CreateDto;
import com.social.backend.dto.user.DeleteDto;
import com.social.backend.dto.user.PasswordDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UpdateDto;
import com.social.backend.model.user.User;

public interface UserService {
    User findById(Long id);
    
    User create(CreateDto dto);
    
    User update(Long id, UpdateDto dto);
    
    void changePassword(Long id, PasswordDto dto);
    
    void delete(Long id, DeleteDto dto);
    
    void addFriend(Long id, Long friendId);
    
    void removeFriend(Long id, Long friendId);
    
    Page<User> getFriends(Long id, Pageable pageable);
    
    Page<User> findAll(Pageable pageable);
    
    User updateRole(Long id, RoleDto dto);
}
