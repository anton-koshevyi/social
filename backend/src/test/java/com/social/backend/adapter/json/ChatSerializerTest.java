package com.social.backend.adapter.json;

import java.io.IOException;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;

import static org.assertj.core.api.Assertions.assertThat;

import static com.social.backend.TestEntity.user;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class ChatSerializerTest {
    @Autowired
    private JacksonTester<Chat> tester;
    
    @Test
    public void given_privateChat_when_anyRequest_then_regularBody() throws IOException {
        PrivateChat chat = new PrivateChat();
        chat.setId(1L);
        chat.setMembers(Sets.newHashSet(
                user()
                        .setId(1L)
                        .setEmail("first@mail.com")
                        .setUsername("first_user"),
                user()
                        .setId(2L)
                        .setEmail("second@mail.com")
                        .setUsername("second_user")
        ));
        
        String expected = "{"
                + "id: 1,"
                + "type: 'private',"
                + "members: [{"
                + "  id: 1,"
                + "  username: 'first_user',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "},"
                + "{"
                + "  id: 2,"
                + "  username: 'second_user',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "}]"
                + "}";
        assertThat(tester.write(chat))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_groupChat_when_anyRequest_then_regularBody() throws IOException {
        GroupChat chat = new GroupChat();
        chat.setId(1L);
        chat.setName("chat name");
        chat.setOwner(user()
                .setId(1L)
                .setEmail("owner@mail.com")
                .setUsername("owner"));
        chat.setMembers(Sets.newHashSet(
                user()
                        .setId(1L)
                        .setEmail("owner@mail.com")
                        .setUsername("owner"),
                user()
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
        assertThat(tester.write(chat))
                .isEqualToJson(expected, JSONCompareMode.NON_EXTENSIBLE);
    }
}
