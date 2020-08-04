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

import com.social.backend.TestEntity;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class AccountControllerTest {

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
    entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .get("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

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
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void create_badRequest_whenInvalidBody() throws JSONException {
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
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void create_andAutoLogin() throws JSONException {
    String actual = RestAssured
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"id\": 1,"
            + "\"email\": \"email@mail.com\","
            + "\"username\": \"username\","
            + "\"firstName\": \"first\","
            + "\"lastName\": \"last\","
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
        + "email: 'email@mail.com',"
        + "username: 'username',"
        + "firstName: 'first',"
        + "lastName: 'last',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void update_badRequest_whenInvalidBody() throws JSONException {
    entityManager.persist(new User()
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
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
        + "message: 'Invalid body: 5 error(s)',"
        + "errors: {"
        + "  'email': ['must not be null'],"
        + "  'username': ['must not be null'],"
        + "  'firstName': ['must not be null'],"
        + "  'lastName': ['must not be null'],"
        + "  'publicity': ['must not be null']"
        + "},"
        + "path: '/account'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void update() throws JSONException {
    entityManager.persist(new User()
        .setEmail("email@mail.com")
        .setUsername("username")
        .setFirstName("first")
        .setLastName("last")
        .setPublicity(Publicity.PRIVATE)
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"email\": \"new_email@mail.com\","
            + "\"username\": \"new_username\","
            + "\"firstName\": \"new_first\","
            + "\"lastName\": \"new_last\","
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
        + "email: 'new_email@mail.com',"
        + "username: 'new_username',"
        + "firstName: 'new_first',"
        + "lastName: 'new_last',"
        + "publicity: 30,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void delete_badRequest_whenInvalidBody() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
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
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void delete() {
    entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Content-Type", "application/json")
        .body("{ \"password\": \"password\" }")
        .when()
        .delete("/account")
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void changePassword_badRequest_whenInvalidBody() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
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
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void changePassword() {
    entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
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
