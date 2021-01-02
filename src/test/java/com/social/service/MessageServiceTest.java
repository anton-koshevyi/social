package com.social.service;

import java.util.Optional;

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

import com.social.exception.NotFoundException;
import com.social.model.chat.Chat;
import com.social.model.chat.Message;
import com.social.model.user.User;
import com.social.repository.MessageRepository;
import com.social.test.comparator.ComparatorFactory;
import com.social.test.comparator.NotNullComparator;
import com.social.test.model.factory.ModelFactory;
import com.social.test.model.mutator.ChatMutators;
import com.social.test.model.mutator.MessageMutators;
import com.social.test.model.type.MessageType;
import com.social.test.model.type.PrivateChatType;
import com.social.test.model.type.UserType;

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
        .createModel(UserType.JOHN_SMITH);
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> {
          Message entity = i.getArgument(0);
          MessageMutators.id(1L).accept(entity);
          return entity;
        });

    Assertions
        .assertThat(service.create(chat, author, "How are you?"))
        .usingComparator(ComparatorFactory.getComparator(Message.class))
        .usingComparatorForFields(NotNullComparator.leftNotNull(), "createdAt")
        .isEqualTo(ModelFactory
            .createModelMutating(MessageType.RAW,
                MessageMutators.id(1L),
                MessageMutators.body("How are you?"),
                MessageMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                MessageMutators.chat(ModelFactory
                    .createModelMutating(PrivateChatType.DEFAULT,
                        ChatMutators.members(ModelFactory
                            .createModel(UserType.JOHN_SMITH))
                    ))
            ));
  }

  @Test
  public void update_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

    Assertions
        .assertThatThrownBy(() -> service.update(2L, author, "How are you?"))
        .isExactlyInstanceOf(NotFoundException.class)
        .hasFieldOrPropertyWithValue("getCodes",
            new Object[]{"notFound.message.byIdAndAuthor"})
        .hasFieldOrPropertyWithValue("getArguments", new Object[]{2L, 1L});
  }

  @Test
  public void update() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(repository.findByIdAndAuthor(2L, author))
        .thenReturn(Optional.of(ModelFactory
            .createModelMutating(MessageType.MEETING,
                MessageMutators.author(author),
                MessageMutators.chat(chat))
        ));
    Mockito
        .when(repository.save(Mockito.any()))
        .then(i -> i.getArgument(0));

    Assertions
        .assertThat(service.update(2L, author, "How are you?"))
        .usingComparator(ComparatorFactory.getComparator(Message.class))
        .usingComparatorForFields(
            NotNullComparator.leftNotNull(), "createdAt", "updatedAt")
        .isEqualTo(ModelFactory
            .createModelMutating(MessageType.MEETING,
                MessageMutators.body("How are you?"),
                MessageMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                MessageMutators.chat(ModelFactory
                    .createModelMutating(PrivateChatType.DEFAULT,
                        ChatMutators.members(ModelFactory
                            .createModel(UserType.JOHN_SMITH))
                    ))
            ));
  }

  @Test
  public void delete_whenNoEntityWithIdAndAuthor_expectException() {
    User author = ModelFactory
        .createModel(UserType.JOHN_SMITH);

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
        .createModel(UserType.JOHN_SMITH);
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Message entity = ModelFactory
        .createModelMutating(MessageType.WHATS_UP,
            MessageMutators.author(author),
            MessageMutators.chat(chat));
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
        .createModel(UserType.JOHN_SMITH);
    Chat chat = ModelFactory
        .createModelMutating(PrivateChatType.DEFAULT,
            ChatMutators.members(author));
    Mockito
        .when(repository.findAllByChat(chat, Pageable.unpaged()))
        .thenReturn(new PageImpl<>(
            Lists.newArrayList(ModelFactory
                .createModelMutating(MessageType.WHATS_UP,
                    MessageMutators.author(author),
                    MessageMutators.chat(chat)))
        ));

    Assertions
        .assertThat(service.findAll(chat, Pageable.unpaged()))
        .usingElementComparator(ComparatorFactory.getComparator(Message.class))
        .usingComparatorForElementFieldsWithNames(
            NotNullComparator.leftNotNull(), "createdAt")
        .containsExactly(ModelFactory
            .createModelMutating(MessageType.WHATS_UP,
                MessageMutators.author(ModelFactory
                    .createModel(UserType.JOHN_SMITH)),
                MessageMutators.chat(ModelFactory
                    .createModelMutating(PrivateChatType.DEFAULT,
                        ChatMutators.members(ModelFactory
                            .createModel(UserType.JOHN_SMITH))
                    ))
            ));
  }

}
