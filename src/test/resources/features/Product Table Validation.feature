@ProductTableValidation
Feature: PostgreSQL Product table to API validation
  Validate that API response data matches records stored in the PostgreSQL Product table


  @ProductTableValidation
  Scenario: Validate all API products match PostgreSQL Product table records
    Given a GET request is made to fetch all products
    When  I collect all existing products from api
    Then I collect all products from product table
    And I validate product data between API and Product table
