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
import com.social.backend.model.chat.Chat;
import com.social.backend.model.user.User;
import com.social.backend.service.ChatService;
import com.social.backend.service.MessageService;
import com.social.backend.service.UserService;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.factory.ModelFactory;
import com.social.backend.test.model.mutator.ChatMutators;
import com.social.backend.test.model.mutator.MessageMutators;
import com.social.backend.test.model.type.MessageType;
import com.social.backend.test.model.type.PrivateChatType;
import com.social.backend.test.model.type.UserType;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

  private @Mock MessageService messageService;
  private @Mock ChatService chatService;
  private @Mock UserService userService;

  @BeforeEach
  public void setUp() {
    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(MessageService.class, () -> messageService);
    appContext.registerBean(ChatService.class, () -> chatService);
    appContext.registerBean(UserService.class, () -> userService);
    appContext.refresh();

    AnnotationConfigWebApplicationContext webContext =
        new AnnotationConfigWebApplicationContext();
    webContext.setParent(appContext);
    webContext.addBeanFactoryPostProcessor(new LazyInitBeanFactoryPostProcessor());
    webContext.setServletContext(new MockServletContext());
    webContext.register(TestConfig.class);
    webContext.register(MessageController.class);
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
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(chatService.find(1L, author))
        .thenReturn(chat);
    Mockito
        .when(messageService.findAll(chat, PageRequest.of(0, 20, Sort.unsorted())))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(MessageType.WHATS_UP,
                    MessageMutators.author(author),
                    MessageMutators.chat(chat)))
        ));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String response = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .get("/chats/{chatId}/messages", 1)
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
        + "body: 'How are you?',"
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
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'johnsmith@example.com',"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
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
        .post("/chats/{chatId}/messages", 1)
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
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(chatService.find(1L, author))
        .thenReturn(chat);
    Mockito
        .when(messageService.create(chat, author, "How are you?"))
        .thenReturn(ModelFactory
            .createModelMutating(MessageType.WHATS_UP,
                MessageMutators.author(author),
                MessageMutators.chat(chat)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"How are you?\" }")
        .when()
        .post("/chats/{chatId}/messages", 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: null,"
        + "body: 'How are you?',"
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
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'johnsmith@example.com',"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
        + "}"
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
        .patch("/chats/{chatId}/messages/{id}", 1, 1)
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
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    Mockito
        .when(messageService.update(1L, author, "How are you?"))
        .thenReturn(ModelFactory
            .createModelMutating(MessageType.WHATS_UP,
                MessageMutators.author(author),
                MessageMutators.chat(chat)));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    String actual = RestAssuredMockMvc
        .given()
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .body("{ \"body\": \"How are you?\" }")
        .when()
        .patch("/chats/{chatId}/messages/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK)
        .extract()
        .asString();

    String expected = "{"
        + "id: 1,"
        + "createdAt: (customized),"
        + "updatedAt: (customized),"
        + "body: 'How are you?',"
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
        + "chat: {"
        + "  id: 1,"
        + "  type: 'private',"
        + "  members: [{"
        + "    id: 1,"
        + "    email: 'johnsmith@example.com',"
        + "    username: 'johnsmith',"
        + "    firstName: 'John',"
        + "    lastName: 'Smith',"
        + "    publicity: 10,"
        + "    moder: false,"
        + "    admin: false"
        + "  }]"
        + "}"
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
        .createModel(UserType.JOHN_SMITH);
    Mockito
        .when(userService.find(1L))
        .thenReturn(author);
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/{chatId}/messages/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);

    Mockito
        .verify(messageService)
        .delete(1L, author);
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
