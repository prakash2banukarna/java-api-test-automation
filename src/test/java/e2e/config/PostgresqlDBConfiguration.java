package e2e.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "sssEntityManagerFactory",
        transactionManagerRef = "sssTransactionManager",
        basePackages = {"e2e.Database.repository"}
)
public class PostgresqlDBConfiguration {

    @Value("${DB_USERNAME_POSTGRES}")
    private String dbUsername;

    @Value("${DB_PASSWORD_POSTGRES}")
    private String dbPassword;

//    @Value("${DB_HOST_POSTGRES}")
//    private String dbHost;

    @Value("${DB_URL}")
    private String dbUrl;
//    private String dbUrl() {
//        return String.join("/", "jdbc:postgresql://" + dbHost + ":" + 5432, "aux");
//    }
    ;

    @Value("${sss.datasource.pool.maximum-size:10}")
    private int maximumPoolSize;

    @Value("${sss.datasource.pool.minimum-idle:2}")
    private int minimumIdle;

    @Value("${sss.datasource.pool.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${sss.datasource.pool.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${sss.datasource.pool.max-lifetime:1800000}")
    private long maxLifetime;

    @Value("${sss.jpa.hibernate.ddl-auto:none}")
    private String ddlAuto;

    @Value("${sss.jpa.show-sql:false}")
    private boolean showSql;

    @Value("${sss.jpa.format-sql:true}")
    private boolean formatSql;


    @Bean(name = "sssDataSource")
    public DataSource sssDataSource() {
        HikariConfig config = new HikariConfig();

        // Database connection settings
// config.setJdbcUrl(sssDBSecrets.get("dbUrl"));
// config.setUsername(sssDBSecrets.get("username"));
// config.setPassword(sssDBSecrets.get("password"));
// config.setDriverClassName("org.postgresql.Driver");


        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");


        // Connection pool settings
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setAutoCommit(false);

        // Pool name for monitoring
        config.setPoolName("SSS-HikariPool");

        // Connection test query
        config.setConnectionTestQuery("SELECT 1");

        // Leak detection threshold (10 seconds)
        config.setLeakDetectionThreshold(10000);

        // Additional PostgreSQL optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Bean(name = "sssEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sssEntityManagerFactory(
            @Qualifier("sssDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("e2e.Database.models");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(showSql);
        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaPropertyMap(jpaProperties());

        return em;
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> properties = new HashMap<>();

        // Schema generation
        properties.put("hibernate.hbm2ddl.auto", ddlAuto);

        // SQL logging
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", formatSql);

        // Performance optimizations
        properties.put("hibernate.jdbc.batch_size", 20);
        properties.put("hibernate.order_inserts", true);
        properties.put("hibernate.order_updates", true);
        properties.put("hibernate.jdbc.batch_versioned_data", true);

        // Connection handling
        properties.put("hibernate.connection.provider_disables_autocommit", true);

        // Statistics (enable for development/troubleshooting)
        properties.put("hibernate.generate_statistics", false);

        // Naming strategy
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return properties;
    }

    @Primary
    @Bean(name = "sssTransactionManager")
    public PlatformTransactionManager sssTransactionManager(
            @Qualifier("sssEntityManagerFactory") EntityManagerFactory sssEntityManagerFactory) {
        return new JpaTransactionManager(sssEntityManagerFactory);
    }
}