package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Sets;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepository;
import com.social.backend.repository.PostRepository;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.ChatServiceImpl;
import com.social.backend.service.PostServiceImpl;
import com.social.backend.service.UserServiceImpl;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.ChatRepositoryStub;
import com.social.backend.test.stub.repository.PostRepositoryStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class UserControllerTest {

  private IdentificationContext<User> userIdentification;
  private IdentificationContext<Post> postIdentification;
  private IdentificationContext<Chat> chatIdentification;
  private UserRepository userRepository;
  private PostRepository postRepository;
  private ChatRepository chatRepository;

  @BeforeEach
  public void setUp() {
    userIdentification = new IdentificationContext<>();
    postIdentification = new IdentificationContext<>();
    chatIdentification = new IdentificationContext<>();
    userRepository = new UserRepositoryStub(userIdentification);
    postRepository = new PostRepositoryStub(postIdentification);
    chatRepository = new ChatRepositoryStub(chatIdentification);

    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PasswordEncoderStub.class);
    appContext.registerBean(UserRepository.class, () -> userRepository);
    appContext.registerBean(PostRepository.class, () -> postRepository);
    appContext.registerBean(ChatRepository.class, () -> chatRepository);
    appContext.registerBean(UserServiceImpl.class);
    appContext.registerBean(PostServiceImpl.class);
    appContext.registerBean(ChatServiceImpl.class);
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
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
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
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setAdmin(true));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L,
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
        .patch("/users/{id}/roles", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 2,"
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User user = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setFriends(Sets.newHashSet(user)));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setPublicity(Publicity.PUBLIC));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .post("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void removeFriend() {
    userIdentification.setStrategy(e -> e.setId(1L));
    User user = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    userIdentification.setStrategy(e -> e.setId(2L));
    User target = userRepository.save(ModelFactory
        .createModel(UserType.FRED_BLOGGS)
        .setFriends(Sets.newHashSet(user)));
    user.setFriends(Sets.newHashSet(target));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/users/{id}/friends", 2)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }

  @Test
  public void getPosts() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));

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
