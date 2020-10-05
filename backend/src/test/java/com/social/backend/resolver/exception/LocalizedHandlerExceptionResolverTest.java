package com.social.backend.resolver.exception;

import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.google.common.collect.ImmutableList;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ExtendWith(MockitoExtension.class)
public class LocalizedHandlerExceptionResolverTest {

  private @Mock MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.mockMvc(MockMvcBuilders
        .standaloneSetup(new TestController())
        .setHandlerExceptionResolvers(new LocalizedHandlerExceptionResolver(messageSource))
        .addDispatcherServletCustomizer(c -> c
            .setThrowExceptionIfNoHandlerFound(true))
        .setValidator(new LocalValidatorFactoryBean())
        .alwaysDo(MockMvcResultHandlers.log())
        .build());
  }

  @Test
  public void handleHttpRequestMethodNotSupported() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.httpRequestMethodNotSupported",
            new Object[]{"POST"},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .post("/httpRequestMethodNotSupported")
        .then()
        .statusCode(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        .header("Allow", "GET")
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(HttpRequestMethodNotSupportedException.class)
                .hasFieldOrPropertyWithValue("getMethod", "POST")
                .hasFieldOrPropertyWithValue("getSupportedMethods",
                    new String[]{"GET"}),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleHttpMediaTypeNotSupported() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.httpMediaTypeNotSupported",
            new Object[]{MediaType.valueOf("text/plain;charset=UTF-8")},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .header("Content-Type", "text/plain;charset=UTF-8")
        .body("{ \"notBlank\": \"\" }")
        .when()
        .post("/httpMediaTypeNotSupported")
        .then()
        .statusCode(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
        .header("Accept", "application/json")
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(HttpMediaTypeNotSupportedException.class)
                .hasFieldOrPropertyWithValue("contentType",
                    MediaType.valueOf("text/plain;charset=UTF-8"))
                .hasFieldOrPropertyWithValue("getSupportedMediaTypes",
                    ImmutableList.of(MediaType.APPLICATION_JSON)),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleHttpMediaTypeNotAcceptable() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.httpMediaTypeNotAcceptable",
            null,
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept", "text/plain")
        .header("Accept-Language", "en")
        .when()
        .get("/httpMediaTypeNotAcceptable")
        .then()
        .statusCode(HttpServletResponse.SC_NOT_ACCEPTABLE)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(HttpMediaTypeNotAcceptableException.class)
                .hasFieldOrPropertyWithValue("getSupportedMediaTypes",
                    ImmutableList.of(MediaType.APPLICATION_JSON)),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleMissingPathVariable() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.serverError",
            null,
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/missingPathVariable")
        .then()
        .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MissingPathVariableException.class)
                .hasFieldOrPropertyWithValue("getVariableName", "var"),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleMissingServletRequestParameter() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.missingServletRequestParameter",
            new Object[]{"param"},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/missingServletRequestParameter")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MissingServletRequestParameterException.class)
                .hasFieldOrPropertyWithValue("getParameterName", "param"),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleServletRequestBindingException() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.servletRequestBindingException",
            null,
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/servletRequestBindingException")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MissingRequestCookieException.class),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleHttpMessageNotReadable() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.httpMessageNotReadable",
            null,
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .post("/httpMessageNotReadable")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(HttpMessageNotReadableException.class),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleTypeMismatch() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.typeMismatch",
            null,
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .queryParam("intParam", "string")
        .when()
        .get("/typeMismatch")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MethodArgumentTypeMismatchException.class),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleMethodArgumentNotValid() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.bindException",
            new Object[]{1},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .header("Content-Type", "application/json")
        .body("{ \"notBlank\": \"\" }")
        .when()
        .post("/methodArgumentNotValid")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MethodArgumentNotValidException.class)
                .hasFieldOrPropertyWithValue("bindingResult.getErrorCount", 1),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleMissingServletRequestPart() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.missingServletRequestPart",
            new Object[]{"part"},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .header("Content-Type", "multipart/*")
        .when()
        .post("/missingServletRequestPart")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(MissingServletRequestPartException.class)
                .hasFieldOrPropertyWithValue("getRequestPartName", "part"),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleBindException() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.bindException",
            new Object[]{1},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .queryParam("notBlank", "")
        .when()
        .post("/bindException")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(BindException.class)
                .hasFieldOrPropertyWithValue("getErrorCount", 1),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void handleNoHandlerFound() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.noHandlerFound",
            new Object[]{"GET", "/noHandlerFound"},
            new Locale("en")
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/noHandlerFound")
        .then()
        .statusCode(HttpServletResponse.SC_NOT_FOUND)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(NoHandlerFoundException.class)
                .hasFieldOrPropertyWithValue("getHttpMethod", "GET")
                .hasFieldOrPropertyWithValue("getRequestURL", "/noHandlerFound"),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }


  @Controller
  private static class TestController {

    @GetMapping("/httpRequestMethodNotSupported")
    private ResponseEntity<Object> httpRequestMethodNotSupported() {
      return ResponseEntity.ok("Get request passed");
    }

    @PostMapping(value = "/httpMediaTypeNotSupported", consumes = "application/json")
    private ResponseEntity<Object> httpMediaTypeNotSupported(@RequestBody Target target) {
      return ResponseEntity.ok("Request body passed: " + target);
    }

    @GetMapping(value = "/httpMediaTypeNotAcceptable", produces = "application/json")
    private ResponseEntity<Object> httpMediaTypeNotAcceptable() {
      return ResponseEntity.ok("Request passed");
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @GetMapping("/missingPathVariable")
    private ResponseEntity<Object> httpMissingPathVariable(@PathVariable String var) {
      return ResponseEntity.ok("Path variable passed: " + var);
    }

    @GetMapping("/missingServletRequestParameter")
    private ResponseEntity<Object> missingServletRequestParameter(@RequestParam String param) {
      return ResponseEntity.ok("Request param passed: " + param);
    }

    @GetMapping("/servletRequestBindingException")
    private ResponseEntity<Object> servletRequestBindingException(@CookieValue String cookie) {
      return ResponseEntity.ok("Cookie value passed: " + cookie);
    }

    @PostMapping("/httpMessageNotReadable")
    private ResponseEntity<Object> httpMessageNotReadable(@RequestBody Target target) {
      return ResponseEntity.ok("Target passed " + target);
    }

    @GetMapping("/typeMismatch")
    private ResponseEntity<Object> typeMismatch(@RequestParam Integer intParam) {
      return ResponseEntity.ok("Param passed:" + intParam);
    }

    @PostMapping("/methodArgumentNotValid")
    private ResponseEntity<Object> methodArgumentNotValid(@Valid @RequestBody Target target) {
      return ResponseEntity.ok("Target valid: " + target);
    }

    @PostMapping("/missingServletRequestPart")
    private ResponseEntity<Object> missingServletRequestPart(@RequestPart String part) {
      return ResponseEntity.ok("Part passed: " + part);
    }

    @PostMapping("/bindException")
    private ResponseEntity<Object> bindException(@Valid @ModelAttribute Target target) {
      return ResponseEntity.ok("Target valid: " + target);
    }


    private static class Target {

      @NotBlank
      private String notBlank;

      private String getNotBlank() {
        return notBlank;
      }

      private void setNotBlank(String notBlank) {
        this.notBlank = notBlank;
      }

    }

  }

}
