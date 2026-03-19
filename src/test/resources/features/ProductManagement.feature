Feature: Product Management API
  As a consumer of the Product API
  I want to perform CRUD operations on products
  So that I can manage product data effectively

  @CRUD
  Scenario Outline: End-to-End CRUD operations on a product
  # CREATE
    Given a GET request is made to fetch all products
    When I collect all existing product ids to generate a unique one
    Then a POST request is made to create a new product
      | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |
    Then the response status code should be 201
    And the response body should match the expected product details
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |

  # READ
    Given a GET request is made to fetch product with id
      | id   |
      | <id> |
    Then the response status code should be 200
    And the response body should match the expected product details
      | id   | name   | year   | price   | cpuModel   | hardDiskSize   |
      | <id> | <name> | <year> | <price> | <cpuModel> | <hardDiskSize> |

  # UPDATE
    Given a PUT request is made to update product with id
      | id   | name          | year   | price          | cpuModel   | hardDiskSize   |
      | <id> | <updatedName> | <year> | <updatedPrice> | <cpuModel> | <hardDiskSize> |
    Then the response status code should be 200
    And the response body should match the expected product details
      | id   | name          | year   | price          | cpuModel   | hardDiskSize   |
      | <id> | <updatedName> | <year> | <updatedPrice> | <cpuModel> | <hardDiskSize> |

  # DELETE
    When a DELETE request is made to remove product with id
      | id   |
      | <id> |
    Then the response status code should be 200
    And a GET request for the deleted product with "<id>" should return 404

    Examples:
      | name        | year | price   | cpuModel      | hardDiskSize | updatedName     | updatedPrice |
      | Dell XPS 16 | 2028 | 1299.99 | Intel Core i7 | 512 GB       | Dell XPS 16 Pro | 1399.99      |