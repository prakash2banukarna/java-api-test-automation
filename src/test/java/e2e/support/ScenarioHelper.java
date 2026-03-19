package e2e.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Scenario;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import java.util.List;
import java.util.Map;


@Data
@Component
public class ScenarioHelper {

    private Scenario scenario;

    public void embedLog(String description) {
        this.getScenario().log(description);
    }

    public void assertComparisonResult(Map<String, List<String>> comparisonResult, String description) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String prettyResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(comparisonResult);
            Assert.assertTrue(comparisonResult.isEmpty(), comparisonResult.size() + description + prettyResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
