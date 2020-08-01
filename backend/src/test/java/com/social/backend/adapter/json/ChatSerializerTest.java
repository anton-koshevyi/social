package com.social.backend.adapter.json;

import java.io.IOException;

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.AbstractJsonMarshalTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.TestEntity;
import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;
import com.social.backend.model.user.User;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class ChatSerializerTest {
  
  @Autowired
  private AbstractJsonMarshalTester<Chat> tester;
  
  @Test
  public void given_privateChat_when_anyRequest_then_regularBody() throws IOException {
    PrivateChat chat = new PrivateChat();
    chat.setId(1L);
    chat.setMembers(Sets
        .newHashSet(
            TestEntity.user()
                .setId(1L)
                .setEmail("user@mail.com")
                .setUsername("user"),
            TestEntity.user()
                .setId(2L)
                .setEmail("target@mail.com")
                .setUsername("target")
        ));
    
    String expected = "{"
        + "id: 1,"
        + "type: 'private',"
        + "members: [{"
        + "  id: 1,"
        + "  username: 'user',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "{"
        + "  id: 2,"
        + "  username: 'target',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}]"
        + "}";
    Assertions
        .assertThat(tester.write(chat))
        .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
  }
  
  @Test
  public void given_groupChat_when_anyRequest_then_regularBody() throws IOException {
    User owner = TestEntity.user()
        .setId(1L)
        .setEmail("owner@mail.com")
        .setUsername("owner");
    GroupChat chat = new GroupChat();
    chat.setId(1L);
    chat.setName("chat name");
    chat.setOwner(owner);
    chat.setMembers(Sets.newHashSet(
        owner,
        TestEntity.user()
            .setId(2L)
            .setEmail("member@mail.com")
            .setUsername("member")
    ));
    
    String expected = "{"
        + "id: 1,"
        + "type: 'group',"
        + "name: 'chat name',"
        + "members: 2,"
        + "owner: {"
        + "  id: 1,"
        + "  username: 'owner',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    Assertions
        .assertThat(tester.write(chat))
        .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
  }
  
}
