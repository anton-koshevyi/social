package com.social.common;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class PrincipalHolderTest {

  @AfterEach
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void getPrincipal_whenNullAuthentication_expectNull() {
    Assertions
        .assertThat(PrincipalHolder.getPrincipal())
        .isNull();
  }

  @Test
  public void getPrincipal_whenNullPrincipal_expectNull() {
    Authentication authentication = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Assertions
        .assertThat(PrincipalHolder.getPrincipal())
        .isNull();
  }

  @Test
  public void getPrincipal_whenNotExpectedPrincipalType_expectNull() {
    UserDetails principal = new User(
        "johnsmith",
        "password",
        Collections.emptySet()
    );
    Authentication authentication = new TestingAuthenticationToken(principal, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Assertions
        .assertThat(PrincipalHolder.getPrincipal())
        .isNull();
  }

  @Test
  public void getPrincipal() {
    UserDetails principal = new IdentifiedUserDetails(
        1L,
        "johnsmith",
        "password",
        Collections.emptySet()
    );
    Authentication authentication = new TestingAuthenticationToken(principal, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Assertions
        .assertThat(PrincipalHolder.getPrincipal())
        .isEqualToComparingFieldByField(new IdentifiedUserDetails(
            1L,
            "johnsmith",
            "password",
            Collections.emptySet()
        ));
  }

  @Test
  public void getUserId_whenNullPrincipal_expectException() {
    Authentication authentication = new TestingAuthenticationToken(null, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Assertions
        .assertThatThrownBy(PrincipalHolder::getUserId)
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage("Principal is null");
  }

  @Test
  public void getUserId() {
    UserDetails principal = new IdentifiedUserDetails(
        1L,
        "johnsmith",
        "password",
        Collections.emptySet()
    );
    Authentication authentication = new TestingAuthenticationToken(principal, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Assertions
        .assertThat(PrincipalHolder.getUserId())
        .isEqualTo(1L);
  }

}
