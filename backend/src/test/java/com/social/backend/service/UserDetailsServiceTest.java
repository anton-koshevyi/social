package com.social.backend.service;

import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {
  
  @Mock
  private UserRepository userRepository;
  private UserDetailsService userDetailsService;
  
  @BeforeEach
  public void setUp() {
    userDetailsService = new UserDetailsServiceImpl(userRepository);
  }
  
  @Test
  public void loadUserByUsername_exception_whenNoUserWithEmailOrUsername() {
    Assertions
        .assertThatThrownBy(() -> userDetailsService.loadUserByUsername("username"))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .hasMessage("No user with email or username: username");
  }
  
  @Test
  public void loadUserByUsername_whenUserFoundByEmail() {
    Mockito
        .when(userRepository.findByEmail("email@mail.com"))
        .thenReturn(Optional.of(new User()
            .setId(1L)
            .setEmail("email@mail.com")
            .setPassword("password")));
    
    Assertions
        .assertThat(userDetailsService.loadUserByUsername("email@mail.com"))
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "email@mail.com",
            "password",
            Collections.emptySet()
        ));
  }
  
  @Test
  public void loadUserByUsername_whenUserFoundByUsername() {
    Mockito
        .when(userRepository.findByUsername("username"))
        .thenReturn(Optional.of(new User()
            .setId(1L)
            .setUsername("username")
            .setPassword("password")
            .setModer(true)
            .setAdmin(true)));
    
    Assertions
        .assertThat(userDetailsService.loadUserByUsername("username"))
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "username",
            "password",
            ImmutableSet.of(
                new SimpleGrantedAuthority(Authority.ADMIN),
                new SimpleGrantedAuthority(Authority.MODER)
            )
        ));
  }
  
}
