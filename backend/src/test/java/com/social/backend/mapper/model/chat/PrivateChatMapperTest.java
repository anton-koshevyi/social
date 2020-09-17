package com.social.backend.mapper.model.chat;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.social.backend.dto.chat.PrivateChatDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

public class PrivateChatMapperTest {

  private AbstractMapper<PrivateChat> mapper = new PrivateChatMapper(
      Mockito.mock(AbstractMapper.class, inv -> new UserDto())
  );

  @Test
  public void map() {
    PrivateChat chat = new PrivateChat();
    chat.setId(1L);
    chat.setMembers(Collections.singleton(new User()));

    Assertions
        .assertThat(mapper.map(chat, PrivateChatDto.class))
        .usingRecursiveComparison()
        .isEqualTo(new PrivateChatDto()
            .setMembers(Collections.singletonList(new UserDto()))
            .setId(1L)
            .setType("private"));
  }

}
