package e2e.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import e2e.Database.models.ResponseModelItem;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component // needed ?
public class Utils {

    public void compareValue(String fieldName, Object expected, Object actual, List<String> result) {

        if ((expected != null || actual == null) && !Objects.equals(expected, actual))
            result.add(fieldName + " ->Expected: " + expected + "  Actual: " + actual);
    }


    public void assertComparisonResult(Map<String, List<String>> comparisonResult, String description) {
        ObjectMapper objMapper = new ObjectMapper();
        try {
            String prettyResult = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(comparisonResult);
            Assertions.assertTrue(comparisonResult.isEmpty(),
                    comparisonResult.size() + description + prettyResult);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public void validateResponsesStatus(Response response, int expectedStatusCode, String description) {
        if (response.getStatusCode() != expectedStatusCode) {
            Assertions.assertEquals(expectedStatusCode, response.getStatusCode(),
                    description + "Status code is not matched Body: " + response.getBody().asString());
        }
    }


    /**
     * Compares expected product data from a DataTable against the actual API response body.
     * Each field is individually validated and any mismatches are collected into the results list
     * rather than failing immediately, allowing all discrepancies to be reported at once.
     *
     * @param expectedData field values from the Cucumber DataTable (source of truth)
     * @param apiResponse  deserialized API response body
     * @param result       list to collect field-level mismatch messages
     */
    public void compareApiData(Map<String, String> expectedData, ResponseModelItem apiResponse, List<String> result) {
        compareValue("name", expectedData.get("name"), apiResponse.getName(), result);
        compareValue("year", Integer.parseInt(expectedData.get("year")), apiResponse.getData().getYear(), result);
        compareValue("price", Float.parseFloat(expectedData.get("price")), apiResponse.getData().getPrice(), result);
        compareValue("CPU model", expectedData.get("cpuModel"), apiResponse.getData().getCPUModel(), result);
        compareValue("Hard disk size", expectedData.get("hardDiskSize"), apiResponse.getData().getHardDiskSize(), result);
    }

    /**
     * Compares product data from the mock database against the actual API response.
     * Each field is individually validated and any mismatches are collected into the
     * mismatches list rather than failing immediately, allowing all discrepancies
     * to be reported at once (soft assertion approach).
     *
     * @param mockDbProduct product record loaded from the mock database (source of truth)
     * @param apiProduct    deserialized API response product
     * @param result        list to collect field-level mismatch messages
     */
    public void compareDbDataWithApiData(ResponseModelItem mockDbProduct, ResponseModelItem apiProduct, List<String> result) {
        compareValue("name", mockDbProduct.getName(), apiProduct.getName(), result);
        compareValue("price", mockDbProduct.getData().getPrice(), apiProduct.getData().getPrice(), result);
        compareValue("year", mockDbProduct.getData().getYear(), apiProduct.getData().getYear(), result);
        compareValue("CPU model", mockDbProduct.getData().getCPUModel(), apiProduct.getData().getCPUModel(), result);
        compareValue("Hard disk size", mockDbProduct.getData().getHardDiskSize(), apiProduct.getData().getHardDiskSize(), result);
    }

    /**
     * Compares product data from the mock database against the actual API response using JsonPath expressions.
     * Each field is extracted from the API response via JsonPath and individually validated against
     * the mock database value. Any mismatches are collected into the mismatches list rather than
     * failing immediately, allowing all discrepancies to be reported at once (soft assertion approach).
     *
     * @param mockDbProduct product record loaded from the mock database (source of truth)
     * @param apiProduct    raw API response as a JsonNode (traversed via JsonPath expressions)
     * @param result        list to collect field-level mismatch messages
     */
    public void compareDbDataWithApiDataUsingJsonPath(ResponseModelItem mockDbProduct, JsonNode apiProduct, List<String> result) {
        compareValue("name", mockDbProduct.getName(), apiProduct.at("/name").asText(), result);
        compareValue("price", mockDbProduct.getData().getPrice(), Float.parseFloat(apiProduct.at("/data/price").asText()), result);
        compareValue("year", mockDbProduct.getData().getYear(), apiProduct.at("/data/year").asInt(), result);
        compareValue("CPU model", mockDbProduct.getData().getCPUModel(), apiProduct.at("/data/CPU model").asText(), result);
        compareValue("Hard disk size", mockDbProduct.getData().getHardDiskSize(), apiProduct.at("/data/Hard disk size").asText(), result);
    }


}
