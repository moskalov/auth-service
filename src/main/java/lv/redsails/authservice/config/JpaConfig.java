package lv.redsails.authservice.config;


import lombok.AllArgsConstructor;
import lv.redsails.authservice.properties.DatabaseProperties;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
public class DatabaseConfig {

    private final ExternalPropertiesLoader propertiesLoader;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        Properties adapterProperties = getHibernateProperties();

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("lv.redsails.authservice.domain");
        em.setJpaProperties(adapterProperties);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setDataSource(dataSource);
        return em;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.ddl-auto", "create");
        properties.setProperty("hibernate.format.sql", "true");
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
