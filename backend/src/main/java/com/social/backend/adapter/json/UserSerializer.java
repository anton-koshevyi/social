package com.social.backend.adapter.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.User;
import com.social.backend.util.AuthenticationUtil;

@JsonComponent
public class UserSerializer
        extends AbstractSerializer<User>
        implements EntityMapper<User, UserDto> {
    private final Logger logger = LoggerFactory.getLogger(UserSerializer.class);
    
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
    @Override
    public Object beforeSerialize(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            if (user.isPublic()) {
                logger.debug("Regular body for all users");
                return this.map(user);
            }
            
            logger.debug("Hidden body for null authentication");
            return this.mapHidden(user);
        }
        
        IdentifiedUserDetails principal = AuthenticationUtil.getPrincipal(authentication);
        
        if (principal != null && principal.getId().equals(user.getId())) {
            logger.debug("Regular body for owner");
            return this.map(user);
        }
        
        SecurityExpressionRoot security = new SecurityExpressionRoot(authentication) {};
        security.setTrustResolver(new AuthenticationTrustResolverImpl());
        
        if (security.hasAnyAuthority(Authority.MODER, Authority.ADMIN)) {
            // Administration role-based access logic will be here.
            // E.g. Moder cannot get extended data of another moder.
            logger.debug("Extended body for administration");
            return this.mapExtended(user);
        }
        
        if (user.isInternal() && security.isAuthenticated()) {
            logger.debug("Regular body for authenticated");
            return this.map(user);
        }
        
        logger.debug("Hidden body by default");
        return this.mapHidden(user);
    }
    
    @Override
    public UserDto map(User source) {
        if (source == null) {
            return null;
        }
        
        return new UserDto()
                .setId(source.getId())
                .setEmail(source.getEmail())
                .setUsername(source.getUsername())
                .setFirstName(source.getFirstName())
                .setLastName(source.getLastName())
                .setPublicity(source.getPublicity())
                .setModer(source.isModer())
                .setAdmin(source.isAdmin());
    }
    
    @Override
    public UserDto mapHidden(User source) {
        return this.map(source)
                .setEmail(null);
    }
}
