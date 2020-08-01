package com.social.backend.adapter.json;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.AbstractJsonMarshalTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.TestEntity;
import com.social.backend.model.post.Post;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class PostSerializerTest {
  
  @Autowired
  private AbstractJsonMarshalTester<Post> tester;
  
  @Test
  public void given_anyPost_when_anyRequest_then_regularBody() throws IOException {
    Post post = new Post();
    post.setId(1L);
    post.setCreated(ZonedDateTime
        .now());
    post.setUpdated(ZonedDateTime
        .now());
    post.setBody("body");
    post.setComments(Collections
        .emptyList());
    post.setAuthor(TestEntity
        .user()
        .setId(1L));
    
    String expected = "{"
        + "id: 1,"
        + "creationDate: (customized)',"
        + "updateDate: (customized)',"
        + "updated: true,"
        + "body: 'body',"
        + "comments: 0,"
        + "author: {"
        + "  id: 1,"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}"
        + "}";
    Assertions
        .assertThat(tester.write(post))
        .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("creationDate", (act, exp) -> true),
            new Customization("updateDate", (act, exp) -> true)
        ));
  }
  
}
