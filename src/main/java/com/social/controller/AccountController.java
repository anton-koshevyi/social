package com.social.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.social.common.PrincipalHolder;
import com.social.dto.user.CreateDto;
import com.social.dto.user.DeleteDto;
import com.social.dto.user.PasswordDto;
import com.social.dto.user.UpdateDto;
import com.social.dto.user.UserDto;
import com.social.mapper.UserMapper;
import com.social.model.user.User;
import com.social.service.UserService;

@RestController
public class AccountController {

  private final UserService userService;

  @Autowired
  public AccountController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/account")
  public UserDto get() {
    User account = userService.find(PrincipalHolder.getUserId());
    return UserMapper.INSTANCE.toDto(account);
  }

  @PostMapping("/account")
  public UserDto create(@Valid @RequestBody CreateDto dto,
                        @RequestParam(required = false) boolean autoLogin,
                        HttpServletRequest request) throws ServletException {
    User account = userService.create(
        dto.getEmail(),
        dto.getUsername(),
        dto.getFirstName(),
        dto.getLastName(),
        dto.getPassword()
    );

    if (autoLogin) {
      request.login(
          dto.getUsername(),
          dto.getPassword()
      );
    }

    return UserMapper.INSTANCE.toDto(account);
  }

  @PatchMapping("/account")
  public UserDto update(@Valid @RequestBody UpdateDto dto) {
    User account = userService.update(
        PrincipalHolder.getUserId(),
        dto.getEmail(),
        dto.getUsername(),
        dto.getFirstName(),
        dto.getLastName(),
        dto.getPublicity()
    );
    return UserMapper.INSTANCE.toDto(account);
  }

  @DeleteMapping("/account")
  public void delete(@Valid @RequestBody DeleteDto dto) {
    userService.delete(
        PrincipalHolder.getUserId(),
        dto.getPassword()
    );
  }

  @PutMapping("/account/password")
  public void changePassword(@Valid @RequestBody PasswordDto dto) {
    userService.changePassword(
        PrincipalHolder.getUserId(),
        dto.getActual(),
        dto.getChange()
    );
  }

}
