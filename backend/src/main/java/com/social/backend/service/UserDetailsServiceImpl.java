package com.social.backend.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  
  private final UserRepository userRepository;
  
  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @Override
  public UserDetails loadUserByUsername(String username) {
    User entity = userRepository.findByEmail(username)
        .orElseGet(() -> userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "No user with email or username: " + username)));
    Set<String> authorities = new HashSet<>();
    
    if (entity.isModer()) {
      authorities.add(Authority.MODER);
    }
    
    if (entity.isAdmin()) {
      authorities.add(Authority.ADMIN);
    }
    
    return new IdentifiedUserDetails(
        entity.getId(),
        username,
        entity.getPassword(),
        authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet())
    );
  }
  
}
