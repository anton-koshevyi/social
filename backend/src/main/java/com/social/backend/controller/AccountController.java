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
import com.social.backend.dto.user.UpdateDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.UserMapper;
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
  public UserDto get(@AuthenticationPrincipal(expression = "id") Long id) {
    User account = userService.find(id);
    return UserMapper.INSTANCE.toDto(account);
  }

  @PostMapping("/account")
  public UserDto create(@Valid @RequestBody CreateDto dto,
                        HttpServletRequest request) throws ServletException {
    User account = userService.create(
        dto.getEmail(),
        dto.getUsername(),
        dto.getFirstName(),
        dto.getLastName(),
        dto.getPassword()
    );
    request.login(
        dto.getUsername(),
        dto.getPassword()
    );
    return UserMapper.INSTANCE.toDto(account);
  }

  @PatchMapping("/account")
  public UserDto update(@AuthenticationPrincipal(expression = "id") Long id,
                        @Valid @RequestBody UpdateDto dto) {

    User account = userService.update(
        id,
        dto.getEmail(),
        dto.getUsername(),
        dto.getFirstName(),
        dto.getLastName(),
        dto.getPublicity()
    );
    return UserMapper.INSTANCE.toDto(account);
  }

  @DeleteMapping("/account")
  public void delete(@AuthenticationPrincipal(expression = "id") Long id,
                     @Valid @RequestBody DeleteDto dto) {
    userService.delete(
        id,
        dto.getPassword()
    );
  }

  @PutMapping("/account/password")
  public void changePassword(@AuthenticationPrincipal(expression = "id") Long id,
                             @Valid @RequestBody PasswordDto dto) {
    userService.changePassword(
        id,
        dto.getActual(),
        dto.getChange()
    );
  }

}
