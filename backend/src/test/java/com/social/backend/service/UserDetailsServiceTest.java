package com.social.backend.service;

import java.util.Collections;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
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
  public void loadUserByUsername_whenNoUserWithEmailOrUsername_expectException() {
    Assertions
        .assertThatThrownBy(() -> userDetailsService.loadUserByUsername("johnsmith"))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .hasMessage("No user with email or username: johnsmith");
  }

  @Test
  public void loadUserByUsername_whenUserFoundByEmail() {
    Mockito
        .when(userRepository.findByEmail("johnsmith@example.com"))
        .thenReturn(Optional.of(new User()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setPassword("{encoded}password")));

    Assertions
        .assertThat(userDetailsService.loadUserByUsername("johnsmith@example.com"))
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "johnsmith@example.com",
            "{encoded}password",
            Collections.emptySet()
        ));
  }

  @Test
  public void loadUserByUsername_whenUserFoundByUsername() {
    Mockito
        .when(userRepository.findByUsername("johnsmith"))
        .thenReturn(Optional.of(new User()
            .setId(1L)
            .setUsername("johnsmith")
            .setPassword("{encoded}password")
            .setModer(true)
            .setAdmin(true)));

    Assertions
        .assertThat(userDetailsService.loadUserByUsername("johnsmith"))
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "johnsmith",
            "{encoded}password",
            Sets.newLinkedHashSet(
                new SimpleGrantedAuthority(Authority.MODER),
                new SimpleGrantedAuthority(Authority.ADMIN)
            )
        ));
  }

}
