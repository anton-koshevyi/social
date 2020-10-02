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
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.message.MessageType;
import com.social.backend.test.model.user.UserType;
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
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    identification.setStrategy(e -> e.setId(1L));
    Chat chat = ModelFactory
        .createModel(PrivateChatType.RAW)
        .setId(1L)
        .setMembers(Sets.newHashSet(author));

    service.create(chat, author, "How are you?");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Message.class))
        .isEqualTo(new Message()
            .setChat(ModelFactory
                .createModel(PrivateChatType.RAW)
                .setId(1L)
                .setMembers(Sets.newHashSet(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH)
                        .setId(1L)
                ))
            )
            .setId(1L)
            .setBody("How are you?")
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.update(0L, author, "How are you?"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.message.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void update() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Chat chat = ModelFactory
        .createModel(PrivateChatType.RAW)
        .setId(1L)
        .setMembers(Sets.newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) ModelFactory
        .createModel(MessageType.MEETING)
        .setChat(chat)
        .setAuthor(author));

    service.update(1L, author, "How are you?");

    Assertions
        .assertThat(repository.find(1L))
        .usingComparator(ComparatorFactory.getComparator(Message.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "updatedAt")
        .isEqualTo(ModelFactory
            .createModel(MessageType.MEETING)
            .setChat(ModelFactory
                .createModel(PrivateChatType.RAW)
                .setId(1L)
                .setMembers(Sets.newHashSet(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH)
                        .setId(1L)
                ))
            )
            .setId(1L)
            .setBody("How are you?")
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);

    Assertions
        .assertThatThrownBy(() -> service.delete(0L, author))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.message.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{0L, 1L});
  }

  @Test
  public void delete() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Chat chat = ModelFactory
        .createModel(PrivateChatType.RAW)
        .setId(1L)
        .setMembers(Sets.newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) ModelFactory
        .createModel(MessageType.WHATS_UP)
        .setChat(chat)
        .setAuthor(author));

    service.delete(1L, author);

    Assertions
        .assertThat(repository.find(1L))
        .isNull();
  }

  @Test
  public void findAll_byChat() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Chat chat = ModelFactory
        .createModel(PrivateChatType.RAW)
        .setId(1L)
        .setMembers(Sets.newHashSet(author));
    identification.setStrategy(e -> e.setId(1L));
    repository.save((Message) ModelFactory
        .createModel(MessageType.WHATS_UP)
        .setChat(chat)
        .setAuthor(author));

    Assertions
        .assertThat(service.findAll(chat, Pageable.unpaged()))
        .usingComparatorForType(ComparatorFactory.getComparator(Message.class), Message.class)
        .containsExactly((Message) ModelFactory
            .createModel(MessageType.WHATS_UP)
            .setChat(ModelFactory
                .createModel(PrivateChatType.RAW)
                .setId(1L)
                .setMembers(Sets.newHashSet(
                    ModelFactory
                        .createModel(UserType.JOHN_SMITH)
                        .setId(1L)
                ))
            )
            .setId(1L)
            .setAuthor(ModelFactory
                .createModel(UserType.JOHN_SMITH)
                .setId(1L)));
  }

}
