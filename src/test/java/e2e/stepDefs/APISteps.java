package e2e.stepDefs;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import e2e.Database.models.Data;
import e2e.Database.models.ProductTable;
import e2e.Database.models.ResponseModelItem;
import e2e.Database.repository.ProductRepository;
import e2e.support.ScenarioHelper;
import e2e.support.Utils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@Slf4j
public class APISteps {
    @Autowired
    private final Utils utils;
    private final ScenarioHelper scenarioHelper;
    private final ProductRepository productRepository;
    List<JsonNode> jsonNode = new ArrayList<>();


    ResponseModelItem json;
    Response response;
    Set<String> allProductIds;
    String idRandom;
    private final ObjectMapper mapper = new ObjectMapper();
    List<ResponseModelItem> dbAPIdata;


    @Value("${DB_API_ENDPOINT}")
    String dbApiEndpoint;

    @Value("${API_KEY}")
    String dbApiKey;


    public APISteps(Utils utils, ScenarioHelper scenarioHelper, ProductRepository productRepository) {
        this.utils = utils;
        this.scenarioHelper = scenarioHelper;
        this.productRepository = productRepository;

    }

    public List<ResponseModelItem> productList;

    void validateResponseStatus(Response response, int expectedStatusCode, String description) {
        if (response.getStatusCode() != expectedStatusCode) {
            Assertions.assertEquals(expectedStatusCode,
                    response.getStatusCode(),
                    description + "Status code is not matched Body: " + response.getBody().toString());
        }
    }

    //Get request - Single product
    @Given("a GET request is made to fetch product with id")
    public void sendGetRequestForProductById(DataTable tbl) {
        Map<String, String> idMap = tbl.transpose().asMap();
        String id = idMap.get("id");

        if (!id.equals("<id>")) {
            id = idMap.get("id");
        } else {
            id = idRandom;
        }

        response = given().header("x-api-key", dbApiKey)
                .baseUri(dbApiEndpoint)
                .log().everything()
                .contentType(ContentType.JSON).when()
                .get("objects/" + id);
    }


    @Then("the response status code should be {int}")
    public void validateResponseStatusCode(int expectedResponseCode) {
        utils.validateResponsesStatus(response, expectedResponseCode, "Response code ");
        log.info("Response status code validated - Expected: {} | Actual: {}", expectedResponseCode, response.statusCode());

    }

    //Put Request
    @Given("a PUT request is made to update product with id")
    public void sendPutRequestToUpdateProductById(DataTable tbl) {
        Map<String, String> dataMap = tbl.asMaps().get(0);
        String id = dataMap.get("id");

        if (!id.equals("<id>")) {
            id = dataMap.get("id");
        } else {
            id = idRandom;
        }
        scenarioHelper.embedLog("PUT request initiated to update product with id: " + id);

//        String name = dataMap.get("name");
//        String year = dataMap.get("year");
//        String price        = "1908.98"; //Hardcoded to make the test fail
        String price = dataMap.get("price");
//        String cpuModel = dataMap.get("cpuModel");
//        String hardDiskSize = dataMap.get("hardDiskSize");

        // Step 1 — build the Data object
        Data data = Data.builder()
                .year(Integer.parseInt(dataMap.get("year")))
                .price(Float.parseFloat(price))
                .cPUModel(dataMap.get("cpuModel"))
                .hardDiskSize(dataMap.get("hardDiskSize"))
                .build();

        // Step 2 — build ResponseModelItem with the Data object
        ResponseModelItem expected = ResponseModelItem.builder()
                .id(id)
                .name(dataMap.get("name"))
                .data(data)
                .build();

        response = given().baseUri(dbApiEndpoint)
                .contentType(ContentType.JSON)
                .body(expected)
                .when()
                .put("objects/" + id);

    }

    @Then("I collect all existing products from api")
    @Then("I collect all existing product ids to generate a unique one")
    public void extractAllProductIdsFromResponse() {
        productList = Arrays.asList(response.getBody().as(ResponseModelItem[].class));
//        Set<String> allProductIds = productList.stream().map(message -> message.getId()).collect(Collectors.toSet());
        allProductIds = productList.stream().map(ResponseModelItem::getId).collect(Collectors.toSet());

        log.info("Extracted {} product id(s) from API response: {}", allProductIds.size(), allProductIds);
        scenarioHelper.embedLog("Existing product id's  " + allProductIds);

    }

    @Then("I collect all existing product ids using json")
    public void extractAllProductsAsJsonFromResponse() {
        jsonNode = Arrays.asList(response.getBody().as(JsonNode[].class));

        Assertions.assertFalse(jsonNode.isEmpty(), "API response returned an empty product list");
        log.info("Extracted {} product(s) as JsonNode from API response", jsonNode.size());
        scenarioHelper.embedLog("Extracted JsonNode product count: " + jsonNode.size());

    }

    @Given("a POST request is made to create a new product")
    public void sendPostRequestToCreateNewProduct(DataTable tbl) {

        /**
         * Generates a random ID (0-100) that does not conflict with existing product IDs.
         *
         * @return a unique string ID not present in {@code allProductIds}
         */
        int nums;
        Random rand = new Random();
        do {
            nums = rand.nextInt(0, 100);
        } while (allProductIds.contains(String.valueOf(nums)));
        idRandom = String.valueOf(nums);
        scenarioHelper.embedLog("Generated unique product id for POST request: " + idRandom);

        Map<String, String> data = tbl.asMaps().get(0);
        String name = data.get("name");
        String year = data.get("year");
        String price = data.get("price");
        String cpuModel = data.get("cpuModel");
        String hardDiskSize = data.get("hardDiskSize");

        Data dataForPostRequest = Data.builder()
                .year(Integer.parseInt(year))
                .price(Float.parseFloat(price))
                .cPUModel(cpuModel)
                .hardDiskSize(hardDiskSize)
                .build();

        ResponseModelItem postRequestBody = ResponseModelItem.builder()
                .id(idRandom)
                .name(name)
                .data(dataForPostRequest)
                .build();

        response = given().baseUri(dbApiEndpoint)
                .contentType(ContentType.JSON)
                .body(postRequestBody)
                .when()
                .post("objects");


        json = response.getBody().as(ResponseModelItem.class);
        log.info("New product created successfully with id: {}", idRandom);
        scenarioHelper.embedLog("New product created: " + json);
    }

    //Delete request
    @Given("a DELETE request is made to remove product with id")
    public void sendDeleteRequestToRemoveProductById(DataTable tbl) {
        Map<String, String> idMap = tbl.transpose().asMap();
        String id = idMap.get("id");

        if (!id.equals("<id>")) {
            id = idMap.get("id");
        } else {
            id = idRandom;
        }

        scenarioHelper.embedLog("DELETE request initiated to remove product with id: " + id);
        response = given()
                .baseUri(dbApiEndpoint)
                .contentType(ContentType.JSON)
                .when()
                .delete("objects/" + id);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Delete failed [" + response.statusCode() + "]: "
                    + response.getBody().asString());
        }

        log.info("Product with id: {} successfully deleted", id);
        scenarioHelper.embedLog("Successfully deleted product with id: " + id);
    }

    @And("a GET request for the deleted product with {string} should return {int}")
    public void verifyDeletedProductIsNoLongerRetrievable(String id, int expectedResponseCode) {

        response = given()
                .baseUri(dbApiEndpoint)
                .contentType(ContentType.JSON)
                .when()
                .get("objects/" + id);

        log.info("GET request for deleted product id: {} returned status: {}", id, response.statusCode());
        scenarioHelper.embedLog("GET request for deleted product id: " + id + " returned status: " + response.statusCode());
        utils.validateResponsesStatus(response, expectedResponseCode, "Response code");

    }

    //@Get all products
    @Given("a GET request is made to fetch all products")
    public void sendGetRequestToFetchAllProducts() {
        log.info("Sending GET request to fetch all products from: {}", dbApiEndpoint);
        response = given()
                .baseUri(dbApiEndpoint)
                .contentType(ContentType.JSON)
                .when()
                .get("objects/");

        log.info("GET all products response status: {}", response.statusCode());
        scenarioHelper.embedLog("GET all products response status: " + response.statusCode());

    }

    @Then("the response should only contain {int} products with a price greater than {float}")
    public void filterProductOnPrice(int productCount, float price) {


        productList = Arrays.asList(response.getBody().as(ResponseModelItem[].class));
        Map<String, ResponseModelItem> productsWithHighPrice = productList.stream().filter(message -> message.getData().getPrice() >= price)
                .collect(Collectors.toMap(ResponseModelItem::getId, Function.identity()));

        Assertions.assertEquals(productCount, productsWithHighPrice.size(), "Add the log");

    }

    //
    @Then("the total number of products in the response should be {int}")
    public void theResponseShouldContain(int expectedCount) {
        productList = Arrays.asList(response.getBody().as(ResponseModelItem[].class));
        Assertions.assertEquals(productList.size(), expectedCount,
                "Expected " + expectedCount + " products but got " + productList.size());
    }

    @Then("the response body should match the expected product details")
    public void verifyResponseBodyMatchesExpectedProductDetails(DataTable tbl) {

        Map<String, String> data = tbl.asMaps().get(0);

        json = response.getBody().as(ResponseModelItem.class);
        log.info("Validating response body for product id: {}", json.getId());
        scenarioHelper.embedLog("Response body received: " + json);

        List<String> mismatches = new ArrayList<>();
        utils.compareApiData(data, json, mismatches);

        log.info("Validation complete - {} mismatch(es) found", mismatches.size());
        scenarioHelper.embedLog("Validation mismatches: " + mismatches);
        Assertions.assertTrue(mismatches.isEmpty(), "Failure : " + mismatches);

    }


    /**
     * Loads all product records from the mock database JSON file.
     * The mock JSON file acts as the source of truth (simulating a database),
     * against which the actual API response data will be validated.
     * This approach demonstrates database-to-API data integrity verification
     * without requiring a live database connection.
     */

    @Given("I load all products from the mock database")
    public void loadAllProductsFromMockDatabase() {
        try {
            dbAPIdata = mapper.readValue(new File("./data/mockDbAPI.json"),
                    new TypeReference<List<ResponseModelItem>>() {
                    });
            Assertions.assertFalse(dbAPIdata.isEmpty(), "Mock database file returned no products");
            log.info("Successfully loaded {} product(s) from mock database", dbAPIdata.size());
            scenarioHelper.embedLog("Mock database loaded with " + dbAPIdata.size() + " product(s)");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Then("I validate mockDb product data with API response products")
    public void iValidateDbWithApi() {
        Map<String, ResponseModelItem> mockdbProducts = dbAPIdata.stream().collect(Collectors.toMap(ResponseModelItem::getId, Function.identity()));

        Map<String, ResponseModelItem> apiProducts = productList.stream().collect(Collectors.toMap(ResponseModelItem::getId, Function.identity()));

        Map<String, List<String>> finalComparison = new HashMap<>();
//        for(Map.Entry<String, ResponseModelItem> dbProductsSourceData : dbProducts.entrySet()){
        for (var dbProductsSourceData : mockdbProducts.entrySet()) {
            String dbProductIdSource = dbProductsSourceData.getKey();
            ResponseModelItem dataFromApi = apiProducts.get(dbProductIdSource);

            if (dataFromApi == null) {
                finalComparison.put(dbProductIdSource, List.of("Product found in mock DB but missing in API response"));
            } else {
                List<String> results = new ArrayList<>();
                utils.compareDbDataWithApiData(dbProductsSourceData.getValue(), dataFromApi, results);
                if (!results.isEmpty()) {
                    finalComparison.put(dbProductIdSource, results);
                }
            }
        }

        scenarioHelper.assertComparisonResult(finalComparison, "One or more products from the mock database do not match the API response");


    }

    @Then("I validate each product's data against the mock database using JsonPath expressions")
    public void validateEachProductAgainstMockDatabaseUsingJsonPath() {
        Map<String, ResponseModelItem> mockdbProducts = dbAPIdata.stream().collect(Collectors.toMap(ResponseModelItem::getId, Function.identity()));
        Map<String, JsonNode> apiProductsStoredInJson = jsonNode.stream()
                .collect(Collectors.toMap(
                        message -> message.at("/id").asText(),
                        Function.identity()
                ));

        Map<String, List<String>> finalComparison = new HashMap<>();
        for (var dbProductsSourceData : mockdbProducts.entrySet()) {
            String dbProductIdSource = dbProductsSourceData.getKey();
            JsonNode dataFromApi = apiProductsStoredInJson.get(dbProductIdSource);

            if (dataFromApi == null) {
                finalComparison.put(dbProductIdSource, List.of("Product found in mock DB but missing in API response"));
            } else {
                List<String> results = new ArrayList<>();
                utils.compareDbDataWithApiDataUsingJsonPath(dbProductsSourceData.getValue(), dataFromApi, results);
                if (!results.isEmpty()) {
                    finalComparison.put(dbProductIdSource, results);
                }
            }
        }

        scenarioHelper.assertComparisonResult(finalComparison, "One or more products from the mock database do not match the API response");


    }


    @Given("I collect all products from product table")
    public void fetchProductTableData() {

        Map<String, ProductTable> productTableData = productRepository.findAll().stream().collect(Collectors.toMap(ProductTable::getId, Function.identity()));
        log.info("Postgresql - {} Product Table count", productTableData.size());
        scenarioHelper.embedLog("Product Table count: " + productTableData.size());

    }

    /**
     * Validates product data between the PostgreSQL Product table and the API response.
     * The Product table acts as the source of truth — each product record from the
     * database is compared field-by-field against the corresponding API response product.
     * Mismatches are collected using a soft assertion approach and reported all at once.
     */
    @And("I validate product data between API and Product table")
    public void validateProductDataBetweenApiAndProductTable() {
        Map<String, ResponseModelItem> apiProducts = productList.stream().collect(Collectors.toMap(ResponseModelItem::getId, Function.identity()));

        //Acting as source of truth
        Map<String, ProductTable> productTableData = productRepository.findAll().stream().collect(Collectors.toMap(ProductTable::getId, Function.identity()));

        Map<String, List<String>> finalComparison = new HashMap<>();
//        for(Map.Entry<String, ResponseModelItem> dbProductsSourceData : dbProducts.entrySet()){
        for (var dbProductsSourceData : productTableData.entrySet()) {
            String dbProductIdSource = dbProductsSourceData.getKey();
            ResponseModelItem dataFromApi = apiProducts.get(dbProductIdSource);

            if (dataFromApi == null) {
                finalComparison.put(dbProductIdSource, List.of("Product found in postgresql DB Product table but missing in API response"));
            } else {
                List<String> results = new ArrayList<>();
                utils.compareProductTableDataWithApiData(dbProductsSourceData.getValue(), dataFromApi, results);
                if (!results.isEmpty()) {
                    finalComparison.put(dbProductIdSource, results);
                }
            }
        }

        scenarioHelper.assertComparisonResult(finalComparison, "One or more products from the Postgresql database product table do not match the API response");

    }


}





