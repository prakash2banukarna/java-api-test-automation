package e2e.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:env-dev.properties")
@PropertySource("classpath:env-${env:dev}.properties") // First read all the property file data
@ComponentScan({"e2e.support"})
public class AppConfiguration {

    @Value("${userName}")
    private String dbUserName;

    @Value("${operation}")
    private String operation;

    // new DB config
    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    /**
     * Creates PostgreSQL connection.
     * Spring manages this as a singleton bean.
     */


}
