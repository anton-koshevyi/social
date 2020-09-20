package com.social.backend.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

@ExtendWith(SpringExtension.class)
public class UserMapperTest {

  @Test
  public void givenNotPublicPublicity_whenNullAuthentication_thenHiddenBody() {
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
  public void givenPublicPublicity_whenNullAuthentication_thenRegularBody() {
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
  public void givenAnyPublicity_whenAdministrationRequest_thenRegularBody(int publicity) {
    authenticate(new IdentifiedUserDetails(
        2L,
        "administration",
        "password",
        Collections.singleton(new SimpleGrantedAuthority(Authority.MODER))
    ));
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
  public void givenPrivatePublicity_whenNotOwnerRequest_thenHiddenBody() {
    authenticate(new IdentifiedUserDetails(
        2L,
        "notOwner",
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
  public void givenAnyPublicity_whenOwnerRequest_thenRegularBody() {
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
  public void givenPublicPublicity_whenNotOwnerNorAdministrationRequest_thenRegularBody() {
    authenticate(new IdentifiedUserDetails(
        2L,
        "notOwnerNorAdministration",
        "password",
        Collections.emptySet()
    ));
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
  public void givenInternalPublicity_whenAnonymousRequest_thenHiddenBody() {
    anonymous(new IdentifiedUserDetails(
        2L,
        "anonymous",
        "password",
        Collections.emptySet()
    ));
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
  public void givenInternalPublicity_whenAuthenticatedRequest_thenRegularBody() {
    authenticate(new IdentifiedUserDetails(
        2L,
        "authenticated",
        "password",
        Collections.emptySet()
    ));
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
  public void givenPrivatePublicity_whenNotOwnerNorAdministrationRequest_thenHiddenBody() {
    authenticate(new IdentifiedUserDetails(
        2L,
        "notOwnerNorAdministration",
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

  public static void anonymous(UserDetails userDetails) {
    List<GrantedAuthority> authorities =
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
    authorities.addAll(userDetails.getAuthorities());
    Authentication token = new AnonymousAuthenticationToken(
        "key", userDetails, authorities);
    SecurityContextHolder.getContext().setAuthentication(token);
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
