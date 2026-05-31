@InventoryActions
Feature: Inventory check practice

  Background:
    Given I am on the SauceDemo login page

  @CountProductOnInventoryPage
  Scenario Outline: Count the products available on inventory page
    When I try to login with "<username>" and "<password>"
    Then I should be redirected to the inventory page
    Then the inventory page should display 7 products

    Examples:
      | username      | password     |
      | standard_user | secret_sauce |


  @ValidateLowPriceProductDetails
  Scenario Outline: Count the products available on inventory page
    When I try to login with "<username>" and "<password>"
    Then I should be redirected to the inventory page
    When I sort the products by "Price (low to high)"
    Then the lowest product price is "$7.99"
    Then the lowest product name is "Sauce Labs Backpackk"


    Examples:
      | username      | password     |
      | standard_user | secret_sauce |