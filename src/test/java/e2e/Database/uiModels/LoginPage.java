package e2e.Database.uiModels;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class LoginPage extends BasePage {

    // SauceDemo locators
    @FindBy(how = How.ID, using = "user-name")
    public WebElement usernameField;

    @FindBy(how = How.ID, using = "password")
    public WebElement passwordField;

    By loginButtonBy = By.id("login-button");

    By errorMessageBy = By.cssSelector("[data-test='error']");

    public LoginPage login(String username, String password) {
        writeText(this.usernameField, username);
        writeText(this.passwordField, password);
        jsClick(loginButtonBy);
        return this;
    }

    public LoginPage verifyErrorMessage(String expectedText) {
        String actualText = readText(errorMessageBy);
        Assertions.assertTrue(actualText.contains(expectedText),
                "Error message mismatch — Expected: " + expectedText
                        + " Actual: " + actualText);
        return this;
    }

    /**
     * Returns the error message text displayed on failed login.
     * Used by LoginSteps to verify the actual message against expected.
     */
    public String getErrorMessage() {
        return readText(errorMessageBy);
    }
    

    @Override
    public boolean isAt() {
        return this.wait.until((d) -> this.usernameField.isDisplayed());
    }
}