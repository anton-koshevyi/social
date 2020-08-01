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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.TestEntity;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.PostRepository;
import com.social.backend.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
  
  @LocalServerPort
  private int port;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PostRepository postRepository;
  
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
    userRepository.save(TestEntity.user());
    
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
    userRepository.save(TestEntity.user());
    
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
    userRepository.save(TestEntity
        .user()
        .setEmail("admin@mail.com")
        .setUsername("admin")
        .setPassword(passwordEncoder.encode("password"))
        .setAdmin(true));
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    
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
    userRepository.save(TestEntity
        .user()
        .setEmail("admin@mail.com")
        .setUsername("admin")
        .setPassword(passwordEncoder.encode("password"))
        .setAdmin(true));
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user"));
    
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
    User user = userRepository.save(TestEntity
        .user()
        .setEmail("email_1@mail.com")
        .setUsername("username_1"));
    userRepository.save(TestEntity
        .user()
        .setEmail("email_2@mail.com")
        .setUsername("username_2")
        .setFriends(Sets
            .newHashSet(user)));
    
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
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("username_2")
        .setPublicity(Publicity.PUBLIC));
    
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
    User user = userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    User target = userRepository.save(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setFriends(Sets.newHashSet(user)));
    user.setFriends(Sets.newHashSet(target));
    userRepository.save(user);
    
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
    User author = userRepository.save(TestEntity.user());
    postRepository.save(TestEntity
        .post()
        .setAuthor(author));
    
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
        + "creationDate: (customized),"
        + "updated: false,"
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
            new Customization("[*].creationDate", (act, exp) -> true)
        ));
  }
  
  @Test
  public void createPrivateChat() throws JSONException {
    userRepository.save(TestEntity
        .user()
        .setEmail("user@mail.com")
        .setUsername("user")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("target@mail.com")
        .setUsername("target")
        .setPublicity(Publicity.PUBLIC));
    
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
