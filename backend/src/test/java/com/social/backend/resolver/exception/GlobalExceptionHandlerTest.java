package com.social.backend.resolver.exception;

import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import com.social.backend.exception.LocalizedException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

  @Mock
  private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.mockMvc(MockMvcBuilders
        .standaloneSetup(new TestController())
        .setHandlerExceptionResolvers(new GlobalExceptionHandler(messageSource))
        .alwaysDo(MockMvcResultHandlers.log())
        .build());
  }

  @Test
  public void whenLocalizedExceptionType_thenExceptionBasedResponse() {
    Mockito
        .when(messageSource.getMessage(
            ArgumentMatchers.any(LocalizedException.class),
            ArgumentMatchers.eq(new Locale("en"))
        ))
        .thenReturn("Mocked");

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/localized")
        .then()
        .statusCode(HttpServletResponse.SC_BAD_REQUEST)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isInstanceOf(LocalizedException.class)
                .hasFieldOrPropertyWithValue("getStatusCode", 400)
                .hasFieldOrPropertyWithValue("getCodes", new String[]{"error.localized"})
                .hasFieldOrPropertyWithValue("getArguments", null),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void whenNotHandledType_thenInternalErrorResponse() {
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
        .get("/global")
        .then()
        .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        .expect(ResultMatcher.matchAll(
            result -> Assertions
                .assertThat(result.getResolvedException())
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("Global error"),
            result -> Assertions
                .assertThat(result.getResponse())
                .hasFieldOrPropertyWithValue("getErrorMessage", "Mocked")
        ));
  }

  @Test
  public void whenFailureToHandle_thenInternalErrorResponseWithStatusOnly() {
    Mockito
        .when(messageSource.getMessage(
            "resolver.serverError",
            null,
            new Locale("en")
        ))
        .thenThrow(new RuntimeException("Exception during handling"));

    RestAssuredMockMvc
        .given()
        .header("Accept-Language", "en")
        .when()
        .get("/global")
        .then()
        .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        .expect(result -> Assertions
            .assertThat(result.getResponse())
            .hasFieldOrPropertyWithValue("getErrorMessage", null));
  }


  @Controller
  private static class TestController {

    @GetMapping("/localized")
    private void localized() {
      throw new LocalizedException("error.localized") {
        @Override
        public int getStatusCode() {
          return HttpServletResponse.SC_BAD_REQUEST;
        }
      };
    }

    @GetMapping("/global")
    private void global() {
      throw new RuntimeException("Global error");
    }

  }

}
