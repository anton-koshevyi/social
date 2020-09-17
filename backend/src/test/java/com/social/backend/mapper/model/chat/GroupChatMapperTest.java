package com.social.backend.mapper.model.chat;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.social.backend.dto.chat.GroupChatDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.mapper.model.AbstractMapper;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.user.User;

public class GroupChatMapperTest {

  private AbstractMapper<GroupChat> mapper = new GroupChatMapper(
      Mockito.mock(AbstractMapper.class, inv -> new UserDto())
  );

  @Test
  public void map() {
    GroupChat chat = new GroupChat();
    chat.setId(1L);
    chat.setName("name");
    chat.setMembers(Collections.singleton(new User()));
    chat.setOwner(new User());

    Assertions
        .assertThat(mapper.map(chat, GroupChatDto.class))
        .usingRecursiveComparison()
        .isEqualTo(new GroupChatDto()
            .setName("name")
            .setOwner(new UserDto())
            .setMembers(1)
            .setId(1L)
            .setType("group"));
  }

}
