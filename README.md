# java-api-test-automation

A REST API test automation framework built with Java,
demonstrating end-to-end API validation including CRUD operations,
mock database validation, and multi-strategy data comparison using both model mapping and JsonPath expressions.

---

## Purpose

This project showcases the design and implementation of a production-grade REST API testing framework. It demonstrates:

- Structured BDD test design using Cucumber feature files
- Full CRUD operation coverage via RestAssured
- Data integrity validation by comparing API responses against a mock JSON database (simulating a real database source
  of truth)
- Two validation strategies: **Model-based** and **JsonPath-based** comparison
- Environment-aware configuration using Spring's `@Value` injection
- Clean separation of concerns across config, step definitions, hooks, and support utilities

---

## Tech Stack

| Technology  | Purpose                                           |
|-------------|---------------------------------------------------|
| Java 17+    | Core language                                     |
| Cucumber    | BDD framework for feature file execution          |
| JUnit 5     | Assertion library                                 |
| RestAssured | HTTP client for API request/response handling     |
| Spring Boot | Dependency injection and configuration management |
| Jackson     | JSON serialisation / deserialisation              |
| Lombok      | (`@Data`, `@Builder`)                             |
| Maven       | Build and dependency management                   |

---

## Project Structure

```
java-api-test-automation/
│
├── data/
│   └── mockDbAPI.json                  # Mock database (source of truth for validation)
│
└── src/
    └── test/
        ├── java/
        │   └── e2e/
        │       ├── config/
        │       │   └── AppConfiguration.java       # Spring @Value config (endpoint, API key)
        │       ├── Database/                        # Mock DB loading logic
        │       ├── hooks/
        │       │   └── Hooks.java                  # Cucumber Before/After hooks
        │       ├── runner/
        │       │   └── TestRunner.java             # Cucumber test runner
        │       ├── stepDefs/
        │       │   └── APISteps.java               # All step definitions (CRUD + validation)
        │       └── support/
        │           ├── ScenarioHelper.java         # Assertion helpers and report embedding
        │           └── Utils.java                  # Reusable comparison utilities
        │
        └── resources/
            ├── features/
            │   ├── Get All Products.feature        # GET single/all product scenarios
            │   ├── mock_db_product_validation.feature  # Mock DB vs API validation scenarios
            │   └── ProductManagement.feature       # POST, PUT, DELETE scenarios
            ├── env-dev.properties                  # Development environment config
            └── env-stg.properties                  # Staging environment config
```

---

## Key Features

### Mock Database Validation

A `mockDbAPI.json` file acts as the source of truth, simulating a database. Product data loaded from this file is
validated field-by-field against the live API response — demonstrating data integrity verification without requiring a
live database connection.

### Two Validation Strategies

| Strategy           | Description                                                                                         |
|--------------------|-----------------------------------------------------------------------------------------------------|
| **Model-based**    | API response deserialised into `ResponseModelItem` POJO using Jackson, then compared field by field |
| **JsonPath-based** | API response traversed as a raw `JsonNode` using JsonPath expressions (e.g. `/data/price`)          |

### Soft Assertions

All field comparisons collect mismatches into a `List<String>` rather than failing on the first mismatch — ensuring all
discrepancies are reported in a single test failure.

### Environment Configuration

API endpoint and keys are externalised into `env-dev.properties` and `env-stg.properties`, injected via Spring
`@Value` — no hardcoded credentials in the codebase.

---

## Feature Files & Tags

| Feature File                         | Tag                   | Description                                                       |
|--------------------------------------|-----------------------|-------------------------------------------------------------------|
| `mock_db_product_validation.feature` | `@ModelValidation`    | Validates API products against mock DB using model mapping        |
| `mock_db_product_validation.feature` | `@JsonPathValidation` | Validates API products against mock DB using JsonPath expressions |
| `Get All Products.feature`           | `@GetRequest`         | Fetches a single product by ID and validates response body        |
| `ProductManagement.feature`          | `@PostRequest`        | Creates a new product and validates creation                      |
| `ProductManagement.feature`          | `@PutRequest`         | Updates an existing product and validates changes                 |
| `ProductManagement.feature`          | `@DeleteRequest`      | Deletes a product and verifies it is no longer retrievable        |

---

## How to Run

### Prerequisites

- Java 17+
- Maven 3.8+

### Set Environment Variables

```bash
export DB_API_ENDPOINT=https://your-api-endpoint.com/
export API_KEY=your-api-key
```

Or configure them in `env-dev.properties`:

```properties
DB_API_ENDPOINT=https://your-api-endpoint.com/
API_KEY=your-api-key
```

### Run test by Tag

```bash
# Run model-based validation only
mvn test -Dcucumber.filter.tags="@ModelValidation"

# Run JsonPath-based validation only
mvn test -Dcucumber.filter.tags="@JsonPathValidation"

# Run all product management tests
mvn test -Dcucumber.filter.tags="@API"
```

### View Test Report

After execution, the Cucumber HTML report is available at:

```
target/reports/cucumber.html
```

---

## Example Scenario

```gherkin
@ModelValidation
Scenario: Validate all products from API match mock database records
Given I load all products from the mock database
When I send a GET request to retrieve all products
Then I extract all product ids from the API response
Then I validate each product's data against the mock database using model mapping
```

---

## Validation Output Example

When a mismatch is detected, the framework reports all discrepancies clearly:

```
Validation mismatches: [
  year   -> Expected: 2009  Actual: 2109,
  price  -> Expected: 1908.98  Actual: 1849.91
]
```