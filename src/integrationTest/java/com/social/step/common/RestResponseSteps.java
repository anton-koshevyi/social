package com.social.step.common;

import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import com.social.test.ContextKeys;
import com.social.test.SharedContext;

public class RestResponseSteps {

  @Then("Response status is {int}")
  public void responseStatusIs(int statusCode) {
    Response response = SharedContext.get(ContextKeys.RESPONSE);

    Assertions
        .assertThat(response.statusCode())
        .isEqualTo(statusCode);
  }

}
