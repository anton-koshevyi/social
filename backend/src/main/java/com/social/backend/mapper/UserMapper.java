package com.social.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.common.PrincipalHolder;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.User;

@Mapper
public abstract class UserMapper {

  public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

  @Named("UserMapper.private.toDtoRegular")
  protected abstract UserDto toDtoRegular(User model);

  @Named("UserMapper.private.toDtoHidden")
  @Mapping(target = "email", ignore = true)
  protected abstract UserDto toDtoHidden(User model);

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
  public UserDto toDto(User model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      if (model.isPublic()) {
        // Unable to check request from administration
        logger.debug("Null authentication - regular 'public' body");
        return this.toDtoRegular(model);
      }

      logger.debug("Null authentication - hidden body");
      return this.toDtoHidden(model);
    }

    SecurityExpressionRoot security = new SecurityExpressionRoot(authentication) {};
    security.setTrustResolver(new AuthenticationTrustResolverImpl());

    if (security.hasAnyAuthority(Authority.MODER, Authority.ADMIN)) {
      // Administration role-based access logic will be here.
      // E.g. Moder cannot get extended data of another moder.
      logger.debug("Administration principal - extended body");
      return this.toDtoRegular(model);
    }

    IdentifiedUserDetails principal = PrincipalHolder.getPrincipal();

    if (principal != null && principal.getId().equals(model.getId())) {
      logger.debug("Owner principal - regular body");
      return this.toDtoRegular(model);
    }


    if (model.isPublic()) {
      // Request is not from administration
      logger.debug("Regular 'public' body");
      return this.toDtoRegular(model);
    }

    // TODO: Authenticated check duplication

    if (model.isInternal() && security.isAuthenticated()) {
      logger.debug("Authenticated - regular 'internal' body");
      return this.toDtoRegular(model);
    }

    logger.debug("Hidden body by default");
    return this.toDtoHidden(model);
  }

}
