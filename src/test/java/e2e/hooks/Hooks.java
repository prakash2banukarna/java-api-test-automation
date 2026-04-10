package e2e.hooks;

import e2e.config.AppConfiguration;
import e2e.config.PostgresqlDBConfiguration;
import e2e.config.WebDriverConfig;
import e2e.stepDefs.APISteps;
import e2e.support.ScenarioHelper;
import io.cucumber.java.*;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Cucumber hooks for test execution and failure tracking.
 * <p>
 * This class provides Cucumber hooks that integrate with the test lifecycle to
 * track execution and record failures. It connects the
 * Cucumber test framework with the TestFailureTracker.
 * </p>
 */
@Slf4j
@CucumberContextConfiguration
//@ContextHierarchy({
//        @ContextConfiguration(classes = AppConfiguration.class),
//        @ContextConfiguration(classes = PostgresqlDBConfiguration.class),
//        @ContextConfiguration(classes = WebDriverConfig.class)
//})
@ContextConfiguration(classes = {
        AppConfiguration.class,
        PostgresqlDBConfiguration.class,
        WebDriverConfig.class
})

public class Hooks {
    private final Map<String, LocalDateTime> scenarioStartTimes = new HashMap<>();
    private final Map<String, String> stepStartTimes = new HashMap<>();

    @Autowired
    ScenarioHelper scenarioHelper;
    @Autowired(required = false)
    @Lazy
    APISteps apiSteps;


    @BeforeAll
    public static void before_all() {
        log.info("=== Environment : {} - Test Execution Started ===", System.getProperty("env").toUpperCase());
        log.info("Initializing test environment with configuration:  AppConfiguration");

    }

    /**
     * Executes after the entire test suite completes.
     * <p>
     * This method exports any failures and sends notifications via
     * SNSMessageSender if failures occurred.
     * </p>
     */
    @AfterAll
    public static void after_all() {
        log.info("=== Test Execution Completed ===");
    }


    @Before
    public void before_scenario(Scenario scenario) {
        String scenarioName = scenario.getName();
        scenarioStartTimes.put(scenarioName, LocalDateTime.now());

        MDC.put("scenario", scenarioName);
        log.info("=== Scenario Started in thread {} ===", Thread.currentThread().threadId());
        scenarioHelper.setScenario(scenario);
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        String stepId = getStepId(scenario);
        stepStartTimes.put(stepId, LocalDateTime.now().toString());
    }

    /**
     * Generates a unique identifier for a step within a scenario.
     * <p>
     * This method creates a unique ID for each step by combining the
     * scenario ID and line number.
     * </p>
     *
     * @param scenario The Cucumber scenario containing the step
     * @return A unique string identifier for the step
     */
    private String getStepId(Scenario scenario) {
        return scenario.getId() + "_" + scenario.getLine();
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        String stepId = getStepId(scenario);
        String startTime = stepStartTimes.remove(stepId);
        if (startTime != null) {
            LocalDateTime start = LocalDateTime.parse(startTime);
            Duration duration = Duration.between(start, LocalDateTime.now());
            log.info("Step Completed - Duration: {} seconds", duration.toSeconds());
        }
    }


}