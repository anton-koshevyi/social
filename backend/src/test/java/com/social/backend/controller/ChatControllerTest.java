package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Sets;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepository;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.ChatServiceImpl;
import com.social.backend.service.UserServiceImpl;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.GroupChatType;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.ChatRepositoryStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class ChatControllerTest {

  private IdentificationContext<User> userIdentification;
  private IdentificationContext<Chat> chatIdentification;
  private UserRepository userRepository;
  private ChatRepository chatRepository;

  @BeforeEach
  public void setUp() {
    userIdentification = new IdentificationContext<>();
    chatIdentification = new IdentificationContext<>();
    userRepository = new UserRepositoryStub(userIdentification);
    chatRepository = new ChatRepositoryStub(chatIdentification);

    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PasswordEncoderStub.class);
    appContext.registerBean(UserRepository.class, () -> userRepository);
    appContext.registerBean(ChatRepository.class, () -> chatRepository);
    appContext.registerBean(UserServiceImpl.class);
    appContext.registerBean(ChatServiceImpl.class);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(TestConfig.class);
    webContext.register(ChatController.class);
    webContext.refresh();

    RestAssuredMockMvc.mockMvc(MockMvcBuilders
        .webAppContextSetup(webContext)
        .alwaysDo(MockMvcResultHandlers.log())
        .build());
  }

  @Test
  public void getAll() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String response = RestAssuredMockMvc
        .given()
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String response = RestAssuredMockMvc
        .given()
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/private/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void createGroup_whenInvalidBody_expectException() {
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    chatIdentification.setStrategy(e -> e.setId(1L));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/chats/group")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void createGroup() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    chatIdentification.setStrategy(e -> e.setId(1L));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
  public void updateGroup_whenInvalidBody_expectException() {
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.SCIENTISTS)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{ \"name\": \"\" }")
        .when()
        .patch("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void updateGroup() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.SCIENTISTS)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User fredBloggs = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    User johnSmith = userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(fredBloggs)
        .setMembers(Sets.newHashSet(fredBloggs, johnSmith)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L, "fredbloggs", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .put("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void deleteGroup() {
    userIdentification.setStrategy(e -> e.setId(1L));
    User member = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(member)
        .setMembers(Sets.newHashSet(member)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void updateGroupMembers_whenInvalidBody_expectException() {
    userIdentification.setStrategy(e -> e.setId(1L));
    User owner = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .put("/chats/group/{id}/members", 1)
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void updateGroupMembers() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User owner = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User owner = userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS));
    userIdentification.setStrategy(e -> e.setId(2L));
    User newOwner = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(GroupChatType.CLASSMATES)
        .setOwner(owner)
        .setMembers(Sets.newHashSet(owner, newOwner)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "fredbloggs", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
        + "  email: null,"
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


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
