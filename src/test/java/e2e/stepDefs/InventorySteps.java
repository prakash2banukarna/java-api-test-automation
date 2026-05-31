package e2e.stepDefs;

import e2e.Database.uiModels.InventoryPage;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@Slf4j
public class InventorySteps {

    @Autowired
    private InventoryPage inventoryPage;


    @When("the inventory page should display {int} products")
    public void inventoryPageCount(int expectedCount) {
        int actualProductCount = inventoryPage.getProductCount();
        log.info("Product count — Expected: {} | Actual: {}", expectedCount, actualProductCount);
        Assert.assertEquals(expectedCount, actualProductCount,
                "Product count mismatch - Expected : " + expectedCount +
                        " Actual Product Count :" + actualProductCount);
    }

    @When("I sort the products by {string}")
    public void iSortProductBy(String sortOption) throws InterruptedException {
        inventoryPage.sortProducts(sortOption);
    }

    @When("the lowest product price is {string}")
    public void iFetchItemPrice(String expectedPrice) {
        String actualLowestPrice = inventoryPage.itemPrice();
        log.info("Lowest Product price found in inventory page is : " + actualLowestPrice);
        Assert.assertEquals(expectedPrice, actualLowestPrice, "Expected price : " + expectedPrice + " but found " + actualLowestPrice);
    }

    @When("the lowest product name is {string}")
    public void iFetchLowestProductName(String expectedProductName) {
        String actualProductName = inventoryPage.productName();
        log.info("Lowest Product name found in inventory page is : " + actualProductName);
        Assert.assertEquals(expectedProductName, actualProductName, "Expected product name : " + expectedProductName
                + " but found : " + actualProductName);
    }
}
