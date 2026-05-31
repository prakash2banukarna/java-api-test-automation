package e2e.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:env-dev.properties")
@PropertySource("classpath:env-${env:dev}.properties") // First read all the property file data

@ComponentScan(basePackages = {
        "e2e.Database.uiModels",
        "e2e.stepDefs",
        "e2e.hooks",
        "e2e.support"
})
public class AppConfiguration {


}
