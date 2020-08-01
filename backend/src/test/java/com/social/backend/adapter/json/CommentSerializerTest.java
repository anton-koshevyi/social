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
import com.social.backend.model.post.Comment;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class CommentSerializerTest {
  @Autowired
  private AbstractJsonMarshalTester<Comment> tester;
  
  @Test
  public void given_anyComment_when_anyRequest_then_regularBody() throws IOException {
    User author = TestEntity
        .user()
        .setId(1L);
    Post post = TestEntity
        .post()
        .setId(1L)
        .setAuthor(author);
    Comment comment = new Comment();
    comment.setId(1L);
    comment.setCreated(ZonedDateTime
        .now());
    comment.setUpdated(ZonedDateTime
        .now());
    comment.setBody("body");
    comment.setAuthor(author);
    comment.setPost(post
        .setComments(Collections
            .singletonList(comment)));
    
    String expected = "{"
        + "id: 1,"
        + "creationDate: (customized)',"
        + "updateDate: (customized)',"
        + "updated: true,"
        + "body: 'body',"
        + "author: {"
        + "  id: 1,"
        + "  username: 'username',"
        + "  firstName: 'first',"
        + "  lastName: 'last',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "},"
        + "post: {"
        + "  id: 1,"
        + "  creationDate: (customized)',"
        + "  updated: false,"
        + "  body: 'post body',"
        + "  comments: 1,"
        + "  author: {"
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
    Assertions
        .assertThat(tester.write(comment))
        .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
            new Customization("**.creationDate", (act, exp) -> true),
            new Customization("**.updateDate", (act, exp) -> true)
        ));
  }
}
