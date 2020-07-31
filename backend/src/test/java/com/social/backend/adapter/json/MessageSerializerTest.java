package com.social.backend.adapter.json;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.AbstractJsonMarshalTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.chat.Message;

import static org.assertj.core.api.Assertions.assertThat;

import static com.social.backend.TestEntity.groupChat;
import static com.social.backend.TestEntity.privateChat;
import static com.social.backend.TestEntity.user;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class MessageSerializerTest {
    @Autowired
    private AbstractJsonMarshalTester<Message> tester;
    
    @Test
    public void given_privateChatMessage_when_anyRequest_then_regularBody() throws IOException {
        Message message = new Message();
        message.setId(1L);
        message.setCreated(ZonedDateTime.now());
        message.setUpdated(ZonedDateTime.now());
        message.setBody("message body");
        message.setAuthor(user()
                .setId(1L));
        message.setChat(privateChat()
                .setId(1L)
                .setMembers(Sets.newHashSet(user()
                        .setId(1L))));
        
        String expected = "{"
                + "id: 1,"
                + "creationDate: (customized),"
                + "updateDate: (customized),"
                + "updated: true,"
                + "body: 'message body',"
                + "author: {"
                + "  id: 1,"
                + "  username: 'username',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "},"
                + "chat: {"
                + "  id: 1,"
                + "  type: 'private',"
                + "  members: [{"
                + "    id: 1,"
                + "    username: 'username',"
                + "    firstName: 'first',"
                + "    lastName: 'last',"
                + "    publicity: 10,"
                + "    moder: false,"
                + "    admin: false"
                + "  }]"
                + "}"
                + "}";
        assertThat(tester.write(message))
                .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                        new Customization("creationDate", (act, exp) -> true),
                        new Customization("updateDate", (act, exp) -> true)
                ));
    }
    
    @Test
    public void given_groupChatMessage_when_anyRequest_then_regularBody() throws IOException {
        Message message = new Message();
        message.setId(1L);
        message.setCreated(ZonedDateTime.now());
        message.setUpdated(ZonedDateTime.now());
        message.setBody("message body");
        message.setAuthor(user()
                .setId(1L));
        message.setChat(groupChat()
                .setName("chat name")
                .setOwner(user()
                        .setId(1L))
                .setId(1L)
                .setMembers(Sets.newHashSet(user()
                        .setId(1L))));
        
        String expected = "{"
                + "id: 1,"
                + "creationDate: (customized),"
                + "updateDate: (customized),"
                + "updated: true,"
                + "body: 'message body',"
                + "author: {"
                + "  id: 1,"
                + "  username: 'username',"
                + "  firstName: 'first',"
                + "  lastName: 'last',"
                + "  publicity: 10,"
                + "  moder: false,"
                + "  admin: false"
                + "},"
                + "chat: {"
                + "  id: 1,"
                + "  type: 'group',"
                + "  name: 'chat name',"
                + "  members: 1,"
                + "  owner: {"
                + "    id: 1,"
                + "    username: 'username',"
                + "    firstName: 'first',"
                + "    lastName: 'last',"
                + "    publicity: 10,"
                + "    moder: false,"
                + "    admin: false"
                + "  }"
                + "}"
                + "}";
        assertThat(tester.write(message))
                .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                        new Customization("creationDate", (act, exp) -> true),
                        new Customization("updateDate", (act, exp) -> true)
                ));
    }
}
