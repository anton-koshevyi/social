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
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.service.CommentService;
import com.social.backend.service.PostService;
import com.social.backend.service.UserService;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.mutator.CommentMutators;
import com.social.backend.test.model.mutator.PostMutators;
import com.social.backend.test.model.type.CommentType;
import com.social.backend.test.model.type.PostType;
import com.social.backend.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

  private @Mock CommentService commentService;
  private @Mock PostService postService;
  private @Mock UserService userService;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(CommentService.class, () -> commentService);
    appContext.registerBean(PostService.class, () -> postService);
    appContext.registerBean(UserService.class, () -> userService);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(TestConfig.class);
    webContext.register(CommentController.class);
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
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(author));
    Mockito
        .when(postService.find(1L))
        .thenReturn(post);
    Mockito
        .when(commentService.findAll(post, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(CommentType.LIKE,
                    CommentMutators.post(post),
                    CommentMutators.author(author)))
        ));

    String response = RestAssuredMockMvc
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
        + "updatedAt: null,"
        + "body: 'Like',"
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
        + "post: {"
        + "  id: 1,"
        + "  createdAt: (customized),"
        + "  updatedAt: null,"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: (customized),"
        + "  author: {"
        + "    id: 1,"
        + "    email: null,"
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
            new Customization("**.createdAt", (act, exp) -> act != null),
            new Customization("[*].post.comments", (act, exp) -> true)
        ));
  }

  @Test
  public void create_whenInvalidBody_expectException() {
    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/posts/{postId}/comments", 1)
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void create() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(author));
    Mockito
        .when(postService.find(1L))
        .thenReturn(post);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(commentService.create(post, author, "Like"))
        .thenReturn(ModelFactory
            .createModelMutating(CommentType.LIKE,
                CommentMutators.post(post),
                CommentMutators.author(author)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"Like\" }")
        .when()
        .post("/posts/{postId}/comments", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: null,"
        + "body: 'Like',"
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
        + "  updatedAt: null,"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: (customized),"
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
            new Customization("post.comments", (act, exp) -> true)
        ));
  }

  @Test
  public void update_whenInvalidBody_expectException() {
    RestAssuredMockMvc
        .given()
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"\" }")
        .when()
        .patch("/posts/{postId}/comments/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(result -> Assertions
            .assertThat(result.getResolvedException())
            .isExactlyInstanceOf(MethodArgumentNotValidException.class));
  }

  @Test
  public void update() throws JSONException {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Post post = ModelFactory
        .createModelMutating(PostType.READING,
            PostMutators.author(author));
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(commentService.update(1L, author, "Like"))
        .thenReturn(ModelFactory
            .createModelMutating(CommentType.LIKE,
                CommentMutators.post(post),
                CommentMutators.author(author)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"Like\" }")
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
        + "body: 'Like',"
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
        + "  updatedAt: null,"
        + "  title: 'Favorite books',"
        + "  body: 'My personal must-read fiction',"
        + "  comments: (customized),"
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
            new Customization("updatedAt", (act, exp) -> act != null),
            new Customization("post.comments", (act, exp) -> true)
        ));
  }

  @Test
  public void delete() {
    Mockito
        .when(userService.find(1L))
        .thenReturn(ModelFactory
            .createModel(UserType.JOHN_SMITH));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/posts/{postId}/comments/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(commentService)
        .delete(Mockito.eq(1L), Mockito.any());
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
