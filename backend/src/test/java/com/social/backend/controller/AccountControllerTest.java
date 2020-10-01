package com.social.backend.controller;

import javax.servlet.http.HttpServletResponse;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.user.UserType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class AccountControllerTest {

  private static final FormAuthConfig AUTH_FORM =
      new FormAuthConfig("/auth", "username", "password");

  @LocalServerPort
  private int port;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private TestEntityManager entityManager;

  @BeforeAll
  public static void beforeAll() {
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
  }

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void get() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
  public void create_whenInvalidBody_expectBadRequest() throws JSONException {
    String actual = RestAssured
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .extract()
        .asString();

    String expected = "{"
        + "timestamp: (customized),"
        + "status: 400,"
        + "error: 'Bad Request',"
        + "message: 'Invalid body: 5 error(s)',"
        + "errors: {"
        + "  'email': ['must not be null'],"
        + "  'username': ['must not be null'],"
        + "  'firstName': ['must not be null'],"
        + "  'lastName': ['must not be null'],"
        + "  'password': ['must not be null']"
        + "},"
        + "path: '/account'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void create() throws JSONException {
    String actual = RestAssured
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
  public void update_whenInvalidBody_expectBadRequest() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("fredbloggs", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"email\": \"\" }")
        .when()
        .patch("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .extract()
        .asString();

    String expected = "{"
        + "timestamp: (customized),"
        + "status: 400,"
        + "error: 'Bad Request',"
        + "message: 'Invalid body: 1 error(s)',"
        + "errors: {"
        + "  'email': ['size must be between 1 and 320']"
        + "},"
        + "path: '/account'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void update() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("fredbloggs", "password", AUTH_FORM)
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
  public void delete_whenInvalidBody_expectBadRequest() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .delete("/account")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .extract()
        .asString();

    String expected = "{"
        + "timestamp: (customized),"
        + "status: 400,"
        + "error: 'Bad Request',"
        + "message: 'Invalid body: 1 error(s)',"
        + "errors: {"
        + "  'password': ['must not be null']"
        + "},"
        + "path: '/account'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void delete() {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Content-Type", "application/json")
        .body("{ \"password\": \"password\" }")
        .when()
        .delete("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void changePassword_whenInvalidBody_expectBadRequest() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .put("/account/password")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .extract()
        .asString();

    String expected = "{"
        + "timestamp: (customized),"
        + "status: 400,"
        + "error: 'Bad Request',"
        + "message: 'Invalid body: 3 error(s)',"
        + "errors: {"
        + "  'actual': ["
        + "    'must not be null',"
        + "    \"fields 'actual' and 'change' must not be equal\""
        + "    ],"
        + "  'change': ['must not be null']"
        + "},"
        + "path: '/account/password'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void changePassword() {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Content-Type", "application/json")
        .body("{"
            + "\"actual\": \"password\","
            + "\"change\": \"new password\","
            + "\"confirm\": \"new password\""
            + "}")
        .when()
        .put("/account/password")
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

}
