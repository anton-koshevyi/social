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

import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.test.TestEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class ChatControllerTest {

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
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .get("/chats")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();
    String actual = new JSONObject(response)
        .getJSONArray("content")
        .toString();

    String expected = "[{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'name',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void get() throws JSONException {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .get("/chats/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'name',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void getMembers() throws JSONException {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .get("/chats/{id}/members", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();
    String actual = new JSONObject(response)
        .getJSONArray("content")
        .toString();

    String expected = "[{"
        + "id: 1,"
        + "email: 'email@mail.com',"
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
  public void deletePrivate() {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .delete("/chats/private/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void createGroup_whenInvalidBody_expectBadRequest() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setEmail("creator@mail.com")
        .setUsername("creator")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("creator", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/chats/group")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .extract()
        .asString();

    String expected = "{"
        + "timestamp: (customized),"
        + "status: 400,"
        + "error: 'Bad Request',"
        + "message: 'Invalid body: 2 error(s)',"
        + "errors: {"
        + "  'name': ['must not be null'],"
        + "  'members': ['must not be null']"
        + "},"
        + "path: '/chats/group'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void createGroup() throws JSONException {
    entityManager.persist(TestEntity
        .user()
        .setEmail("creator@mail.com")
        .setUsername("creator")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("creator", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"name\": \"name\","
            + "\"members\": [ 2 ]"
            + "}")
        .when()
        .post("/chats/group")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'name',"
        + "members: 2,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'creator@mail.com',"
        + "  username: 'creator',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void updateGroup_whenInvalidBody_expectBadRequest() throws JSONException {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"name\": \"\" }")
        .when()
        .patch("/chats/group/{id}", 1)
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
        + "  'name': ['size must be between 1 and 20']"
        + "},"
        + "path: '/chats/group/1'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void updateGroup() throws JSONException {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(new GroupChat()
        .setName("name")
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"name\": \"new name\"}")
        .when()
        .patch("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'new name',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void leaveGroup() {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    User member = entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("member", "password", new FormAuthConfig("/auth", "username", "password"))
        .when()
        .put("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void deleteGroup() {
    User member = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .when()
        .delete("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void updateGroupMembers_whenInvalidBody_expectBadRequest() throws JSONException {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("owner", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .put("/chats/group/{id}/members", 1)
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
        + "  'members': ['must not be null']"
        + "},"
        + "path: '/chats/group/1/members'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void updateGroupMembers() throws JSONException {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("owner", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"members\": [ 1, 2 ] }")
        .when()
        .put("/chats/group/{id}/members", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'name',"
        + "members: 2,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'owner@mail.com',"
        + "  username: 'owner',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void changeOwner() throws JSONException {
    User owner = entityManager.persist(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    User newOwner = entityManager.persist(TestEntity
        .user()
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, newOwner)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("owner", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .when()
        .put("/chats/group/{id}/members/2", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'name',"
        + "members: 2,"
        + "owner: {"
        + "  id: 2,"
        + "  username: 'newOwner',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

}
