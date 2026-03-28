@ProductAPIDemo
Feature: Product API Validation
  Validate that the product API response data matches the mock database

  Background:
    Given I load all products from the mock database
    When a GET request is made to fetch all products


  @ModelValidation
  Scenario: Validate all products from API match mock database records
    Then I collect all existing product ids
    Then I validate mockDb product data with API response products


  @JsonPathValidation
  Scenario: Validate all products from API match mock database records using JsonPath
    Then I collect all existing product ids using json
    Then I validate each product's data against the mock database using JsonPath expressions




