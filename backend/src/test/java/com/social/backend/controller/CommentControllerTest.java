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

import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.test.TestEntity;
import com.social.backend.test.model.ModelFactoryProducer;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Commit
@Transactional
@AutoConfigureTestEntityManager
public class CommentControllerTest {

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
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH));
    Post post = entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
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
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void create_whenInvalidBody_expectBadRequest() throws JSONException {
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void create() throws JSONException {
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
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
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    email: 'johnsmith@example.com',"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void update_whenInvalidBody_expectBadRequest() throws JSONException {
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
        .setAuthor(author));
    entityManager.persist((Comment) new Comment()
        .setPost(post)
        .setBody("body")
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"\" }")
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
        + "  'body': ['size must be between 1 and 250']"
        + "},"
        + "path: '/posts/1/comments/1'"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("timestamp", (act, exp) -> act != null)
        ));
  }

  @Test
  public void update() throws JSONException {
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
        .setAuthor(author));
    entityManager.persist((Comment) new Comment()
        .setPost(post)
        .setBody("body")
        .setAuthor(author));
    TestTransaction.end();

    String actual = RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"new body\" }")
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
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: 1,"
        + "  author: {"
        + "    id: 1,"
        + "    email: 'johnsmith@example.com',"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }"
        + "}"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.createdAt", (act, exp) -> act != null),
            new Customization("updatedAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void delete() {
    User author = entityManager.persist(ModelFactoryProducer.getFactory(User.class)
        .createModel(UserType.JOHN_SMITH)
        .setPassword(passwordEncoder.encode("password")));
    Post post = entityManager.persist(ModelFactoryProducer.getFactory(Post.class)
        .createModel(PostType.READING)
        .setAuthor(author));
    entityManager.persist((Comment) TestEntity
        .comment()
        .setPost(post)
        .setAuthor(author));
    TestTransaction.end();

    RestAssured
        .given()
        .auth()
        .form("johnsmith", "password", AUTH_FORM)
        .when()
        .delete("/posts/{postId}/comments/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

}
