package com.social.backend.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityManager {

  private SecurityManager() {
  }

  public static void setAnonymousUser(UserDetails userDetails) {
    Objects.requireNonNull(userDetails, "UserDetails must not be null");
    List<GrantedAuthority> authorities =
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
    authorities.addAll(userDetails.getAuthorities());
    Authentication token = new AnonymousAuthenticationToken(
        SecurityManager.class.getName(), userDetails, authorities);
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  public static void setUser(UserDetails userDetails) {
    Objects.requireNonNull(userDetails, "UserDetails must not be null");
    List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
    Authentication token = new TestingAuthenticationToken(
        userDetails, userDetails.getPassword(), authorities);
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  public static List<GrantedAuthority> createAuthorities(String... authorities) {
    return AuthorityUtils.createAuthorityList(authorities);
  }

  public static void clearContext() {
    SecurityContextHolder.clearContext();
  }

}
