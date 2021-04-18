Feature: Account creation

  Endpoint: POST /account

  Scenario: Account creation with existing email
    Given Account of John Smith
    But Account of John Smith has unique username
    When Request to create account with auto-login false
    Then Error payload of existent email
    And Response status is 400

  Scenario: Account creation with existing username
    Given Account of John Smith
    But Account of John Smith has unique email
    When Request to create account with auto-login false
    Then Error payload of existent username
    And Response status is 400

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
