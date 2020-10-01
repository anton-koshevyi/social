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

import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.GroupChatType;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.user.UserType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class ChatControllerTest {

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
  public void getAll() throws JSONException {
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
        + "name: 'Classmates',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
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
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
        + "name: 'Classmates',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
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
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
        + "email: 'johnsmith@example.com',"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void deletePrivate() {
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .when()
        .delete("/chats/private/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void createGroup_whenInvalidBody_expectBadRequest() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void createGroup() throws JSONException {
    entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"name\": \"Classmates\","
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
        + "name: 'Classmates',"
        + "members: 2,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
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
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void updateGroup() throws JSONException {
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.SCIENTISTS)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"name\": \"Classmates\" }")
        .when()
        .patch("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'Classmates',"
        + "members: 1,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
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
    User fredBloggs = entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS));
    User johnSmith = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(fredBloggs)
        .setMembers(Sets.newHashSet(fredBloggs, johnSmith)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .when()
        .put("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void deleteGroup() {
    User member = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .when()
        .delete("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void updateGroupMembers_whenInvalidBody_expectBadRequest() throws JSONException {
    User owner = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void updateGroupMembers() throws JSONException {
    User owner = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
        + "name: 'Classmates',"
        + "members: 2,"
        + "owner: {"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
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
    User fredBloggs = entityManager.persist(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPassword(passwordEncoder.encode("password")));
    User johnSmith = entityManager.persist(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    entityManager.persist(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(fredBloggs)
        .setMembers(Sets.newHashSet(fredBloggs, johnSmith)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("fredbloggs", "password", AUTH_FORM)
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
        + "name: 'Classmates',"
        + "members: 2,"
        + "owner: {"
        + "  id: 2,"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

}
