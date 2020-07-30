package com.social.backend.controller;

import javax.servlet.http.HttpServletResponse;

import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;
import com.social.backend.repository.CommentRepository;
import com.social.backend.repository.PostRepository;
import com.social.backend.repository.UserRepository;

import static org.skyscreamer.jsonassert.Customization.customization;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import static com.social.backend.TestEntity.comment;
import static com.social.backend.TestEntity.user;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentControllerTest {
    @LocalServerPort
    private int port;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @BeforeAll
    public static void beforeAll() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
    
    @Test
    @Disabled("Pagination metadata is absent")
    public void getAll() {
    }
    
    @Test
    public void create_badRequest_whenInvalidBody() throws JSONException {
        User author = userRepository.save(user()
                .setUsername("username")
                .setPassword(passwordEncoder.encode("password")));
        postRepository.save(TestEntity.post()
                .setAuthor(author));
        
        String actual = RestAssured
                .given()
                .auth()
                .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{}")
                .when()
                .post("/post/{postId}/comments", 1)
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
                + "path: '/post/1/comments'"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("timestamp", (o1, o2) -> true)
        ));
    }
    
    @Test
    public void create() throws JSONException {
        User author = userRepository.save(user()
                .setUsername("username")
                .setPassword(passwordEncoder.encode("password")));
        postRepository.save(TestEntity.post()
                .setAuthor(author));
        
        String actual = RestAssured
                .given()
                .auth()
                .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"body\": \"comment body\"}")
                .when()
                .post("/post/{postId}/comments", 1)
                .then()
                .statusCode(HttpServletResponse.SC_OK)
                .extract()
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "creationDate: (customized),"
                + "updated: false,"
                + "body: 'comment body',"
                + "author: {"
                + "  id: 1,"
                + "  email: 'email@mail.com',"
                + "  username: 'username',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "},"
                + "post: {"
                + "  id: 1,"
                + "  creationDate: (customized),"
                + "  updated: false,"
                + "  body: 'post body',"
                + "  comments: 1,"
                + "  author: {"
                + "    id: 1,"
                + "    email: 'email@mail.com',"
                + "    username: 'username',"
                + "    firstName: 'first',"
                + "    lastName: 'last',"
                + "    publicity: 10,"
                + "    moder: false,"
                + "    admin: false"
                + "  }"
                + "}"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("**.creationDate", (o1, o2) -> true)
        ));
    }
    
    @Test
    public void update_badRequest_whenInvalidBody() throws JSONException {
        User author = userRepository.save(user()
                .setUsername("username")
                .setPassword(passwordEncoder.encode("password")));
        Post post = postRepository.save(TestEntity.post()
                .setAuthor(author));
        commentRepository.save((Comment) new Comment()
                .setPost(post)
                .setBody("body")
                .setAuthor(author));
        
        String actual = RestAssured
                .given()
                .auth()
                .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{}")
                .when()
                .patch("/post/{postId}/comments/{id}", 1, 1)
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
                + "path: '/post/1/comments/1'"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("timestamp", (o1, o2) -> true)
        ));
    }
    
    @Test
    public void update() throws JSONException {
        User author = userRepository.save(user()
                .setUsername("username")
                .setPassword(passwordEncoder.encode("password")));
        Post post = postRepository.save(TestEntity.post()
                .setAuthor(author));
        commentRepository.save((Comment) new Comment()
                .setPost(post)
                .setBody("body")
                .setAuthor(author));
        
        String actual = RestAssured
                .given()
                .auth()
                .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{ \"body\": \"new body\"}")
                .when()
                .patch("/post/{postId}/comments/{id}", 1, 1)
                .then()
                .statusCode(HttpServletResponse.SC_OK)
                .extract()
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "creationDate: (customized),"
                + "updateDate: (customized),"
                + "updated: true,"
                + "body: 'new body',"
                + "author: {"
                + "  id: 1,"
                + "  email: 'email@mail.com',"
                + "  username: 'username',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "},"
                + "post: {"
                + "  id: 1,"
                + "  creationDate: (customized),"
                + "  updated: false,"
                + "  body: 'post body',"
                + "  comments: 1,"
                + "  author: {"
                + "    id: 1,"
                + "    email: 'email@mail.com',"
                + "    username: 'username',"
                + "    firstName: 'first',"
                + "    lastName: 'last',"
                + "    publicity: 10,"
                + "    moder: false,"
                + "    admin: false"
                + "  }"
                + "}"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("**.creationDate", (o1, o2) -> true),
                customization("updateDate", (o1, o2) -> true)
        ));
    }
    
    @Test
    public void delete() {
        User author = userRepository.save(user()
                .setUsername("username")
                .setPassword(passwordEncoder.encode("password")));
        Post post = postRepository.save(TestEntity.post()
                .setAuthor(author));
        commentRepository.save((Comment) comment()
                .setPost(post)
                .setAuthor(author));
        
        RestAssured
                .given()
                .auth()
                .form("username", "password", new FormAuthConfig("/auth", "username", "password"))
                .when()
                .delete("/post/{postId}/comments/{id}", 1, 1)
                .then()
                .statusCode(HttpServletResponse.SC_OK);
    }
}
