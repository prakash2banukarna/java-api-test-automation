@API
Feature: Product Management API
  As a consumer of the Product API
  I want to perform CRUD operations on products
  So that I can manage product data effectively


  @GetRequest
  Scenario Outline: Ability to fetch a product and validate the data
    Given a GET request is made to fetch product with id
      | id   |
      | <id> |
    Then the response status code should be 200
    And the response body should match the expected product details
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |

    Examples:
      | id | name                 | year | price   | cpuModel      | hardDiskSize |
      | 7  | Apple MacBook Pro 16 | 2009 | 1908.98 | Intel Core i9 | 1 TB         |
#      | 5  | Apple MacBook Pro 22 |2019|1849.98|Intel Core i9|1 TB|

  @PutRequest
  Scenario Outline: Ability to update a product
    Given a PUT request is made to update product with id
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |
    Then the response status code should be 200
    And the response body should match the expected product details
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |


    Examples:
      | id | name                 | year | price   | cpuModel      | hardDiskSize |
      | 7  | Apple MacBook Pro 16 | 2109 | 1849.91 | Intel Core i9 | 1 TB         |


  # NOTE: Expected to FAIL
  # API treats nested 'data' object as full replacement in PATCH
  # Only top-level fields support partial update
  # Fix: Send all data fields in PATCH request body

  @PatchRequest @KnownDefect
  Scenario Outline: Ability to update partial fields of product
    Given a PATCH request is made to update product with id
      | id   | name                 | price |
      | <id> | Apple MacBook Pro 18 | 2131  |
    Then the response status code should be 200
    When a GET request is made to fetch product with id
      | id   |
      | <id> |
    And the response body should match the expected product details
      | id   | name   | price   |
      | <id> | <name> | <price> |
    Examples:
      | id | name                 | price   |
      | 7  | Apple MacBook Pro 18 | 1849.91 |

  @PostRequest
  Scenario Outline: Ability to create a product
    Given a GET request is made to fetch all products
    When I collect all existing product ids to generate a unique one
    Then a POST request is made to create a new product
      | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |
    Then the response status code should be 201
    Then the response body should match the expected product details
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |

    Examples:
      | name        | year | price   | cpuModel      | hardDiskSize |
      | Dell XPS 19 | 2028 | 1299.99 | Intel Core i9 | 256 GB       |
#      | Lenovo ThinkPad   | 2028 | 1099.99| Intel Core i5   | 256 GB       |




  @DeleteRequest
  Scenario Outline: Ability to delete a product

    When a DELETE request is made to remove product with id
      | id   |
      | <id> |
    Then the response status code should be 200
    And a GET request for the deleted product with "<id>" should return 404
    Examples:
      | id   |
      | 1ce4 |


  @GetAllProducts
  Scenario: Ability to retrieve all the products and validate the total count
    When a GET request is made to fetch all products
    Then the response status code should be 200
    Then the total number of products in the response should be 5


  @GetProductWithPrice
  Scenario: Ability to retrieve products filtered by price threshold
    When a GET request is made to fetch all products
    Then the response status code should be 200
    Then the response should only contain 5 products with a price greater than 1800
