package com.social.backend.controller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.social.backend.dto.ResponseMapper;
import com.social.backend.dto.post.PostDto;
import com.social.backend.dto.user.UserDto;
import com.social.backend.model.post.Post;
import com.social.backend.model.user.User;

@ControllerAdvice
public class PostResponseAdvice extends SafeResponseBodyAdvice<Post, PostDto> {
    private final SafeResponseBodyAdvice<User, UserDto> userResponseAdvice;
    
    @Autowired
    public PostResponseAdvice(ResponseMapper<Post, PostDto> responseMapper,
                              SafeResponseBodyAdvice<User, UserDto> userResponseAdvice) {
        super(responseMapper);
        this.userResponseAdvice = userResponseAdvice;
    }
    
    @Override
    public PostDto beforeBodyWriteSafely(Post body,
                                         MethodParameter returnType,
                                         MediaType selectedContentType,
                                         Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                         ServerHttpRequest request,
                                         ServerHttpResponse response) {
        UserDto author = userResponseAdvice.beforeBodyWriteSafely(
                body.getAuthor(), returnType, selectedContentType, selectedConverterType, request, response);
        return responseMapper.map(body)
                .setAuthor(author);
    }
}
