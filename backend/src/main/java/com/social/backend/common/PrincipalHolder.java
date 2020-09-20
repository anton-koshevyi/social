package com.social.backend.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public final class PrincipalHolder {

  private static final Logger logger = LoggerFactory.getLogger(PrincipalHolder.class);

  private PrincipalHolder() {
  }

  public static IdentifiedUserDetails getPrincipal(Authentication authentication) {
    Assert.notNull(authentication, "Authentication must not be null");
    Object principal = authentication.getPrincipal();
    
    if (principal == null) {
      logger.debug("Principal is null");
      return null;
    }
    
    Class<?> principalType = principal.getClass();
    
    if (!IdentifiedUserDetails.class.isAssignableFrom(principalType)) {
      logger.debug("Wrong principal type: {}", principalType);
      return null;
    }
    
    return (IdentifiedUserDetails) principal;
  }
  
}
