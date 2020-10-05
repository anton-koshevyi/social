package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.model.user.User;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

  private @Mock PostService postService;
  private @Mock UserService userService;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PostService.class, () -> postService);
    appContext.registerBean(UserService.class, () -> userService);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(TestConfig.class);
    webContext.register(PostController.class);
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
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(postService.findAll(PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModel(PostType.READING)
                .setId(1L)
                .setAuthor(author))
        ));

    String response = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/posts")
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
  public void create_whenInvalidBody_expectException() {
    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/posts")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void create() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(postService.create(
            author,
            "Favorite books",
            "My personal must-read fiction"
        ))
        .thenReturn(ModelFactory
            .createModel(PostType.READING)
            .setId(1L)
            .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"title\": \"Favorite books\","
            + "\"body\": \"My personal must-read fiction\""
            + "}")
        .post("/posts")
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: null,"
        + "title: 'Favorite books',"
        + "body: 'My personal must-read fiction',"
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
        + "comments: 0"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("createdAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void get() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(postService.find(1L))
        .thenReturn(ModelFactory
            .createModel(PostType.READING)
            .setId(1L)
            .setAuthor(author));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .when()
        .get("/posts/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: null,"
        + "title: 'Favorite books',"
        + "body: 'My personal must-read fiction',"
        + "author: {"
        + "  id: 1,"
        + "  email: null,"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "comments: 0"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("createdAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void update_whenInvalidBody_expectException() {
    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"\" }")
        .when()
        .patch("/posts/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void update() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(postService.update(
            1L,
            author,
            "Favorite books",
            "My personal must-read fiction"
        ))
        .thenReturn(ModelFactory
            .createModel(PostType.READING)
            .setId(1L)
            .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{"
            + "\"title\": \"Favorite books\","
            + "\"body\": \"My personal must-read fiction\""
            + "}")
        .when()
        .patch("/posts/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: (customized),"
        + "title: 'Favorite books',"
        + "body: 'My personal must-read fiction',"
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
        + "comments: 0"
        + "}";
    JSONAssert
        .assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("createdAt", (act, exp) -> act != null),
            new Customization("updatedAt", (act, exp) -> act != null)
        ));
  }

  @Test
  public void delete() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/posts/{id}", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(postService)
        .delete(1L, author);
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
