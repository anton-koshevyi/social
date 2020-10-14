package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Sets;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.UserService;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.type.GroupChatType;
import com.social.backend.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

  private @Mock ChatService chatService;
  private @Mock UserService userService;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(ChatService.class, () -> chatService);
    appContext.registerBean(UserService.class, () -> userService);
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

  @AfterEach
  public void tearDown() {
    SecurityManager.clearContext();
  }

  @Test
  public void getAll() throws JSONException {
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    Mockito
        .when(chatService.findAll(member, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(GroupChatType.CLASSMATES)
                .setOwner(member)
                .setId(1L)
                .setMembers(Sets.newHashSet(member)))
        ));
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    Mockito
        .when(chatService.find(1L, member))
        .thenReturn(ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(member)
            .setId(1L)
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    Mockito
        .when(chatService.getMembers(1L, member, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(member)
        ));
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/private/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(chatService)
        .deletePrivate(1L, member);
  }

  @Test
  public void createGroup_whenInvalidBody_expectException() {
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
    User creator = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User member = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(2L)
        .setPublicity(Publicity.PUBLIC);
    Mockito
        .when(userService.find(1L))
        .thenReturn(creator);
    Mockito
        .when(userService.find(2L))
        .thenReturn(member);
    Mockito
        .when(chatService.createGroup(creator, "Classmates", Sets.newHashSet(member)))
        .thenReturn((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(creator)
            .setId(1L)
            .setMembers(Sets.newHashSet(creator, member)));
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    Mockito
        .when(chatService.updateGroup(1L, member, "Classmates"))
        .thenReturn((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(member)
            .setId(1L)
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
    User member = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(member);
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .put("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(chatService)
        .leaveGroup(1L, member);
  }

  @Test
  public void deleteGroup() {
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(owner);
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/group/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(chatService)
        .deleteGroup(1L, owner);
  }

  @Test
  public void updateGroupMembers_whenInvalidBody_expectException() {
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
    User owner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    User newMember = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC)
        .setId(2L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(owner);
    Mockito
        .when(userService.find(2L))
        .thenReturn(newMember);
    Mockito
        .when(chatService.updateGroupMembers(1L, owner, Sets.newHashSet(owner, newMember)))
        .thenReturn((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(owner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner, newMember)));
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
    User owner = ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setId(1L);
    User newOwner = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(2L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(owner);
    Mockito
        .when(userService.find(2L))
        .thenReturn(newOwner);
    Mockito
        .when(chatService.changeOwner(1L, owner, newOwner))
        .thenReturn((GroupChat) ModelFactory
            .createModel(GroupChatType.CLASSMATES)
            .setOwner(newOwner)
            .setId(1L)
            .setMembers(Sets.newHashSet(owner, newOwner)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "fredbloggs", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .when()
        .put("/chats/group/{id}/members/{newOwnerId}", 1, 2)
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
