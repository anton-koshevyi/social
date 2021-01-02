package com.social.mapper;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.social.common.IdentifiedUserDetails;
import com.social.config.SecurityConfig.Authority;
import com.social.dto.user.UserDto;
import com.social.model.user.Publicity;
import com.social.model.user.User;
import com.social.test.SecurityManager;

public class UserMapperTest {

  @AfterEach
  public void tearDown() {
    SecurityManager.clearContext();
  }

  @Test
  public void givenNotPublicPublicity_whenNullAuthentication_thenHiddenBody() {
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenPublicPublicity_whenNullAuthentication_thenRegularBody() {
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PUBLIC)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.PUBLIC)
            .setModer(false)
            .setAdmin(false));
  }

  @ParameterizedTest
  @ValueSource(ints = {Publicity.PUBLIC, Publicity.INTERNAL, Publicity.PRIVATE})
  public void givenAnyPublicity_whenAdministrationRequest_thenRegularBody(int publicity) {
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "administration",
        "password",
        SecurityManager.createAuthorities(Authority.MODER)
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(publicity)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(publicity)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenPrivatePublicity_whenNotOwnerRequest_thenHiddenBody() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "notOwner",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenAnyPublicity_whenOwnerRequest_thenRegularBody() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L,
        "johnsmith",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenPublicPublicity_whenNotOwnerNorAdministrationRequest_thenRegularBody() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "notOwnerNorAdministration",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PUBLIC)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.PUBLIC)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenInternalPublicity_whenAnonymousRequest_thenHiddenBody() {
    SecurityManager.setAnonymousUser(new IdentifiedUserDetails(
        2L,
        "anonymous",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenInternalPublicity_whenAuthenticatedRequest_thenRegularBody() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "authenticated",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.INTERNAL)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail("johnsmith@example.com")
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.INTERNAL)
            .setModer(false)
            .setAdmin(false));
  }

  @Test
  public void givenPrivatePublicity_whenNotOwnerNorAdministrationRequest_thenHiddenBody() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "notOwnerNorAdministration",
        "password",
        Collections.emptySet()
    ));
    User user = new User()
        .setId(1L)
        .setEmail("johnsmith@example.com")
        .setUsername("johnsmith")
        .setFirstName("John")
        .setLastName("Smith")
        .setPublicity(Publicity.PRIVATE)
        .setPassword("{encoded}password");

    Assertions
        .assertThat(UserMapper.INSTANCE.toDto(user))
        .usingRecursiveComparison()
        .isEqualTo(new UserDto()
            .setId(1L)
            .setEmail(null)
            .setUsername("johnsmith")
            .setFirstName("John")
            .setLastName("Smith")
            .setPublicity(Publicity.PRIVATE)
            .setModer(false)
            .setAdmin(false));
  }

}
