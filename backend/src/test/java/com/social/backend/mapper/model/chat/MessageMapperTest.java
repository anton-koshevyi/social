package com.social.backend.mapper.model.chat;

import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.social.backend.TestComparator;
import com.social.backend.dto.chat.ChatDto;
import com.social.backend.dto.reply.MessageDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.Message;
import com.social.backend.model.user.User;

public class MessageMapperTest {

  private AbstractMapper<Message> mapper = new MessageMapper(
      Mockito.mock(AbstractMapper.class, inv -> new UserDto()),
      Mockito.mock(AbstractMapper.class, inv -> new ChatDto() {})
  );

  @Test
  public void map() {
    Message message = new Message();
    message.setId(1L);
    message.setCreatedAt(ZonedDateTime.now());
    message.setUpdatedAt(ZonedDateTime.now());
    message.setBody("body");
    message.setAuthor(new User());
    message.setChat(new Chat() {});

    Assertions
        .assertThat(mapper.map(message, MessageDto.class))
        .usingRecursiveComparison()
        .withComparatorForType(TestComparator.notNullFirst(), ZonedDateTime.class)
        .isEqualTo(new MessageDto()
            .setChat(new ChatDto() {})
            .setId(1L)
            .setBody("body")
            .setAuthor(new UserDto()));
  }

}
