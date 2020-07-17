package com.social.backend.util;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.social.backend.config.IdentifiedUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticationUtilTest {
    @Test
    public void getPrincipal_exception_onNullAuthentication() {
        assertThatThrownBy(() -> AuthenticationUtil.getPrincipal(null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void getPrincipal_null_onNullPrincipal() {
        assertThat(AuthenticationUtil.getPrincipal(new TestingAuthenticationToken(null, null)))
                .isNull();
    }
    
    @Test
    public void getPrincipal_null_whenPrincipalTypeIsNotExpected() {
        UserDetails principal = new User(
                "username",
                "password",
                Collections.emptySet()
        );
        assertThat(AuthenticationUtil.getPrincipal(new TestingAuthenticationToken(principal, null)))
                .isNull();
    }
    
    @Test
    public void getPrincipal() {
        UserDetails principal = new IdentifiedUserDetails(
                1L,
                "username",
                "password",
                Collections.emptySet()
        );
        assertThat(AuthenticationUtil.getPrincipal(new TestingAuthenticationToken(principal, null)))
                .isExactlyInstanceOf(IdentifiedUserDetails.class)
                .isEqualToComparingFieldByField(new IdentifiedUserDetails(
                        1L,
                        "username",
                        "password",
                        Collections.emptySet()
                ));
    }
}
