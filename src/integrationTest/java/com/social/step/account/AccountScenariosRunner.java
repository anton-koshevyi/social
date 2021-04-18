package com.social.step.account;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features/account",
    glue = {
        "com.social.hook",
        "com.social.step.account",
        "com.social.step.common"
    }
)
public class AccountScenariosRunner {
}
