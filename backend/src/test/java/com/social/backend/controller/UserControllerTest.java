package com.social.backend.controller;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Sets;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.json.JSONException;
import org.json.JSONObject;
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
public class UserControllerTest {

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
  public void getAll() throws JSONException {
    entityManager.persist(TestEntity.user());
    TestTransaction.end();

    String response = RestAssured
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/users")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();
    String actual = new JSONObject(response)
        .getJSONArray("content")
        .toString();

    String expected = "[{"
        + "id: 1,"
        + "username: 'username',"
        + "firstName: 'first',"
        + "lastName: 'last',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void get() throws JSONException {
    entityManager.persist(TestEntity.user());
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/users/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
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
  public void updateRole_badRequest_whenBodyInvalid() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setEmail("admin@mail.com")
        .setUsername("admin")
        .setPassword(passwordEncoder.encode("password"))
        .setAdmin(true));
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("admin", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .patch("/users/{id}/roles", 2)
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
        + "  'moder': ['must not be null']"
        + "},"
        + "path: '/users/2/roles'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> true)
        ));
  }

  @Test
  public void updateRole() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setEmail("admin@mail.com")
        .setUsername("admin")
        .setPassword(passwordEncoder.encode("password"))
        .setAdmin(true));
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("admin", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"moder\": true }")
        .when()
        .patch("/users/{id}/roles", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 2,"
        + "email: 'user@mail.com',"
        + "username: 'user',"
        + "firstName: 'first',"
        + "lastName: 'last',"
        + "publicity: 10,"
        + "moder: true,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void getFriends() throws JSONException {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("email_1@mail.com")
        .setUsername("username_1"));
    entityManager.persist(TestEntity
        .user()
        .setEmail("email_2@mail.com")
        .setUsername("username_2")
        .setFriends(Sets
            .newHashSet(user)));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();
    String actual = new JSONObject(response)
        .getJSONArray("content")
        .toString();

    String expected = "[{"
        + "id: 1,"
        + "username: 'username_1',"
        + "firstName: 'first',"
        + "lastName: 'last',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void addFriend() {
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("username_2")
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("user", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .post("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void removeFriend() {
    User user = entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    User target = entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setFriends(Sets.newHashSet(user)));
    user.setFriends(Sets.newHashSet(target));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("user", "password", new FormAuthConfig("/auth", "username", "password"))
        .when()
        .delete("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void getPosts() throws JSONException {
    User author = entityManager.persist(TestEntity.user());
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/users/{id}/posts", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();
    String actual = new JSONObject(response)
        .getJSONArray("content")
        .toString();

    String expected = "[{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "title: 'title',"
        + "body: 'post body',"
        + "comments: 0,"
        + "author: {"
        + "  id: 1,"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("[*].createdAt", (act, exp) -> true)
        ));
  }

  @Test
  public void createPrivateChat() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("user", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .post("/users/{id}/chats/private", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'private',"
        + "members: [{"
        + "  id: 1,"
        + "  email: 'user@mail.com',"
        + "  username: 'user',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "{"
        + "  id: 2,"
        + "  email: 'target@mail.com',"
        + "  username: 'target',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 30,"
        + "  moder: false,"
        + "  admin: false"
        + "}]"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

}
