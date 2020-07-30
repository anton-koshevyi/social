package com.social.backend.adapter.json;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.social.backend.model.post.Post;

import static org.skyscreamer.jsonassert.Customization.customization;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class PostSerializerTest {
    private ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new SimpleModule()
                        .addSerializer(Post.class, new PostSerializer()))
                .setSerializationInclusion(Include.NON_NULL);
    }
    
    @Test
    public void given_anyPost_when_anyRequest_then_regularBody()
            throws IOException, JSONException {
        Post object = new Post()
                .setId(1L)
                .setCreated(ZonedDateTime.now())
                .setUpdated(ZonedDateTime.now())
                .setBody("body")
                .setComments(Collections.emptyList());
    
        String actual = mapper.writeValueAsString(object);
        
        String expected = "{"
                + "id: 1,"
                + "creationDate: (customized)',"
                + "updateDate: (customized)',"
                + "updated: true,"
                + "body: 'body',"
                + "comments: 0"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("creationDate", (act, exp) -> true),
                customization("updateDate", (act, exp) -> true)
        ));
    }
}
