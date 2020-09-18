package com.social.backend.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.User;
import com.social.backend.util.AuthenticationUtil;

@Mapper
public abstract class UserMapper {

  public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
  public UserDto toDto(User model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      if (model.isPublic()) {
        // Unable to check request from administration
        logger.debug("Null authentication - regular 'public' body");
        return regular(model);
      }

      logger.debug("Null authentication - hidden body");
      return hidden(model);
    }

    SecurityExpressionRoot security = new SecurityExpressionRoot(authentication) {};
    security.setTrustResolver(new AuthenticationTrustResolverImpl());

    if (security.hasAnyAuthority(Authority.MODER, Authority.ADMIN)) {
      // Administration role-based access logic will be here.
      // E.g. Moder cannot get extended data of another moder.
      logger.debug("Administration principal - extended body");
      return regular(model);
    }

    IdentifiedUserDetails principal = AuthenticationUtil.getPrincipal(authentication);

    if (principal != null && principal.getId().equals(model.getId())) {
      logger.debug("Owner principal - regular body");
      return regular(model);
    }

    if (model.isPublic()) {
      // Request is not from administration
      logger.debug("Regular 'public' body");
      return regular(model);
    }

    // TODO: Authenticated check duplication

    if (model.isInternal() && security.isAuthenticated()) {
      logger.debug("Authenticated - regular 'internal' body");
      return regular(model);
    }

    logger.debug("Hidden body by default");
    return hidden(model);

  }

  private static UserDto regular(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setEmail(user.getEmail());
    dto.setUsername(user.getUsername());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setPublicity(user.getPublicity());
    dto.setModer(user.isModer());
    dto.setAdmin(user.isAdmin());
    return dto;
  }

  private static UserDto hidden(User user) {
    UserDto dto = regular(user);
    dto.setEmail(null);
    return dto;
  }

}
