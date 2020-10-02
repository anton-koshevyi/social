package com.social.backend.controller;

import java.util.Collections;
import javax.servlet.http.HttpServletResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.social.backend.common.IdentifiedUserDetails;
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;
import com.social.backend.repository.PostRepository;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.CommentServiceImpl;
import com.social.backend.service.PostServiceImpl;
import com.social.backend.service.UserServiceImpl;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.comment.CommentType;
import com.social.backend.test.model.post.PostType;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.CommentRepositoryStub;
import com.social.backend.test.stub.repository.PostRepositoryStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class CommentControllerTest {

  private IdentificationContext<User> userIdentification;
  private IdentificationContext<Post> postIdentification;
  private IdentificationContext<Comment> commentIdentification;
  private UserRepository userRepository;
  private PostRepository postRepository;
  private CommentRepository commentRepository;

  @BeforeEach
  public void setUp() {
    userIdentification = new IdentificationContext<>();
    postIdentification = new IdentificationContext<>();
    commentIdentification = new IdentificationContext<>();
    userRepository = new UserRepositoryStub(userIdentification);
    postRepository = new PostRepositoryStub(postIdentification);
    commentRepository = new CommentRepositoryStub(commentIdentification);

    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PasswordEncoderStub.class);
    appContext.registerBean(UserRepository.class, () -> userRepository);
    appContext.registerBean(PostRepository.class, () -> postRepository);
    appContext.registerBean(CommentRepository.class, () -> commentRepository);
    appContext.registerBean(UserServiceImpl.class);
    appContext.registerBean(PostServiceImpl.class);
    appContext.registerBean(CommentServiceImpl.class);
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

  @Test
  public void getAll() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    Post post = postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
    commentRepository.save((Comment) ModelFactory
        .createModel(CommentType.LIKE)
        .setPost(post)
        .setAuthor(author));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    Post post = postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
    commentRepository.save((Comment) ModelFactory
        .createModel(CommentType.BADLY)
        .setPost(post)
        .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    Post post = postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
    commentRepository.save((Comment) ModelFactory
        .createModel(CommentType.BADLY)
        .setPost(post)
        .setAuthor(author));
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    postIdentification.setStrategy(e -> e.setId(1L));
    Post post = postRepository.save(ModelFactory
        .createModel(PostType.READING)
        .setAuthor(author));
    commentIdentification.setStrategy(e -> e.setId(1L));
    commentRepository.save((Comment) ModelFactory
        .createModel(CommentType.LIKE)
        .setPost(post)
        .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/posts/{postId}/comments/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
