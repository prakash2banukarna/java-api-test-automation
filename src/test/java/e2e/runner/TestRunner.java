package e2e.runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        glue={"e2e.stepDefs","e2e.hooks"},
        features = {"src/test/resources/features"},
        plugin = {"pretty",
        "html:target/reports/cucumber.html"},
        monochrome = true)

public class TestRunner extends AbstractTestNGCucumberTests {

    @DataProvider(parallel = true)
    @Override
    public Object[][] scenarios(){
        return super.scenarios();
    }

}
