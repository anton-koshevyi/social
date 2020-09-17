package com.social.backend.mapper.model.user;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.user.User;
import com.social.backend.util.AuthenticationUtil;

public class UserMapper extends AbstractMapper<User> {

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
  @Override
  public <R> R map(User model, Class<R> dtoType) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      if (model.isPublic()) {
        // Unable to check request from administration
        logger.debug("Null authentication - regular 'public' body");
        return regular(model, dtoType);
      }

      logger.debug("Null authentication - hidden body");
      return hidden(model, dtoType);
    }

    SecurityExpressionRoot security = new SecurityExpressionRoot(authentication) {};
    security.setTrustResolver(new AuthenticationTrustResolverImpl());

    if (security.hasAnyAuthority(Authority.MODER, Authority.ADMIN)) {
      // Administration role-based access logic will be here.
      // E.g. Moder cannot get extended data of another moder.
      logger.debug("Administration principal - extended body");
      return regular(model, dtoType);
    }

    IdentifiedUserDetails principal = AuthenticationUtil.getPrincipal(authentication);

    if (principal != null && principal.getId().equals(model.getId())) {
      logger.debug("Owner principal - regular body");
      return regular(model, dtoType);
    }

    if (model.isPublic()) {
      // Request is not from administration
      logger.debug("Authenticated - regular 'public' body");
      return regular(model, dtoType);
    }

    // TODO: Authenticated check duplication

    if (model.isInternal() && security.isAuthenticated()) {
      logger.debug("Authenticated - regular 'internal' body");
      return regular(model, dtoType);
    }

    logger.debug("Hidden body by default");
    return hidden(model, dtoType);
  }

  public <R> R regular(User source, Class<R> dtoType) {
    logger.debug("Mapping User to regular UserDto by default");
    UserDto dto = new UserDto();
    dto.setId(source.getId());
    dto.setEmail(source.getEmail());
    dto.setUsername(source.getUsername());
    dto.setFirstName(source.getFirstName());
    dto.setLastName(source.getLastName());
    dto.setPublicity(source.getPublicity());
    dto.setModer(source.isModer());
    dto.setAdmin(source.isAdmin());
    return (R) dto;
  }

  public <R> R hidden(User source, Class<R> dtoType) {
    logger.debug("Mapping User to hidden UserDto by default");
    UserDto dto = regular(source, UserDto.class);
    dto.setEmail(null);
    return (R) dto;
  }

}
