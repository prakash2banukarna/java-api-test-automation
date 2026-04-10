package e2e.Database.uiModels;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class HomePage extends BasePage {

    @Value("${application.url}")
    private String baseURL;

    @FindBy(how = How.ID, using = "login-button")
    public WebElement loginButton;

    public HomePage goToHomePage() {
        driver.get(baseURL);
        return this;
    }

    // SauceDemo login page IS the home page — no separate signIn click needed
    public HomePage goToLoginPage() {
        return this;
    }

    @Override
    public boolean isAt() {
        return this.wait.until((d) -> this.loginButton.isDisplayed());
    }
}