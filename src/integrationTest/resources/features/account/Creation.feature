Feature: Account creation

  Endpoint: POST /account

  Scenario: Account creation without auto-login
    When Request to create account with auto-login false
    Then Hidden payload of account
    And Response status is 200
    And Account of John Smith saved to database

  Scenario: Account creation with auto-login
    When Request to create account with auto-login true
    Then Regular payload of account
    And Response status is 200
    And Account of John Smith saved to database
