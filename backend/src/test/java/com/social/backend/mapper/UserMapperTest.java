package com.social.backend.mapper;

import java.util.ArrayList;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

@ExtendWith(SpringExtension.class)
public class UserMapperTest {

  @Test
  public void given_notPublicPublicity_when_nullAuthentication_then_hiddenBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void given_publicPublicity_when_nullAuthentication_then_regularBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PUBLIC)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PUBLIC)
            .setModer(false)
            .setAdmin(false));
  }

  @ParameterizedTest
  @ValueSource(ints = {Publicity.PUBLIC, Publicity.INTERNAL, Publicity.PRIVATE})
  @WithMockUser(authorities = Authority.MODER)
  public void given_anyPublicity_when_administrationRequest_then_extendedBody(int publicity) {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(publicity)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(publicity)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  @WithMockUser
  public void given_privatePublicity_when_notIdentifiedPrincipal_then_hiddenBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void given_privatePublicity_when_notOwnerRequest_then_hiddenBody() {
    authenticate(new IdentifiedUserDetails(
        2L,
        "username",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void given_anyPublicity_when_ownerRequest_then_regularBody() {
    authenticate(new IdentifiedUserDetails(
        1L,
        "username",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  @WithAnonymousUser
  public void given_publicPublicity_when_notOwnerNorAdministrationRequest_then_regularBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PUBLIC)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PUBLIC)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  @WithAnonymousUser
  public void given_internalPublicity_when_anonymousRequest_then_hiddenBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  @WithMockUser
  public void given_internalPublicity_when_authenticatedRequest_then_regularBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("email@mail.com")
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  @WithMockUser
  public void given_privatePublicity_when_notOwnerNorAdministrationRequest_hiddenBody() {
    User user = new User()
        .setId(1L)
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("encoded");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("username")
            .setFirstName("first")
            .setLastName("last")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  private static void authenticate(UserDetails details) {
    AuthenticationProvider provider = new TestingAuthenticationProvider();
    Authentication token = new TestingAuthenticationToken(
        details,
        details.getPassword(),
        new ArrayList<>(details.getAuthorities())
    );
    Authentication authentication = provider.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

}
