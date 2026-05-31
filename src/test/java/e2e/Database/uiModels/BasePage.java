package e2e.Database.uiModels;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BasePage {
    @Autowired
    protected WebDriver driver;
    @Autowired
    protected WebDriverWait wait;
    @Autowired
    protected JavascriptExecutor javascriptExecutor;

    @PostConstruct
    private void init() {
        if (this.driver != null) {
            PageFactory.initElements(this.driver, this);
        }
    }

    public abstract boolean isAt();


    public <T> void waitElement(T elementAttr) {
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            wait.until(ExpectedConditions.presenceOfElementLocated((By) elementAttr));
        } else {
            wait.until(ExpectedConditions.visibilityOf((WebElement) elementAttr));
        }
    }


    public void jsClick(By by) {
        javascriptExecutor.executeScript("arguments[0].click();", wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
    }


    //Write Text by using JAVA Generics (You can use both By or WebElement)
    public <T> void writeText(T elementAttr, String text) {
        waitElement(elementAttr);
        if (elementAttr
                .getClass()
                .getName()
                .contains("By")) {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy((By) elementAttr));
            driver
                    .findElement((By) elementAttr)
                    .sendKeys(text);
        } else {
            wait.until(ExpectedConditions.visibilityOf((WebElement) elementAttr));
            ((WebElement) elementAttr).sendKeys(text);
        }
    }

    // Private helper — resolves By or WebElement to a WebElement
    private <T> WebElement getElement(T elementAttr) {
        if (elementAttr.getClass().getName().contains("By")) {
            return driver.findElement((By) elementAttr);
        } else {
            return (WebElement) elementAttr;
        }
    }

    //Read Text by using JAVA Generics (You can use both By or WebElement)
    public <T> String readText(T elementAttr) {
        return getElement(elementAttr).getText();
    }

    public <T> void selectByText(T elementAttr, String optionText) {
        waitElement(elementAttr);
        new Select(getElement(elementAttr)).selectByVisibleText(optionText);
    }

    public <T> void selectByValue(T elementAttr, String value) {
        waitElement(elementAttr);
        new Select(getElement(elementAttr)).selectByValue(value);
    }

    public <T> void selectByIndex(T elementAttr, int index) {
        waitElement(elementAttr);
        new Select(getElement(elementAttr)).selectByIndex(index);
    }


    // Check a checkbox or select a radio button
    public <T> void check(T elementAttr) {
        waitElement(elementAttr);
        WebElement element = getElement(elementAttr);
        if (!element.isSelected()) {   // only click if not already checked
            element.click();
        }
    }

    // Uncheck a checkbox
    public <T> void uncheck(T elementAttr) {
        waitElement(elementAttr);
        WebElement element = getElement(elementAttr);
        if (element.isSelected()) {    // only click if currently checked
            element.click();
        }
    }

    // Check if checkbox/radio is selected
    public <T> boolean isChecked(T elementAttr) {
        waitElement(elementAttr);
        return getElement(elementAttr).isSelected();
    }


}