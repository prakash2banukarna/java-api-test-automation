package e2e.runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        glue = {"e2e.stepDefs", "e2e.hooks"},
        features = {"src/test/resources/features"},
        plugin = {"pretty",
                "html:target/reports/cucumber.html",
                "rerun:rerun/failed_scenarios.txt"},
        monochrome = true)

public class TestRunner extends AbstractTestNGCucumberTests {

    //@BeforeTest method to read the TestNG parameter and set it as a system property: - To enable parallel cross browser testing

//    @BeforeTest
//    @Parameters("browser")
//    public void setBrowser(String browser) {
//        System.setProperty("browser", browser);
//    }

    @DataProvider(parallel = true)
    @Override
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
