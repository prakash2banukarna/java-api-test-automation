package e2e.hooks;

import e2e.config.AppConfiguration;
import e2e.config.PostgresqlDBConfiguration;
import e2e.config.WebDriverConfig;
import e2e.stepDefs.APISteps;
import e2e.support.ScenarioHelper;
import io.cucumber.java.*;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private int stepCounter = 1;

    @Autowired
    ScenarioHelper scenarioHelper;
    @Autowired(required = false)
    @Lazy
    APISteps apiSteps;

    @Autowired(required = false)
    @Lazy
    private TakesScreenshot takesScreenshot;

    @Autowired(required = false)
    @Lazy
    private WebDriver driver;

    @Value("${screenshot.mode:failed}")  // defaults to failed if not set
    private String screenshotMode;


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
        stepCounter = 1;
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

    // Scenario names can have spaces and special characters — not valid in file paths
    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }

//    @AfterStep
//    public void afterStep(Scenario scenario) {
//        String stepId = getStepId(scenario);
//        String startTime = stepStartTimes.remove(stepId);
//        if (startTime != null) {
//            LocalDateTime start = LocalDateTime.parse(startTime);
//            Duration duration = Duration.between(start, LocalDateTime.now());
//            log.info("Step Completed - Duration: {} seconds", duration.toSeconds());
//        }
//    }

    /**
     * // As bytes — embed in Cucumber report
     * byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
     * <p>
     * // As Base64 string — useful for logging
     * String base64 = driver.getScreenshotAs(OutputType.BASE64);
     * <p>
     * // As file — save to disk
     * File file = driver.getScreenshotAs(OutputType.FILE);
     * FileUtils.copyFile(file, new File("screenshots/failure.png"));
     */
    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed() && takesScreenshot != null) {
            try {
                byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on failure");
                log.info("Screenshot captured for failed scenario: {}", scenario.getName());
            } catch (Exception e) {
                log.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }

        if (driver != null) {
            driver.quit();
        }
    }

    //Taking screenshot irrespective of pass or fail
    @AfterStep
    public void afterStep(Scenario scenario) {

        // Existing duration logging
        String stepId = getStepId(scenario);
        String startTime = stepStartTimes.remove(stepId);
        if (startTime != null) {
            LocalDateTime start = LocalDateTime.parse(startTime);
            Duration duration = Duration.between(start, LocalDateTime.now());
            log.info("Step Completed - Duration: {} seconds", duration.toSeconds());
        }

        // Screenshot control
        boolean shouldCapture = switch (screenshotMode.toLowerCase()) {
            case "all" -> true;                    // always capture
            case "failed" -> scenario.isFailed();     // only on failure
            default -> scenario.isFailed();     // default to failed
        };

        if (shouldCapture && takesScreenshot != null) {
            try {
                String folderPath = "target/screenshots/" + sanitize(scenario.getName());
                Files.createDirectories(Paths.get(folderPath));

                String status = scenario.isFailed() ? "FAILED" : "PASSED";
                String fileName = folderPath + "/" + stepCounter + "_" + status + ".png";

                // Save to file
                File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), Paths.get(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                // Attach to HTML report
                byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshotBytes, "image/png",
                        "Step " + stepCounter + " — " + status);

                stepCounter++;

            } catch (Exception e) {
                log.error("Failed to capture step screenshot: {}", e.getMessage());
            }
        }
    }

}