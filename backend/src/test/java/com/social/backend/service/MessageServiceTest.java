package com.social.backend.service;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.test.TestComparator;
import com.social.backend.test.TestEntity;
import com.social.backend.test.stub.repository.MessageRepositoryStub;
import com.social.backend.test.stub.repository.identification.IdentificationContext;

public class MessageServiceTest {

  private IdentificationContext<Message> identification;
  private MessageRepositoryStub repository;
  private MessageService service;

  @BeforeEach
  public void setUp() {
    identification = new IdentificationContext<>();
    repository = new MessageRepositoryStub(identification);
    service = new MessageServiceImpl(repository);
  }

  @Test
  public void create() {
    User author = TestEntity
        .user()
        .setId(1L)
        .setEmail("author@mail.com")
        .setUsername("author");
    identification.setStrategy(e -> e.setId(1L));
    Chat chat = TestEntity
        .privateChat()
        .setId(1L)
        .setMembers(Sets
            .newHashSet(author));

    service.create(chat, author, "body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(TestComparator
            .messageComparator())
        .isEqualTo(new Message()
            .setChat(TestEntity
                .privateChat()
                .setId(1L))
            .setId(1L)
            .setBody("body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)
                .setEmail("author@mail.com")
                .setUsername("author")));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.update(0L, author, "body"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = TestEntity
        .user()
        .setId(1L)
        .setEmail("author@mail.com")
        .setUsername("author");
    Chat chat = TestEntity
        .privateChat()
        .setId(1L)
        .setMembers(Sets
            .newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) new Message()
        .setChat(chat)
        .setBody("message body")
        .setAuthor(author));

    service.update(1L, author, "new body");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(TestComparator
            .messageComparator())
        .usingComparatorForFields(TestComparator
            .notNullFirst(), "updated")
        .isEqualTo(new Message()
            .setChat(TestEntity
                .privateChat()
                .setId(1L))
            .setId(1L)
            .setBody("new body")
            .setAuthor(TestEntity
                .user()
                .setId(1L)
                .setEmail("author@mail.com")
                .setUsername("author")));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = TestEntity
        .user()
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes", new Object[]{"notFound.message.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = TestEntity
        .user()
        .setId(1L)
        .setEmail("author@mail.com")
        .setUsername("author");
    Chat chat = TestEntity
        .privateChat()
        .setId(1L)
        .setMembers(Sets
            .newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) TestEntity
        .message()
        .setChat(chat)
        .setAuthor(author));

    service.delete(1L, author);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void findAll_byChat() {
    User author = TestEntity
        .user()
        .setId(1L)
        .setEmail("author@mail.com")
        .setUsername("author");
    Chat chat = TestEntity
        .privateChat()
        .setId(1L)
        .setMembers(Sets
            .newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) TestEntity
        .message()
        .setChat(chat)
        .setAuthor(author));

    Assertions
        .assertThat(service.findAll(chat, Pageable.unpaged()))
        .usingComparatorForType(TestComparator
            .messageComparator(), Message.class)
        .containsExactly((Message) TestEntity
            .message()
            .setChat(TestEntity
                .privateChat()
                .setId(1L))
            .setId(1L)
            .setAuthor(TestEntity
                .user()
                .setId(1L)
                .setEmail("author@mail.com")
                .setUsername("author")));
  }

}
