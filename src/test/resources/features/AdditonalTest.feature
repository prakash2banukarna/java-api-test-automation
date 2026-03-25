@API
Feature:


  @GetRequestToValidateSingleField
  Scenario Outline: Ability to fetch a product and validate the data
    Given a GET request is made to fetch product with id and validate the field and value
      | id   | field   | value   |
      | <id> | <field> | <value> |


    Examples:
      | id | field            | value                |
      | 7  | name             | Apple MacBook Pro 16 |
      | 82 | data.'CPU model' | Intel Core i7        |


  @PostRequestWithAutoId
  Scenario: Ability to update a product
    Given a POST request is made to create a user
