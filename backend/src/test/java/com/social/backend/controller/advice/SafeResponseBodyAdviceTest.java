package com.social.backend.controller.advice;

import java.util.Arrays;
import java.util.List;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

public class SafeResponseBodyAdviceTest {
    @Test
    public void given_anyTypesOrNotSpecified_when_nullBody_then_nullBody() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new TypesAreNotSpecified())
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
        
        RestAssuredMockMvc
                .get("/null")
                .then()
                .body(emptyString());
    }
    
    @Test
    public void given_typesAreNotSpecified_when_anyRequest_then_originalBody() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new TypesAreNotSpecified())
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
        
        RestAssuredMockMvc
                .get("/test")
                .then()
                .body(equalTo("string"));
    }
    
    @Test
    public void when_handledTypeIsNotTarget_then_originalBody() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new HandledTypeIsNotTarget())
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
        
        RestAssuredMockMvc
                .get("/test")
                .then()
                .body(equalTo("string"));
    }
    
    @Test
    public void when_handledTypeIsTarget_then_processBody() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new HandledType())
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
    
        RestAssuredMockMvc
                .get("/test")
                .then()
                .body(equalTo("changed string"));
    }
    
    @Test
    public void when_handleTypeIsIterableWithTarget_then_processBodyOfItem() throws JSONException {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new HandledType())
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
        
        String actual = RestAssuredMockMvc
                .get("/iterable")
                .asString();
        
        String expected = "["
                + "'changed string',"
                + "1"
                + "]";
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Controller
    private static class TestController {
        @ResponseBody
        @GetMapping("/null")
        private void nullBody() {
        }
        
        @GetMapping("/test")
        private ResponseEntity<String> test() {
            return ResponseEntity.ok("string");
        }
        
        @ResponseBody
        @GetMapping("/iterable")
        private List<?> iterable() {
            return Arrays.asList("string", 1);
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ControllerAdvice
    private static class TypesAreNotSpecified extends SafeResponseBodyAdvice {
        private TypesAreNotSpecified() {
            super(null);
        }
        
        @Override
        public Object beforeBodyWriteSafely(Object body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class selectedConverterType,
                                            ServerHttpRequest request,
                                            ServerHttpResponse response) {
            return new Object();
        }
    }
    
    @ControllerAdvice
    private static class HandledTypeIsNotTarget extends SafeResponseBodyAdvice<Integer, String> {
        private HandledTypeIsNotTarget() {
            super(null);
        }
        
        @Override
        public String beforeBodyWriteSafely(Integer body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            ServerHttpRequest request,
                                            ServerHttpResponse response) {
            return "integer";
        }
    }
    
    @ControllerAdvice
    private static class HandledType extends SafeResponseBodyAdvice<String, String> {
        private HandledType() {
            super(null);
        }
        
        @Override
        public String beforeBodyWriteSafely(String body,
                                            MethodParameter returnType,
                                            MediaType selectedContentType,
                                            Class<? extends HttpMessageConverter<?>>
                                                    selectedConverterType,
                                            ServerHttpRequest request,
                                            ServerHttpResponse response) {
            return "changed string";
        }
    }
}
