package e2e.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.Map;

@Configuration  //  Spring ignores @Bean methods without this
public class WebDriverConfig {

    @Value("${browser:chrome}")  // defaults to chrome if not set
    private String browser;

    @Value("${headless:false}")
    private boolean headless;

    /**
     * Creates WebDriver bean based on browser property.
     * cucumber-glue scope = new instance per scenario.
     */
    @Bean
    @Scope("cucumber-glue")
    @Primary
    public WebDriver webDriver() {
//        String currentBrowser = System.getProperty("browser", "chrome");
        return switch (browser.toLowerCase()) {
            case "firefox" -> { //Adding the following to remove the noise in terminal --Dwebdriver.firefox.logfile=/dev/null
                WebDriverManager.firefoxdriver().driverVersion("0.36.0").setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setLogLevel(FirefoxDriverLogLevel.FATAL);  // silences geckodriver output
                if (headless) firefoxOptions.addArguments("--headless");
                Proxy proxy = new Proxy();
                proxy.setProxyType(Proxy.ProxyType.MANUAL);
                proxy.setNoProxy("localhost,127.0.0.1");
                firefoxOptions.setCapability("proxy", proxy);
                yield new FirefoxDriver(firefoxOptions);

            }
            case "edge" -> {
                System.setProperty("webdriver.edge.driver", "/usr/local/bin/msedgedriver");
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                edgeOptions.addArguments("--disable-notifications");
                edgeOptions.addArguments("--remote-allow-origins=*");
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-dev-shm-usage");
                edgeOptions.addArguments("--disable-gpu");
                edgeOptions.setBinary("/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge");
                if (headless) edgeOptions.addArguments("--headless=new");
                yield new EdgeDriver(edgeOptions);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--remote-allow-origins=*"); // fixes 403 on WebSocket
                options.addArguments("--log-level=3");        // suppress Chrome logs
                options.addArguments("--silent");
                if (headless) options.addArguments("--headless=new");
                //Disabling chrome password manager
                options.addArguments("--disable-save-password-bubble");
                options.setExperimentalOption("prefs", Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false
                ));

                System.setProperty("webdriver.chrome.silentOutput", "true"); // suppress ChromeDriver stdout
                java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.SEVERE);
                yield new ChromeDriver(options);
            }
        };
    }

    /**
     * WebDriverWait bean — reuses the WebDriver bean.
     */
    @Bean
    @Scope("cucumber-glue")
    public WebDriverWait webDriverWait(WebDriver webDriver) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(15));
    }

    /**
     * JavascriptExecutor bean — cast from WebDriver.
     */
    @Bean
    @Scope("cucumber-glue")
    public JavascriptExecutor javascriptExecutor(WebDriver webDriver) {
        return (JavascriptExecutor) webDriver;
    }
}