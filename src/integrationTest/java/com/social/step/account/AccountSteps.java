package com.social.step.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.social.test.ContextKeys;
import com.social.test.SharedContext;

public class AccountSteps {

  @When("Request to create account with auto-login {}")
  public void requestToCreateAccountWithAutoLogin(boolean autoLogin) {
    Response response = RestAssured
        .given()
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .body("{"
            + "  \"email\": \"johnsmith@example.com\","
            + "  \"username\": \"johnsmith\","
            + "  \"firstName\": \"John\","
            + "  \"lastName\": \"Smith\","
            + "  \"password\": \"password\","
            + "  \"confirm\": \"password\""
            + "}")
        .queryParam("autoLogin", autoLogin)
        .when()
        .post("/account");
    SharedContext.put(ContextKeys.RESPONSE, response);
  }

  @Then("Hidden payload of account")
  public void hiddenPayloadOfAccount() throws Exception {
    Response response = SharedContext.get(ContextKeys.RESPONSE);
    String actual = response.asString();

    String expected = "{"
        + "  id: 1,"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}";
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Then("Regular payload of account")
  public void regularPayloadOfAccount() throws Exception {
    Response response = SharedContext.get(ContextKeys.RESPONSE);
    String actual = response.asString();

    String expected = "{"
        + "  id: 1,"
        + "  email: 'johnsmith@example.com',"
        + "  username: 'johnsmith',"
        + "  firstName: 'John',"
        + "  lastName: 'Smith',"
        + "  publicity: 10,"
        + "  moder: false,"
        + "  admin: false"
        + "}";
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Then("Account of John Smith saved to database")
  public void accountOfJohnSmithSavedToDatabase() throws Exception {
    String sql = ""
        + " select u.*"
        + " from users u";

    DataSource postgresDataSource = SharedContext.get(ContextKeys.DATASOURCE_POSTGRES);

    try (Connection connection = postgresDataSource.getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(resultSet.next()).isTrue();
      softly.assertThat(resultSet.getObject("id")).isEqualTo(1L);
      softly.assertThat(resultSet.getObject("email")).isEqualTo("johnsmith@example.com");
      softly.assertThat(resultSet.getObject("username")).isEqualTo("johnsmith");
      softly.assertThat(resultSet.getObject("first_name")).isEqualTo("John");
      softly.assertThat(resultSet.getObject("last_name")).isEqualTo("Smith");
      softly.assertThat(resultSet.getObject("publicity")).isEqualTo(10);
      softly.assertThat(resultSet.getObject("password")).isNotNull();
      softly.assertThat(resultSet.getObject("role_moder")).isEqualTo(false);
      softly.assertThat(resultSet.getObject("role_admin")).isEqualTo(false);
      softly.assertAll();
    }
  }

}
