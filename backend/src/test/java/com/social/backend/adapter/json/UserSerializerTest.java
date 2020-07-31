package com.social.backend.adapter.json;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.AbstractJsonMarshalTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.assertj.core.api.Assertions.assertThat;

import static com.social.backend.TestEntity.user;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class UserSerializerTest {
    @Autowired
    private AbstractJsonMarshalTester<User> tester;
    
    @Test
    public void given_notPublicPublicity_when_nullAuthentication_then_hiddenBody() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.INTERNAL);
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_publicPublicity_when_anyRequest_then_regularBody() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.PUBLIC);
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 30,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    @WithMockUser
    public void given_privatePublicity_when_notIdentifiedPrincipal_then_hiddenBodyByDefault() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.PRIVATE);
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_privatePublicity_when_notOwnerRequest_then_hiddenBody() throws IOException {
        authenticate(new IdentifiedUserDetails(
                2L,
                "username",
                "password",
                Collections.emptySet()
        ));
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.PRIVATE);
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_anyPublicity_when_ownerRequest_then_regularBody() throws IOException {
        authenticate(new IdentifiedUserDetails(
                1L,
                "username",
                "password",
                Collections.emptySet()
        ));
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.PRIVATE);
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    @WithAnonymousUser
    public void given_internalPublicity_when_anonymousRequest_then_hiddenBody() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.INTERNAL);
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    @WithMockUser
    public void given_internalPublicity_when_authenticatedRequest_then_regularBody() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.INTERNAL);
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {Publicity.PUBLIC, Publicity.INTERNAL, Publicity.PRIVATE})
    @WithMockUser(authorities = Authority.MODER)
    public void given_anyPublicity_when_administrationRequest_then_extendedBody(int publicity) throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(publicity);
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: (customized),"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                        new Customization("publicity", (act, exp) -> true)
                ));
    }
    
    @Test
    @WithMockUser
    public void given_privatePublicity_when_notOwnerNorAdministrationRequest_hiddenBody() throws IOException {
        User user = user()
                .setId(1L)
                .setPublicity(Publicity.PRIVATE);
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertThat(tester.write(user))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    private static void authenticate(UserDetails details) {
        AuthenticationProvider provider = new TestingAuthenticationProvider();
        Authentication token = new TestingAuthenticationToken(details, details.getPassword());
        Authentication authentication = provider.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
