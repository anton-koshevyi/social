package com.social.step.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.social.test.ContextKeys;
import com.social.test.Customizations;
import com.social.test.SharedContext;

public class AccountSteps {

  @Given("Account of John Smith")
  public void accountOfJohnSmith() throws Exception {
    String sql = ""
        + "insert into users ("
        + "  email,"
        + "  username,"
        + "  first_name,"
        + "  last_name,"
        + "  publicity,"
        + "  password,"
        + "  role_moder,"
        + "  role_admin"
        + ") values ("
        + "  'johnsmith@example.com',"
        + "  'johnsmith',"
        + "  'John',"
        + "  'Smith',"
        + "  10,"
        + "  '$2y$10$ftOMnAlaVfm63/.9o95GtOcjAzX2QFCLnTld/8fdlzyjpL5T2EjO6'," /* password */
        + "  false,"
        + "  false"
        + ")";

    DataSource postgresDataSource = SharedContext.get(ContextKeys.DATASOURCE_POSTGRES);

    try (Connection connection = postgresDataSource.getConnection();
         Statement statement = connection.createStatement()) {
      Assertions
          .assertThat(statement.executeUpdate(sql))
          .isEqualTo(1);
    }
  }

  @Given("Account of John Smith has unique email")
  public void accountOfJohnSmithHasUniqueEmail() throws Exception {
    String sql = ""
        + " update users"
        + " set email = 'uniquesmith@example.com'"
        + " where email = 'johnsmith@example.com'";

    DataSource postgresDataSource = SharedContext.get(ContextKeys.DATASOURCE_POSTGRES);

    try (Connection connection = postgresDataSource.getConnection();
         Statement statement = connection.createStatement()) {
      Assertions
          .assertThat(statement.executeUpdate(sql))
          .isEqualTo(1);
    }
  }

  @Given("Account of John Smith has unique username")
  public void accountOfJohnSmithHasUniqueUsername() throws Exception {
    String sql = ""
        + " update users"
        + " set username = 'uniquesmith'"
        + " where username = 'johnsmith'";

    DataSource postgresDataSource = SharedContext.get(ContextKeys.DATASOURCE_POSTGRES);

    try (Connection connection = postgresDataSource.getConnection();
         Statement statement = connection.createStatement()) {
      Assertions
          .assertThat(statement.executeUpdate(sql))
          .isEqualTo(1);
    }
  }

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

    String expected = ""
        + "{"
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

    String expected = ""
        + "{"
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

  @Then("Error payload of existent email")
  public void errorPayloadOfExistentEmail() throws Exception {
    Response response = SharedContext.get(ContextKeys.RESPONSE);
    String actual = response.asString();

    String expected = ""
        + "{"
        + "  timestamp: (customized),"
        + "  status: 400,"
        + "  error: 'Bad Request',"
        + "  message: 'Invalid body: 1 error(s)',"
        + "  errors: {"
        + "    email: ['email already exists']"
        + "  },"
        + "  path: '/account'"
        + "}";
    JSONAssert.assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
        Customizations.notNullActual("timestamp")
    ));
  }

  @Then("Error payload of existent username")
  public void errorPayloadOfExistentUsername() throws Exception {
    Response response = SharedContext.get(ContextKeys.RESPONSE);
    String actual = response.asString();

    String expected = ""
        + "{"
        + "  timestamp: (customized),"
        + "  status: 400,"
        + "  error: 'Bad Request',"
        + "  message: 'Invalid body: 1 error(s)',"
        + "  errors: {"
        + "    username: ['username already exists']"
        + "  },"
        + "  path: '/account'"
        + "}";
    JSONAssert.assertEquals(expected, actual, new CustomComparator(JSONCompareMode.NON_EXTENSIBLE,
        Customizations.notNullActual("timestamp")
    ));
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
