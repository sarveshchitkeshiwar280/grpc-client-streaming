package com.javatechie.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.javatechie.cucumber.steps", "com.javatechie.cucumber"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml"
    },
    monochrome = true,
    tags = "@Smoke"
)
public class CucumberTestRunner {
    // This is the Cucumber test runner
    /*
    step 1 : we need to integrated rate limiting as per my latest grpc poc+ upgrading some driven case
    scenerios handle make sure carefully resolve confict either facing any blocker comes stage , prod carefully checkout

     */
}
