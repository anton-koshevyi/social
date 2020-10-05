package com.social.backend.service;

import java.util.Optional;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.social.backend.exception.NotFoundException;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;
import com.social.backend.repository.MessageRepository;
import com.social.backend.test.comparator.ComparatorFactory;
import com.social.backend.test.comparator.NotNullComparator;
import com.social.backend.test.model.ModelFactory;
import com.social.backend.test.model.chat.PrivateChatType;
import com.social.backend.test.model.message.MessageType;
import com.social.backend.test.model.user.UserType;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  private @Mock MessageRepository repository;
  private MessageService service;

  @BeforeEach
  public void setUp() {
    service = new MessageServiceImpl(repository);
  }

  @Test
  public void create() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH)
        .setId(1L);
    Chat chat = ModelFactory
        .createModel(PrivateChatType.RAW)
        .setId(1L)
        .setMembers(Sets.newHashSet(author));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Message entity = i.getArgument(0);
          entity.setId(1L);
          return entity;
        });

    Assertions
        .assertThat(service.create(chat, author, "How are you?"))
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
    Mockito
        .when(repository.findByIdAndAuthor(1L, author))
        .thenReturn(Optional.of((Message) ModelFactory
            .createModel(MessageType.MEETING)
            .setChat(chat)
            .setId(1L)
            .setBody("Let's meet")
            .setAuthor(author)));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(1L, author, "How are you?"))
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
    Message entity = (Message) ModelFactory
        .createModel(MessageType.WHATS_UP)
        .setChat(chat)
        .setId(1L)
        .setAuthor(author);
    Mockito
        .when(repository.findByIdAndAuthor(1L, author))
        .thenReturn(Optional.of(entity));

    service.delete(1L, author);

    Mockito
        .verify(repository)
        .delete(entity);
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
    Mockito
        .when(repository.findAllByChat(chat, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList((Message) ModelFactory
                .createModel(MessageType.WHATS_UP)
                .setChat(chat)
                .setId(1L)
                .setAuthor(author))
        ));

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
