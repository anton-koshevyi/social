package com.social.backend.controller.advice;

import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.dto.ResponseMapper;
import com.social.backend.dto.user.ResponseDto;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class UserResponseAdviceTest {
    @Mock
    private ResponseMapper<User, ResponseDto> responseMapper;
    
    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new UserResponseAdvice(responseMapper))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()
                        .setSerializationInclusion(Include.NON_NULL)))
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
    }
    
    @Test
    public void given_notPublicPublicity_when_nullAuthentication_then_hiddenBody() {
        Mockito.when(responseMapper.mapHidden(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get("/internal")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    public void given_publicPublicity_when_anyRequest_then_regularBody() {
        Mockito.when(responseMapper.map(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get("/public")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    public void given_privatePublicity_when_principalIsNotIdentified_then_hiddenBodyByDefault() {
        Mockito.when(responseMapper.mapHidden(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .given()
                .auth()
                .principal(new org.springframework.security.core.userdetails.User(
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    public void given_privatePublicity_when_principalAndBodyAreNotEqual_then_hiddenBody() {
        Mockito.when(responseMapper.mapHidden(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .given()
                .auth()
                .principal(new IdentifiedUserDetails(
                        99999L,
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    public void given_privatePublicity_when_principalAndBodyAreEqual_then_regularBodyForOwner() {
        Mockito.when(responseMapper.map(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .given()
                .auth()
                .principal(new IdentifiedUserDetails(
                        10L,
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    @WithAnonymousUser
    public void given_internalPublicity_when_anonymousRequest_then_hiddenBody() {
        Mockito.when(responseMapper.mapHidden(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get("/internal")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    @WithMockUser
    public void given_internalPublicity_when_authenticatedRequest_then_regularBody() {
        Mockito.when(responseMapper.map(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get("/internal")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/public", "/internal", "/private"})
    @WithMockUser(authorities = Authority.MODER)
    public void given_anyPublicity_when_administrationRequest_then_extendedBody(String endpoint) {
        Mockito.when(responseMapper.mapExtended(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get(endpoint)
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Test
    @WithMockUser
    public void given_privatePublicity_when_notOwnerNorAdministrationRequest_hiddenBody() {
        Mockito.when(responseMapper.mapHidden(
                any(User.class)
        )).thenReturn(new ResponseDto()
                .setUsername("mocked"));
        
        RestAssuredMockMvc
                .get("/private")
                .then()
                .body(equalTo("{\"username\":\"mocked\"}"));
    }
    
    @Controller
    private static class TestController {
        @GetMapping("/public")
        private ResponseEntity<User> getPublicUser() {
            return ResponseEntity.ok(new User()
                    .setId(30L)
                    .setPublicity(Publicity.PUBLIC));
        }
        
        @GetMapping("/internal")
        private ResponseEntity<User> getInternalUser() {
            return ResponseEntity.ok(new User()
                    .setId(20L)
                    .setPublicity(Publicity.INTERNAL));
        }
        
        @GetMapping("/private")
        private ResponseEntity<User> getPrivateUser() {
            return ResponseEntity.ok(new User()
                    .setId(10L)
                    .setPublicity(Publicity.PRIVATE));
        }
    }
}
