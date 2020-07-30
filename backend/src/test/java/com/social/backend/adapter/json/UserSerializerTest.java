package com.social.backend.adapter.json;

import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.backend.config.IdentifiedUserDetails;
import com.social.backend.config.SecurityConfig.Authority;
import com.social.backend.model.user.Publicity;
import com.social.backend.model.user.User;

import static org.skyscreamer.jsonassert.Customization.customization;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import static com.social.backend.TestEntity.user;

// TODO: Use rest-assured mock auth instead of security-method annotations

@ExtendWith(SpringExtension.class)
public class UserSerializerTest {
    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders
                .standaloneSetup(new TestController())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()
                        .registerModule(new SimpleModule()
                                .addSerializer(User.class, new UserSerializer()))
                        .setSerializationInclusion(Include.NON_NULL)))
                .alwaysDo(MockMvcResultHandlers.log())
                .build());
    }
    
    @Test
    public void given_notPublicPublicity_when_nullAuthentication_then_hiddenBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get("/internal")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_publicPublicity_when_anyRequest_then_regularBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get("/public")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 30,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_privatePublicity_when_notIdentifiedPrincipal_then_hiddenBodyByDefault()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .given()
                .auth()
                .principal(new org.springframework.security.core.userdetails.User(
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_privatePublicity_when_notEqualPrincipalIdAndBodyId_then_hiddenBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .given()
                .auth()
                .principal(new IdentifiedUserDetails(
                        99999L,
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    public void given_anyPublicity_when_equalPrincipalIdAndBodyId_then_regularBodyForOwner()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .given()
                .auth()
                .principal(new IdentifiedUserDetails(
                        1L,
                        "username",
                        "password",
                        Collections.emptySet()
                ))
                .get("/private")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    @WithAnonymousUser
    public void given_internalPublicity_when_anonymousRequest_then_hiddenBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get("/internal")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @Test
    @WithMockUser
    public void given_internalPublicity_when_authenticatedRequest_then_regularBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get("/internal")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 20,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/public", "/internal", "/private"})
    @WithMockUser(authorities = Authority.MODER)
    public void given_anyPublicity_when_administrationRequest_then_extendedBody(String endpoint)
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get(endpoint)
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "email: 'email@mail.com',"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: (customized),"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
                customization("publicity", (act, exp) -> true)
        ));
    }
    
    @Test
    @WithMockUser
    public void given_privatePublicity_when_notOwnerNorAdministrationRequest_hiddenBody()
            throws JSONException {
        String actual = RestAssuredMockMvc
                .get("/private")
                .asString();
        
        String expected = "{"
                + "id: 1,"
                + "username: 'username',"
                + "firstName: 'first',"
                + "lastName: 'last',"
                + "publicity: 10,"
                + "moder: false,"
                + "admin: false"
                + "}";
        assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }
    
    @RestController
    private static class TestController {
        @GetMapping("/public")
        private User getPublicUser() {
            return user()
                    .setId(1L)
                    .setPublicity(Publicity.PUBLIC);
        }
        
        @GetMapping("/internal")
        private User getInternalUser() {
            return user()
                    .setId(1L)
                    .setPublicity(Publicity.INTERNAL);
        }
        
        @GetMapping("/private")
        private User getPrivateUser() {
            return user()
                    .setId(1L)
                    .setPublicity(Publicity.PRIVATE);
        }
    }
}
