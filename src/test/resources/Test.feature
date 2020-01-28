Feature: BDD Framework Test

  Scenario: BDD Test Fail 1
    Given BDD Framework I open the URL "https://www.google.com"
    And   I fail the test

  Scenario: BDD Test Pass 1
    Given BDD Framework I open the URL "https://www.google.com"

  Scenario: BDD Test Fail 2
    Given BDD Framework I open the URL "https://www.google.com"
    And   I fail the test

  Scenario: BDD Test Pass 2
    Given BDD Framework I open the URL "https://www.google.com"