# java-api-test-automation

A production-grade REST API test automation framework built with Java, demonstrating end-to-end API
validation including full CRUD operations, mock database validation, real PostgreSQL database validation
hosted in Docker, and multi-strategy data comparison using model/POJO Class mapping, JsonPath expressions, and
direct database-to-API integrity checks.

---

## Purpose

This project showcases the design and implementation of a professional REST API testing framework. It demonstrates:

- Structured BDD test design using Cucumber feature files and Gherkin syntax
- Full CRUD operation coverage (GET, POST, PUT, DELETE) via RestAssured
- Data integrity validation against a **mock JSON database** (simulating a source of truth)
- Data integrity validation against a **real PostgreSQL database** hosted in Docker
- Four validation strategies: model-based, JsonPath-based, mock DB comparison, and PostgreSQL table comparison
- Soft assertion approach — all mismatches collected and reported in a single failure
- Environment-aware configuration using Spring `@Value` injection and properties files
- HikariCP connection pooling for efficient PostgreSQL connectivity
- Clean separation of concerns across config, step definitions, hooks, repository, and support utilities

---

## Tech Stack

| Technology         | Purpose                                                |
|--------------------|--------------------------------------------------------|
| Java 21            | Core language                                          |
| Cucumber 7.x       | BDD framework — feature files and step definitions     |
| TestNG 7.x         | Test runner — parallel execution and suite management  |
| JUnit 5            | Assertion library                                      |
| RestAssured 5.x    | HTTP client for API request/response handling          |
| Spring Framework 6 | Dependency injection and configuration management      |
| Spring Data JPA    | Repository layer for PostgreSQL database access        |
| Hibernate 6        | JPA implementation — ORM for entity mapping            |
| HikariCP           | High-performance JDBC connection pool                  |
| PostgreSQL 15      | Relational database hosted in Docker                   |
| Docker             | Containerised PostgreSQL database for local testing    |
| Jackson 2.x        | JSON serialisation / deserialisation                   |
| Lombok             | Boilerplate reduction (`@Data`, `@Builder`, `@Slf4j`)  |
| Logback            | Structured logging embedded into Cucumber HTML reports |
| Maven              | Build tool and dependency management                   |

---

## Project Structure

```
java-api-test-automation/
│
├── docker-compose.yml                          # Starts PostgreSQL container
├── data/
│   ├── mockDbAPI.json                          # Mock database — source of truth (JSON)
│   └── init.sql                               # Auto-loaded into PostgreSQL on first start
│
└── src/
    └── test/
        ├── java/
        │   └── e2e/
        │       ├── config/
        │       │   ├── AppConfiguration.java           # Spring config — API endpoint, API key
        │       │   └── PostgresqlDBConfiguration.java  # PostgreSQL DataSource + JPA + HikariCP
        │       ├── Database/
        │       │   ├── models/
        │       │   │   └── ProductTable.java           # JPA @Entity mapped to products table
        │       │   └── repository/
        │       │       └── ProductRepository.java      # Spring Data JPA repository
        │       ├── hooks/
        │       │   └── Hooks.java                      # Cucumber @Before/@After lifecycle hooks
        │       ├── runner/
        │       │   └── TestRunner.java                 # Cucumber test runner (TestNG + parallel)
        │       ├── stepDefs/
        │       │   └── APISteps.java                   # All step definitions (CRUD + validation)
        │       └── support/
        │           ├── ScenarioHelper.java             # Assertion helpers and report embedding
        │           └── Utils.java                      # Reusable field comparison utilities
        │
        └── resources/
            ├── features/
            │   ├── Get All Products.feature             # GET single/all product scenarios
            │   ├── mock_db_product_validation.feature   # Mock JSON DB vs API validation
            │   ├── ProductManagement.feature            # POST, PUT, DELETE scenarios
            │   └── Product Table Validation.feature     # PostgreSQL table vs API validation
            ├── env-dev.properties                       # Development environment config
            └── env-stg.properties                       # Staging environment config
```

---

## Key Features

### 1. Mock Database Validation

A `mockDbAPI.json` file acts as the source of truth, simulating a database. Product data loaded from
this file is validated field-by-field against the live API response — demonstrating data integrity
verification without requiring a live database connection.

### 2. PostgreSQL Database Validation (Docker)

A real PostgreSQL 15 database runs locally inside Docker. The `products` table is pre-loaded with
records from `init.sql` on first container start. Tests then validate the API response against the
actual database records — reflecting real-world backend-to-API integrity testing.

### 3. Four Validation Strategies

| Strategy                        | Source of Truth             | Approach                                                 |
|---------------------------------|-----------------------------|----------------------------------------------------------|
| **Model-based**                 | Mock JSON file              | API response deserialised into `ResponseModelItem` POJO  |
| **JsonPath-based**              | Mock JSON file              | API response traversed as raw `JsonNode` via JsonPath    |
| **DB model comparison**         | Mock JSON file              | Field-by-field comparison between DB model and API model |
| **PostgreSQL table comparison** | PostgreSQL `products` table | `ProductTable` entity vs API response via JPA repository |

### 4. Soft Assertions

All field comparisons collect mismatches into a `List<String>` rather than failing on the first
mismatch — ensuring all discrepancies across all products are visible in a single test failure report.

### 5. HikariCP Connection Pool

PostgreSQL connections are managed via HikariCP — the fastest JDBC connection pool available for the
JVM. Pool size, idle timeout, and leak detection are all configurable via properties files.

### 6. Environment-aware Configuration

All sensitive config (API endpoint, API key, DB credentials) is externalised into environment-specific
properties files. Switch environments with a single Maven flag — no code changes required.

---

## PostgreSQL Setup with Docker

### Prerequisites

- Docker Desktop installed and running
- On macOS M1: [Colima](https://github.com/abiosoft/colima) as a Docker runtime alternative

```bash
# If using Colima (macOS M1)
colima start

# Verify Docker is running
docker --version
```

### Step 1 — Generate `init.sql` (one-time only)

`init.sql` is generated from your existing `mockDbAPI.json` file using the `SqlGenerator` utility.
This only needs to be run once — or whenever `mockDbAPI.json` changes.

```
In IntelliJ:
Right click SqlGenerator.java → Run 'main()'
→ data/init.sql generated with all records ✅
```

What `init.sql` contains:

```sql
CREATE TABLE IF NOT EXISTS products (
    id              VARCHAR(50) PRIMARY KEY,
    name            VARCHAR(255),
    cpu_model       VARCHAR(255),
    hard_disk_size  VARCHAR(100),
    capacity_gb     INT,
    screen_size     VARCHAR(100),
    strap_colour    VARCHAR(100),
    case_size       VARCHAR(100)
);

INSERT INTO products VALUES ('1', 'MacBook Pro', 'Intel Core i9', '1 TB', 0, ...);
INSERT INTO products VALUES ('2', 'Dell XPS', 'Intel Core i7', '512 GB', 0, ...);
-- ... remaining records
```

### Step 2 — Start PostgreSQL container

```bash
cd java-api-test-automation
docker-compose up -d
```

`docker-compose.yml` does the following automatically:

- Pulls the official `postgres:15` image
- Creates the `api_test_db` database
- Creates `testuser` with credentials
- Mounts `data/init.sql` into the container's init directory
- PostgreSQL auto-executes `init.sql` on **first start only** — creating the table and loading all records

```yaml
volumes:
  - ./data/init.sql:/docker-entrypoint-initdb.d/init.sql
```

### Step 3 — Verify records loaded

```bash
# Connect to the running container
docker exec -it api-test-db psql -U testuser -d api_test_db

# Check record count
SELECT COUNT(*) FROM products;

# Preview records
SELECT id, name, cpu_model FROM products LIMIT 5;

# Exit
\q
```

### Step 4 — Stop the container

```bash
docker-compose down
```

> Note: `init.sql` only runs on **first container start**. Running `docker-compose down -v`
> removes volumes — the next `docker-compose up` will re-initialise from `init.sql`.

---

## Database Configuration

### `PostgresqlDBConfiguration.java`

Manages the full PostgreSQL connection setup including HikariCP connection pool, JPA entity manager,
and transaction manager. All values are injected from properties files via `@Value`.

```java

@Configuration
@EnableJpaRepositories(basePackages = "e2e.Database.repository")
@EnableTransactionManagement
public class PostgresqlDBConfiguration {

    @Bean(name = "sssDataSource")
    public DataSource sssDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);
        config.setLeakDetectionThreshold(10000);
        return new HikariDataSource(config);
    }
}
```

### `ProductRepository.java`

Spring Data JPA repository — Spring auto-generates all SQL queries from the interface definition:

```java

@Repository
public interface ProductRepository extends JpaRepository<ProductTable, String> {
    // findAll()       → SELECT * FROM products
    // findById(id)    → SELECT * FROM products WHERE id = ?
    // existsById(id)  → SELECT COUNT(*) FROM products WHERE id = ?
    // count()         → SELECT COUNT(*) FROM products
}
```

---

## Environment Configuration

### `env-dev.properties`

```properties
# API config
DB_API_ENDPOINT=http://localhost:3000/
API_KEY=your-api-key
# PostgreSQL config
DB_URL=jdbc:postgresql://localhost:5432/api_test_db
DB_USERNAME_POSTGRES=testuser
DB_PASSWORD_POSTGRES=testpass
```

### `env-stg.properties`

```properties
# API config
DB_API_ENDPOINT=https://staging-api.company.com/
API_KEY=stg-api-key
# PostgreSQL config
DB_URL=jdbc:postgresql://staging-db-host:5432/api_test_db
DB_USERNAME_POSTGRES=stg_user
DB_PASSWORD_POSTGRES=stg_password
```

Switch environments at runtime — no code changes needed:

```bash
mvn test -Denv=stg
```

---

## Feature Files & Tags

| Feature File                         | Tag                       | Description                                                     |
|--------------------------------------|---------------------------|-----------------------------------------------------------------|
| `mock_db_product_validation.feature` | `@ModelValidation`        | Validates API products against mock JSON DB using model mapping |
| `mock_db_product_validation.feature` | `@JsonPathValidation`     | Validates API products against mock JSON DB using JsonPath      |
| `Get All Products.feature`           | `@GetRequest`             | Fetches a single product by ID and validates response body      |
| `ProductManagement.feature`          | `@PostRequest`            | Creates a new product and validates creation                    |
| `ProductManagement.feature`          | `@PutRequest`             | Updates an existing product and validates changes               |
| `ProductManagement.feature`          | `@DeleteRequest`          | Deletes a product and verifies it is no longer retrievable      |
| `Product Table Validation.feature`   | `@ProductTableValidation` | Validates API products against PostgreSQL `products` table      |

---

## How to Run

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (with Colima on macOS M1)
- `json-server` for the mock API

### Start the mock API

```bash
json-server --watch data/mockDbAPI.json --port 3000
```

### Start PostgreSQL (required for `@ProductTableValidation` tests)

```bash
colima start          # macOS M1 only
docker-compose up -d
```

### Run all tests

```bash
mvn test
```

### Run by tag

```bash
# Mock DB — model-based validation
mvn test -Dcucumber.filter.tags="@ModelValidation"

# Mock DB — JsonPath validation
mvn test -Dcucumber.filter.tags="@JsonPathValidation"

# PostgreSQL table validation
mvn test -Dcucumber.filter.tags="@ProductTableValidation"

# All CRUD tests
mvn test -Dcucumber.filter.tags="@API"
```

### Run against staging environment

```bash
mvn test -Denv=stg
```

### View Test Report

```
target/reports/cucumber.html
```

---

## Example Scenarios

```gherkin
@ModelValidation
Scenario: Validate all products from API match mock database records
Given I load all products from the mock database
When a GET request is made to fetch all products
Then I collect all existing product ids
Then I validate each product's data against the mock database using model mapping

@ProductTableValidation
Scenario: Validate all products from API match PostgreSQL Product table records
When a GET request is made to fetch all products
Then I collect all existing product ids
And I validate product data between API and Product table
```

---

## Validation Output Example

When a mismatch is detected the framework reports all discrepancies clearly in a single failure:

```
PostgreSQL Product table validation complete — 2 product(s) with mismatches

{
  "5" : [ "cpu_model → Expected: Intel Core i7  Actual: Intel Core i9" ],
  "7" : [
    "hard_disk_size → Expected: 512 GB  Actual: 1 TB",
    "capacity_gb    → Expected: 0       Actual: 256"
  ]
}
```

---

## Architecture Overview

```
Feature Files (Gherkin)
        ↓
Step Definitions (APISteps.java)
        ↓
    ┌─────────────────────────────────────┐
    │  RestAssured — HTTP client          │  ──► Live API (json-server / real API)
    │  Utils.java — field comparisons     │
    │  ScenarioHelper — report embedding  │
    └─────────────────────────────────────┘
        ↓
    ┌─────────────────────────────────────┐
    │  Source of truth                    │
    │  ┌──────────────────────────────┐   │
    │  │  mockDbAPI.json              │   │  ──► Mock DB validation (no DB needed)
    │  └──────────────────────────────┘   │
    │  ┌──────────────────────────────┐   │
    │  │  PostgreSQL 15 (Docker)      │   │  ──► Real DB validation
    │  │  ProductRepository (JPA)     │   │
    │  │  HikariCP (connection pool)  │   │
    │  └──────────────────────────────┘   │
    └─────────────────────────────────────┘
        ↓
Cucumber HTML Report → target/reports/cucumber.html
```
