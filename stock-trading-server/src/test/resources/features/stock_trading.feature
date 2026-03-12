Feature: Stock Trading Application

  Scenario: Verify application is running
    Given the Stock Trading Application is running
    When I check the health endpoint
    Then I should get a successful response
