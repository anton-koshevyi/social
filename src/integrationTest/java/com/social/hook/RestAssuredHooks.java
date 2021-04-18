package com.social.hook;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import com.social.test.ContextKeys;
import com.social.test.HooksOrders;
import com.social.test.SharedContext;

public class RestAssuredHooks {

  static {
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
  }

  @Before(order = HooksOrders.REST_ASSURED)
  public void setupPort() {
    RestAssured.port = SharedContext.get(ContextKeys.PORT_APPLICATION);
  }

}
