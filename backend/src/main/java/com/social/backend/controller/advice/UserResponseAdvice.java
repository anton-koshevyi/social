package com.social.backend.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.ResponseMapper;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.User;
import com.social.backend.util.AuthenticationUtil;

@ControllerAdvice
public class UserResponseAdvice extends SafeResponseBodyAdvice<User, UserDto> {
    @Autowired
    public UserResponseAdvice(ResponseMapper<User, UserDto> responseMapper) {
        super(responseMapper);
    }
    
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:JavaNCSS"})
    @Override
    public UserDto beforeBodyWriteSafely(User user,
                                         MethodParameter returnType,
                                         MediaType selectedContentType,
                                         Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                         ServerHttpRequest request,
                                         ServerHttpResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            if (user.isPublic()) {
                logger.debug("Regular body for all users");
                return responseMapper.map(user);
            }
            
            logger.debug("Hidden body for null authentication");
            return responseMapper.mapHidden(user);
        }
        
        IdentifiedUserDetails principal = AuthenticationUtil.getPrincipal(authentication);
        
        if (principal != null && principal.getId().equals(user.getId())) {
            logger.debug("Regular body for owner");
            return responseMapper.map(user);
        }
        
        SecurityExpressionRoot security = new SecurityExpressionRoot(authentication) {};
        security.setTrustResolver(new AuthenticationTrustResolverImpl());
        
        if (security.hasAnyAuthority(Authority.MODER, Authority.ADMIN)) {
            // Administration role-based access logic will be here.
            // E.g. Moder cannot get extended data of another moder.
            logger.debug("Extended body for administration");
            return responseMapper.mapExtended(user);
        }
        
        if (user.isInternal() && security.isAuthenticated()) {
            logger.debug("Regular body for authenticated");
            return responseMapper.map(user);
        }
        
        logger.debug("Hidden body by default");
        return responseMapper.mapHidden(user);
    }
}
