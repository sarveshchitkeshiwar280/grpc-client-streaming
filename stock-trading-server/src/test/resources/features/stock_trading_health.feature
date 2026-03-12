Feature: Stock Trading Application Health Check

  Background:
    Given the Stock Trading Application is running

  @Smoke
  Scenario: Verify application health endpoint
    When I request the health status from the application
    Then the response should indicate the overall status is "UP"

  @Health
  Scenario: Verify database connectivity
    When I request the health status from the application
    Then the database connection status should be "UP"
