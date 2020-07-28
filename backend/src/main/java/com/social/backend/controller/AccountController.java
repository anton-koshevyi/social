package com.social.backend.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.dto.user.CreateDto;
import com.social.backend.dto.user.DeleteDto;
import com.social.backend.dto.user.PasswordDto;
import com.social.backend.dto.user.RoleDto;
import com.social.backend.dto.user.UpdateDto;
import com.social.backend.model.user.User;
import com.social.backend.service.UserService;

@RestController
public class AccountController {
    private final UserService userService;
    
    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/account")
    public User account(@AuthenticationPrincipal(expression = "id") Long id) {
        return userService.findById(id);
    }
    
    @PostMapping("/account")
    public User created(@Valid @RequestBody CreateDto dto,
                        HttpServletRequest request) throws ServletException {
        String email = dto.getEmail();
        String username = dto.getUsername();
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String password = dto.getPassword();
        
        User account = userService.create(email, username, firstName, lastName, password);
        request.login(dto.getUsername(), dto.getPassword());
        return account;
    }
    
    @PatchMapping("/account")
    public User updated(@AuthenticationPrincipal(expression = "id") Long id,
                        @Valid @RequestBody UpdateDto dto) {
        // TODO: Implement as PATCH-request
        String email = dto.getEmail();
        String username = dto.getUsername();
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        Integer publicity = dto.getPublicity();
        return userService.update(id, email, username, firstName, lastName, publicity);
    }
    
    @DeleteMapping("/account")
    public void delete(@AuthenticationPrincipal(expression = "id") Long id,
                       @Valid @RequestBody DeleteDto dto) {
        String password = dto.getPassword();
        userService.delete(id, password);
    }
    
    @PutMapping("/account/password")
    public void changePassword(@AuthenticationPrincipal(expression = "id") Long id,
                               @Valid @RequestBody PasswordDto dto) {
        String actual = dto.getActual();
        String change = dto.getChange();
        userService.changePassword(id, actual, change);
    }
    
    @PatchMapping("/account/role")
    public User updatedRole(@AuthenticationPrincipal(expression = "id") Long id,
                            @Valid @RequestBody RoleDto dto) {
        // TODO: Implement as PATCH-request
        Boolean moder = dto.getModer();
        return userService.updateRole(id, moder);
    }
}
