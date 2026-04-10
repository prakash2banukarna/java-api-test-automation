package e2e.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

@Configuration  //  Spring ignores @Bean methods without this
public class WebDriverConfig {

    @Value("${browser:chrome}")  // defaults to chrome if not set
    private String browser;

    /**
     * Creates WebDriver bean based on browser property.
     * cucumber-glue scope = new instance per scenario.
     */
    @Bean
    @Scope("cucumber-glue")
    @Primary
    public WebDriver webDriver() {
        return switch (browser.toLowerCase()) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                Proxy proxy = new Proxy();
                proxy.setAutodetect(false);
                proxy.setNoProxy("localhost,127.0.0.1");  // was "no_proxy-var" — fix this
                proxy.setNoProxy("no_proxy-var");
                firefoxOptions.setCapability("proxy", proxy);
                yield new FirefoxDriver(firefoxOptions);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver();
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--remote-allow-origins=*"); // fixes 403 on WebSocket
                options.addArguments("--log-level=3");        // suppress Chrome logs
                options.addArguments("--silent");
                System.setProperty("webdriver.chrome.silentOutput", "true"); // suppress ChromeDriver stdout
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