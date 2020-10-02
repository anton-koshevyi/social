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
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.repository.ChatRepository;
import com.social.backend.repository.MessageRepository;
import com.social.backend.repository.UserRepository;
import com.social.backend.service.ChatServiceImpl;
import com.social.backend.service.MessageServiceImpl;
import com.social.backend.service.UserServiceImpl;
import com.social.backend.test.LazyInitBeanFactoryPostProcessor;
import com.social.backend.test.SecurityManager;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.message.MessageType;
import com.social.backend.test.model.user.UserType;
import com.social.backend.test.stub.PasswordEncoderStub;
import com.social.backend.test.stub.repository.ChatRepositoryStub;
import com.social.backend.test.stub.repository.MessageRepositoryStub;
import com.social.backend.test.stub.repository.UserRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class MessageControllerTest {

  private IdentificationContext<User> userIdentification;
  private IdentificationContext<Chat> chatIdentification;
  private IdentificationContext<Message> messageIdentification;
  private UserRepository userRepository;
  private ChatRepository chatRepository;
  private MessageRepository messageRepository;

  @BeforeEach
  public void setUp() {
    userIdentification = new IdentificationContext<>();
    chatIdentification = new IdentificationContext<>();
    messageIdentification = new IdentificationContext<>();
    userRepository = new UserRepositoryStub(userIdentification);
    chatRepository = new ChatRepositoryStub(chatIdentification);
    messageRepository = new MessageRepositoryStub(messageIdentification);

    GenericApplicationContext appContext = new GenericApplicationContext();
    appContext.registerBean(PasswordEncoderStub.class);
    appContext.registerBean(UserRepository.class, () -> userRepository);
    appContext.registerBean(ChatRepository.class, () -> chatRepository);
    appContext.registerBean(MessageRepository.class, () -> messageRepository);
    appContext.registerBean(UserServiceImpl.class);
    appContext.registerBean(ChatServiceImpl.class);
    appContext.registerBean(MessageServiceImpl.class);
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

  @Test
  public void getAll() throws JSONException {
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    Chat chat = chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
    messageRepository.save((Message) ModelFactory
        .createModel(MessageType.WHATS_UP)
        .setChat(chat)
        .setAuthor(author));
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    Chat chat = chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
    messageRepository.save((Message) ModelFactory
        .createModel(MessageType.MEETING)
        .setChat(chat)
        .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    Chat chat = chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
    messageRepository.save((Message) ModelFactory
        .createModel(MessageType.MEETING)
        .setChat(chat)
        .setAuthor(author));
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
    userIdentification.setStrategy(e -> e.setId(1L));
    User author = userRepository.save(ModelFactory
        .createModel(UserType.JOHN_SMITH));
    chatIdentification.setStrategy(e -> e.setId(1L));
    Chat chat = chatRepository.save(ModelFactory
        .createModel(PrivateChatType.RAW)
        .setMembers(Sets.newHashSet(author)));
    messageIdentification.setStrategy(e -> e.setId(1L));
    messageRepository.save((Message) ModelFactory
        .createModel(MessageType.WHATS_UP)
        .setChat(chat)
        .setAuthor(author));
    SecurityManager.setUser(new IdentifiedUserDetails(
        1L, "johnsmith", "password", Collections.emptySet()));

    RestAssuredMockMvc
        .delete("/chats/{chatId}/messages/{id}", 1, 1)
        .then()
        .statusCode(HttpServletResponse.SC_OK);
  }


  @EnableWebMvc
  @EnableSpringDataWebSupport
  private static class TestConfig {
  }

}
