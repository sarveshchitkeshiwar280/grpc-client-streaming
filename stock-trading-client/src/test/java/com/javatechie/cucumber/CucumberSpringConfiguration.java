package com.javatechie.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // This class provides Spring context for Cucumber tests
}


/*
  step 1:" we will integrated configurations 10x faster as compare to maven using most of time using progamatic logic (gradle.yml and build.gradle) => growing and mobile app its directly connect with git
 */