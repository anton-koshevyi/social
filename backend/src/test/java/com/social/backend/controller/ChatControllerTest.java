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
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepositoryBase;
import com.social.backend.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatControllerTest {
  
  @LocalServerPort
  private int port;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private ChatRepositoryBase<Chat> chatRepository;
  
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .privateChat()
        .setMembers(Sets
            .newHashSet(member)));
    
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
  public void createGroup_badRequest_whenInvalidBody() throws JSONException {
    userRepository.save(TestEntity
        .user()
        .setEmail("creator@mail.com")
        .setUsername("creator")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    
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
    userRepository.save(TestEntity
        .user()
        .setEmail("creator@mail.com")
        .setUsername("creator")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    
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
  public void updateGroup_badRequest_whenInvalidBody() throws JSONException {
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
    String actual = RestAssured
        .given()
        .auth()
        .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{}")
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
        + "  'name': ['must not be null']"
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(new GroupChat()
        .setName("name")
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
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
    User owner = userRepository.save(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    User member = userRepository.save(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("member")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, member)));
    
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
    User member = userRepository.save(TestEntity
        .user()
        .setUsername("username")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(member)
        .setMembers(Sets
            .newHashSet(member)));
    
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
  public void updateGroupMembers_badRequest_whenInvalidBody() throws JSONException {
    User owner = userRepository.save(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));
    
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
    User owner = userRepository.save(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    userRepository.save(TestEntity
        .user()
        .setEmail("member@mail.com")
        .setUsername("members")
        .setPassword(passwordEncoder.encode("password"))
        .setPublicity(Publicity.PUBLIC));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner)));
    
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
    User owner = userRepository.save(TestEntity
        .user()
        .setEmail("owner@mail.com")
        .setUsername("owner")
        .setPassword(passwordEncoder.encode("password")));
    User newOwner = userRepository.save(TestEntity
        .user()
        .setEmail("newOwner@mail.com")
        .setUsername("newOwner")
        .setPassword(passwordEncoder.encode("password")));
    chatRepository.save(TestEntity
        .groupChat()
        .setOwner(owner)
        .setMembers(Sets
            .newHashSet(owner, newOwner)));
    
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
