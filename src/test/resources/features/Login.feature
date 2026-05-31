@UI @SauceDemo
Feature: SauceDemo login validation
  Validate login behaviour on SauceDemo using Selenium Page Object Model

  Background:
    Given I am on the SauceDemo login page

  @SuccessfulLogin
  Scenario Outline: Successful login with valid credentials
    When I try to login with "<username>" and "<password>"
    Then I should be redirected to the inventory page
    And the inventory page title should be "Products"

    Examples:
      | username      | password     |
      | standard_user | secret_sauce |

  @LockedUser
  Scenario Outline: Locked out user cannot login
    When I try to login with "<username>" and "<password>"
    Then I should see the login error message "Sorry, this user has been locked out"

    Examples:
      | username        | password     |
      | locked_out_user | secret_sauce |

  @InvalidCredentials
  Scenario Outline: Login fails with invalid credentials
    When I try to login with "<username>" and "<password>"
    Then I should see the login error message "Username and password do not match"

    Examples:
      | username      | password     |
      | invalid_user  | secret_sauce |
      | standard_user | wrong_pass   |

  @EmptyFields
  Scenario Outline: Login fails with empty fields
    When I try to login with "<username>" and "<password>"
    Then I should see the login error message "<errorMessage>"

    Examples:
      | username      | password     | errorMessage         |
      |               | secret_sauce | Username is required |
      | standard_user |              | Password is required |