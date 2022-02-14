package lv.redsails.authservice.config;


import lombok.AllArgsConstructor;
import lv.redsails.authservice.properties.ApplicationProperties;
import lv.redsails.authservice.properties.DatabaseProperties;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@AllArgsConstructor
public class JpaConfig {

    private final ExternalPropertiesLoader propertiesLoader;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        Properties adapterProperties = getDefaultHibernateProperties();

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("lv.redsails.authservice.domain");
        em.setJpaProperties(adapterProperties);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setDataSource(dataSource);
        return em;
    }

    private Properties getDefaultHibernateProperties() {
        ApplicationProperties appProperties = propertiesLoader.readProperty(ApplicationProperties.class);
        boolean isAppFirstStart = appProperties.getIsAppFirstStart();
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", isAppFirstStart ? "create" : "none");
        properties.setProperty("hibernate.show_sql", "true");
        return properties;
    }

    @Bean
    public DataSource dataSource() {
        DatabaseProperties databaseProperties = propertiesLoader.readProperty(DatabaseProperties.class);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername(databaseProperties.getUserName());
        dataSource.setPassword(databaseProperties.getPassword());
        dataSource.setUrl("jdbc:" + databaseProperties.getUrl());
        return dataSource;
    }

}
