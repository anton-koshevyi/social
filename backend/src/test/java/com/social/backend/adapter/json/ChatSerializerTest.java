package com.social.backend.adapter.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.social.backend.model.chat.Chat;
import com.social.backend.model.chat.GroupChat;
import com.social.backend.model.chat.PrivateChat;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class ChatSerializerTest {
    private ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new SimpleModule()
                        .addSerializer(Chat.class, new ChatSerializer()))
                .setSerializationInclusion(Include.NON_NULL);
    }
    
    @Test
    public void given_privateChat_when_anyRequest_then_regularBody()
            throws IOException, JSONException {
        PrivateChat object = new PrivateChat();
        object.setId(1L);
        
        String actual = mapper.writeValueAsString(object);
        
        String expected = "{"
                + "id: 1,"
                + "type: 'private',"
                + "members: [ ]"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_groupChat_when_anyRequest_then_regularBody()
            throws IOException, JSONException {
        GroupChat object = new GroupChat();
        object.setId(1L);
        object.setName("chat name");
        
        String actual = mapper.writeValueAsString(object);
        
        String expected = "{"
                + "id: 1,"
                + "type: 'group',"
                + "name: 'chat name',"
                + "members: 0"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
}
