package com.social.backend.config;

import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class IdentifiedUserDetails extends User {
  
  private final Long id;
  
  public IdentifiedUserDetails(
      Long id, String username, String password, Set<GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
  }
  
  public Long getId() {
    return id;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    IdentifiedUserDetails details = (IdentifiedUserDetails) o;
    return Objects.equals(id, details.id);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }
  
}
