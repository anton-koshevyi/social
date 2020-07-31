package com.social.backend.adapter.json;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.social.backend.model.post.Comment;

import static org.assertj.core.api.Assertions.assertThat;

import static com.social.backend.TestEntity.post;
import static com.social.backend.TestEntity.user;

@JsonTest
@ActiveProfiles("test")
@ComponentScan("com.social.backend.dto")
public class CommentSerializerTest {
    @Autowired
    private JacksonTester<Comment> tester;
    
    @Test
    public void given_anyComment_when_anyRequest_then_regularBody() throws IOException {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(ZonedDateTime.now());
        comment.setUpdated(ZonedDateTime.now());
        comment.setBody("body");
        comment.setAuthor(user()
                .setId(1L));
        comment.setPost(post()
                .setId(1L)
                .setComments(Collections.singletonList(comment)));
        
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
                + "  comments: 1"
                + "}"
                + "}";
        assertThat(tester.write(comment))
                .isEqualToJson(expected, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                        new Customization("**.creationDate", (act, exp) -> true),
                        new Customization("**.updateDate", (act, exp) -> true)
                ));
    }
}
