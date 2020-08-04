package com.social.backend.controller;

import javax.servlet.http.HttpServletResponse;

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
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class CommentControllerTest {

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
    User author = entityManager.persist(TestEntity.user());
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    entityManager.persist((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(author));
    TestTransaction.end();

    String response = RestAssured
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/posts/{postId}/comments", 1)
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
        + "body: 'comment body',"
        + "author: {"
        + "  id: 1,"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'title',"
        + "  body: 'post body',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (act, exp) -> true)
        ));
  }

  @Test
  public void create_badRequest_whenInvalidBody() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .post()
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
        .post("/posts/{postId}/comments", 1)
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
        + "path: '/posts/1/comments'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void create() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"comment body\"}")
        .when()
        .post("/posts/{postId}/comments", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "body: 'comment body',"
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
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'title',"
        + "  body: 'post body',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    email: 'email@mail.com',"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (o1, o2) -> true)
        ));
  }

  @Test
  public void update_badRequest_whenInvalidBody() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    entityManager.persist((Comment) new Comment()
        .setPost(post)
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
        .patch("/posts/{postId}/comments/{id}", 1, 1)
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
        + "path: '/posts/1/comments/1'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (o1, o2) -> true)
        ));
  }

  @Test
  public void update() throws JSONException {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    entityManager.persist((Comment) new Comment()
        .setPost(post)
        .setBody("body")
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"new body\"}")
        .when()
        .patch("/posts/{postId}/comments/{id}", 1, 1)
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
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'title',"
        + "  body: 'post body',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    email: 'email@mail.com',"
        + "    username: 'username',"
        + "    firstName: 'first',"
        + "    lastName: 'last',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (o1, o2) -> true),
            new Customization("updatedAt", (o1, o2) -> true)
        ));
  }

  @Test
  public void delete() {
    User author = entityManager.persist(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(TestEntity
        .post()
        .setAuthor(author));
    entityManager.persist((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(author));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .when()
        .delete("/posts/{postId}/comments/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

}
