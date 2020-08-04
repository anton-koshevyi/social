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
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class MessageControllerTest {

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
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Chat chat = entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    entityManager.persist((Message) TestEntity
        .message()
        .setChat(chat)
        .setAuthor(author));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .get("/chats/{chatId}/messages", 1)
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
        + "body: 'message body',"
        + "author: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'email@mail.com',"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("[*].createdAt", (act, exp) -> true)
        ));
  }

  @Test
  public void create_badRequest_whenInvalidBody() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/chats/{chatId}/messages", 1)
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
        + "  'body': ['must not be null']"
        + "},"
        + "path: '/chats/1/messages'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> true)
        ));
  }

  @Test
  public void create() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"body\" }")
        .when()
        .post("/chats/{chatId}/messages", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "body: 'body',"
        + "author: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'email@mail.com',"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("createdAt", (act, exp) -> true)
        ));
  }

  @Test
  public void update_badRequest_whenInvalidBody() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Chat chat = entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    entityManager.persist((Message) new Message()
        .setChat(chat)
        .setBody("body")
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .patch("/chats/{chatId}/messages/{id}", 1, 1)
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
        + "  'body': ['must not be null']"
        + "},"
        + "path: '/chats/1/messages/1'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> true)
        ));
  }

  @Test
  public void update() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Chat chat = entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    entityManager.persist((Message) new Message()
        .setChat(chat)
        .setBody("body")
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"new body\" }")
        .when()
        .patch("/chats/{chatId}/messages/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: (customized),"
        + "body: 'new body',"
        + "author: {"
        + "  id: 1,"
        + "  email: 'email@mail.com',"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'email@mail.com',"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("createdAt", (act, exp) -> true),
            new Customization("updatedAt", (act, exp) -> true)
        ));
  }

  @Test
  public void delete() {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Chat chat = entityManager.persist(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(author)));
    entityManager.persist((Message) TestEntity
        .message()
        .setChat(chat)
        .setAuthor(author));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .when()
        .delete("/chats/{chatId}/messages/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

}
