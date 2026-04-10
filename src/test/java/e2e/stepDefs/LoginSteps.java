package e2e.stepDefs;

import e2e.Database.uiModels.HomePage;
import e2e.Database.uiModels.InventoryPage;
import e2e.Database.uiModels.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class LoginSteps {

    @Value("${browser:chrome}")
    private String browser;

    @Autowired
    private HomePage homePage;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private InventoryPage inventoryPage;

    /**
     * Navigates to SauceDemo login page via HomePage.
     * HomePage.goToHomePage() opens the URL from application.url property.
     */
    @Given("I am on the SauceDemo login page")
    public void iAmOnTheSauceDemoLoginPage() {
        homePage.goToHomePage();
        log.info("Navigated to SauceDemo login page — browser: {}", browser);
    }

    /**
     * Enters username and password into the login form.
     * Empty strings simulate empty field scenarios.
     */
    @When("I try to login with {string} and {string}")
    public void iTryToLoginWith(String username, String password) {
        loginPage.login(username, password);
        log.info("Login attempted — username: '{}'", username);
    }

    /**
     * Verifies successful login by checking redirect to inventory page.
     */
    @Then("I should be redirected to the inventory page")
    public void iShouldBeRedirectedToTheInventoryPage() {
        boolean onInventoryPage = inventoryPage.isOnInventoryPage();
        log.info("On inventory page: {}", onInventoryPage);
        Assertions.assertTrue(onInventoryPage,
                "Expected to land on inventory page but current URL was: "
                        + inventoryPage.getCurrentUrl());
    }

    /**
     * Verifies the inventory page title after successful login.
     */
    @Then("the inventory page title should be {string}")
    public void theInventoryPageTitleShouldBe(String expectedTitle) {
        String actualTitle = inventoryPage.getTitle();
        log.info("Inventory page title — Expected: {} | Actual: {}", expectedTitle, actualTitle);
        Assertions.assertEquals(expectedTitle, actualTitle,
                "Inventory page title mismatch — Expected: " + expectedTitle
                        + " | Actual: " + actualTitle);
    }

    /**
     * Verifies error message displayed on failed login attempt.
     */
    @Then("I should see the login error message {string}")
    public void iShouldSeeTheLoginErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = loginPage.getErrorMessage();
        log.info("Login error — Expected: '{}' | Actual: '{}'",
                expectedErrorMessage, actualErrorMessage);
        Assertions.assertTrue(actualErrorMessage.contains(expectedErrorMessage),
                "Login error message mismatch — Expected: '" + expectedErrorMessage
                        + "' | Actual: '" + actualErrorMessage + "'");
    }
}