package com.social.service;

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

import com.social.common.IdentifiedUserDetails;
import com.social.config.SecurityConfig.Authority;
import com.social.repository.UserRepository;
import com.social.test.model.factory.ModelFactory;
import com.social.test.model.mutator.UserMutators;
import com.social.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

  private @Mock UserRepository userRepository;
  private UserDetailsService userDetailsService;

  @BeforeEach
  public void setUp() {
    userDetailsService = new UserDetailsServiceImpl(userRepository);
  }

  @Test
  public void loadUserByUsername_whenNoUserWithEmailOrUsername_expectException() {
    Assertions
        .assertThatThrownBy(() -> userDetailsService.loadUserByUsername("johnsmith@example.com"))
        .isExactlyInstanceOf(UsernameNotFoundException.class)
        .hasMessage("No user with email or username: johnsmith@example.com");
  }

  @Test
  public void loadUserByUsername_whenUserFoundByEmail() {
    Mockito
        .when(userRepository.findByEmail("johnsmith@example.com"))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.RAW,
                UserMutators.id(1L),
                UserMutators.email("johnsmith@example.com"),
                UserMutators.username("johnsmith"),
                UserMutators.password("{encoded}password"))
        ));

    Assertions
        .assertThat(userDetailsService.loadUserByUsername("johnsmith@example.com"))
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "johnsmith",
            "{encoded}password",
            Collections.emptySet()
        ));
  }

  @Test
  public void loadUserByUsername_whenUserFoundByUsername() {
    Mockito
        .when(userRepository.findByUsername("johnsmith"))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(UserType.RAW,
                UserMutators.id(1L),
                UserMutators.username("johnsmith"),
                UserMutators.password("{encoded}password"),
                UserMutators.moder(true),
                UserMutators.admin(true))
        ));

    Assertions
        .assertThat(userDetailsService.loadUserByUsername("johnsmith"))
        .isEqualTo(new IdentifiedUserDetails(
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
