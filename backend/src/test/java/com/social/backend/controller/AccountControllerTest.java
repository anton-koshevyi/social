package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.model.user.User;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.UserServiceImpl;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;
import com.social.backend.validator.EmailValidator;
import com.social.backend.validator.UsernameValidator;

public class AccountControllerTest {

  private IdentificationContext<User> identification;
  private UserRepository repository;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new UserRepositoryStub(identification);

    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PasswordEncoderStub.class);
    appContext.registerBean(UserRepository.class, () -> repository);
    appContext.registerBean(UserServiceImpl.class);
    appContext.registerBean(EmailValidator.class);
    appContext.registerBean(UsernameValidator.class);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(WebMvcConfigurationSupport.class);
    webContext.register(AccountController.class);
    webContext.refresh();

    RestAssuredMockMvc.mockMvc(MockMvcBuilders
        .webAppContextSetup(webContext)
        .alwaysDo(MockMvcResultHandlers.log())
        .build());
  }

  @Test
  public void get() throws JSONException {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "email: 'johnsmith@example.com',"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void create_whenInvalidBody_expectException() {
    identification.setStrategy(e -> e.setId(1L));

    RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  @Disabled("Perform auto-login when required")
  public void create() throws JSONException {
    identification.setStrategy(e -> e.setId(1L));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"email\": \"johnsmith@example.com\","
            + "\"username\": \"johnsmith\","
            + "\"firstName\": \"John\","
            + "\"lastName\": \"Smith\","
            + "\"password\": \"password\","
            + "\"confirm\": \"password\""
            + "}")
        .when()
        .post("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "email: 'johnsmith@example.com',"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void update_whenInvalidBody_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "fredbloggs", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"username\": \"\" }")
        .when()
        .patch("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void update() throws JSONException {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "fredbloggs", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"email\": \"johnsmith@example.com\","
            + "\"username\": \"johnsmith\","
            + "\"firstName\": \"John\","
            + "\"lastName\": \"Smith\","
            + "\"publicity\": 30"
            + "}")
        .when()
        .patch("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "email: 'johnsmith@example.com',"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 30,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void delete_whenInvalidBody_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .delete("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void delete() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{ \"password\": \"password\" }")
        .when()
        .delete("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void changePassword_whenInvalidBody_expectException() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .put("/account/password")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void changePassword() {
    identification.setStrategy(e -> e.setId(1L));
    repository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{"
            + "\"actual\": \"password\","
            + "\"change\": \"newPassword\","
            + "\"confirm\": \"newPassword\""
            + "}")
        .when()
        .put("/account/password")
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

}
