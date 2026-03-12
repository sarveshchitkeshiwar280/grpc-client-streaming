Feature: Stock Trading Application Smoke Test

  Scenario: Verify Spring context loads successfully
    Given the Stock Trading Application is running
    When I check the health endpoint
    Then I should get a successful response
