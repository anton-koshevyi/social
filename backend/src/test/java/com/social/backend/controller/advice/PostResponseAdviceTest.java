package com.social.backend.controller.advice;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import com.social.backend.dto.ResponseMapper;
import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

import static org.mockito.ArgumentMatchers.any;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(SpringExtension.class)
public class PostResponseAdviceTest {
    @Mock
    private ResponseMapper<Post, PostDto> responseMapper;
    
    @Mock
    private SafeResponseBodyAdvice<User, UserDto> userResponseAdvice;
    
    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new PostResponseAdvice(responseMapper, userResponseAdvice))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()
                        .setSerializationInclusion(Include.NON_NULL)))
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
    }
    
    @Test
    public void when_anyRequest_then_regularBody() throws JSONException {
        Mockito.when(responseMapper.map(
                any(Post.class)
        )).thenReturn(new PostDto()
                .setBody("mocked body"));
        Mockito.when(userResponseAdvice.beforeBodyWriteSafely(
                any(User.class), any(), any(), any(), any(), any()
        )).thenReturn(new UserDto()
                .setUsername("mocked username"));
        
        String actual = RestAssuredMockMvc
                .get("/post")
                .asString();
        
        String expected = ("{"
                + "body: 'mocked body',"
                + "author: {"
                + "  username: 'mocked username'"
                + "}"
                + "}");
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Controller
    private static class TestController {
        @GetMapping("/post")
        private ResponseEntity<Post> getPost() {
            return ResponseEntity.ok(new Post()
                    .setId(1L)
                    .setBody("body")
                    .setAuthor(new User()
                            .setUsername("username")));
        }
    }
}
