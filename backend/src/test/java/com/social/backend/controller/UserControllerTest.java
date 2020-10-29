package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.mutator.ChatMutators;
import com.social.backend.test.model.mutator.PostMutators;
import com.social.backend.test.model.mutator.UserMutators;
import com.social.backend.test.model.type.PostType;
import com.social.backend.test.model.type.PrivateChatType;
import com.social.backend.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

  private @Mock UserService userService;
  private @Mock PostService postService;
  private @Mock ChatService chatService;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(UserService.class, () -> userService);
    appContext.registerBean(PostService.class, () -> postService);
    appContext.registerBean(ChatService.class, () -> chatService);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(TestConfig.class);
    webContext.register(UserController.class);
    webContext.refresh();

    RestAssuredMockMvc.mockMvc(MockMvcBuilders
        .webAppContextSetup(webContext)
        .alwaysDo(MockMvcResultHandlers.log())
        .build());
  }

  @Test
  public void getAll() throws JSONException {
    Mockito
        .when(userService.findAll(PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(UserType.JOHN_SMITH))
        ));

    String response = RestAssuredMockMvc
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
        + "email: null,"
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
  public void get() throws JSONException {
    Mockito
        .when(userService.find(1L))
        .thenReturn(ModelFactory
            .createModel(UserType.JOHN_SMITH));

    String actual = RestAssuredMockMvc
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
        + "email: null,"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 10,"
        + "moder: false,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void updateRole() throws JSONException {
    Mockito
        .when(userService.updateRole(1L, true))
        .thenReturn(ModelFactory
            .createModelMutating(UserType.JOHN_SMITH,
                UserMutators.moder(true)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        2L,
        "fredbloggs",
        "password",
        SecurityManager.createAuthorities(Authority.ADMIN)
    ));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"moder\": true }")
        .when()
        .patch("/users/{id}/roles", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "email: 'johnsmith@example.com',"
        + "username: 'johnsmith',"
        + "firstName: 'John',"
        + "lastName: 'Smith',"
        + "publicity: 10,"
        + "moder: true,"
        + "admin: false"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void getFriends() throws JSONException {
    Mockito
        .when(userService.getFriends(2L, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(UserType.JOHN_SMITH))
        ));

    String response = RestAssuredMockMvc
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
        + "email: null,"
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
  public void addFriend() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .post("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(userService)
        .addFriend(1L, 2L);
  }

  @Test
  public void removeFriend() {
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(userService)
        .removeFriend(1L, 2L);
  }

  @Test
  public void getPosts() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(postService.findAll(author, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(PostType.READING,
                    PostMutators.author(author)))
        ));

    String response = RestAssuredMockMvc
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
        + "updatedAt: null,"
        + "title: 'Favorite books',"
        + "body: 'My personal must-read fiction',"
        + "comments: 0,"
        + "author: {"
        + "  id: 1,"
        + "  email: null,"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}]";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("[*].createdAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void createPrivateChat() throws JSONException {
    User user = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    User target = ModelFactory
        .createModelMutating(UserType.FRED_BLOGGS,
            UserMutators.publicity(Publicity.PUBLIC));
    Mockito
        .when(userService.find(1L))
        .thenReturn(user);
    Mockito
        .when(userService.find(2L))
        .thenReturn(target);
    Mockito
        .when(chatService.createPrivate(user, target))
        .thenReturn(ModelFactory
            .createModelMutating(PrivateChatType.DEFAULT,
                ChatMutators.members(user, target)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
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
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "{"
        + "  id: 2,"
        + "  email: 'fredbloggs@example.com',"
        + "  username: 'fredbloggs',"
        + "  firstName: 'Fred',"
        + "  lastName: 'Bloggs',"
        + "  publicity: 30,"
        + "  moder: false,"
        + "  admin: false"
        + "}]"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
