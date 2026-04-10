package e2e.Database.uiModels;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page Object for SauceDemo inventory page.
 * Represents the product listing page shown after successful login.
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class InventoryPage extends BasePage {

    @FindBy(how = How.CLASS_NAME, using = "title")
    private WebElement pageTitle;

    @FindBy(how = How.CLASS_NAME, using = "inventory_item")
    private java.util.List<WebElement> productItems;

    /**
     * Returns the page title text — "Products" on successful login.
     */
    public String getTitle() {
        return readText(pageTitle);
    }

    /**
     * Returns current URL — used to verify redirect after login.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Checks whether the current URL contains "inventory".
     */
    public boolean isOnInventoryPage() {
        boolean onPage = driver.getCurrentUrl().contains("inventory");
        log.info("Inventory page URL check: {} — on page: {}",
                driver.getCurrentUrl(), onPage);
        return onPage;
    }

    /**
     * Returns the number of products displayed on the inventory page.
     */
    public int getProductCount() {
        int count = productItems.size();
        log.info("Product count on inventory page: {}", count);
        return count;
    }

    @Override
    public boolean isAt() {
        return this.wait.until((d) -> this.pageTitle.isDisplayed());
    }
}