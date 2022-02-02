package lv.redsails.authservice.config;


import lombok.AllArgsConstructor;
import lv.redsails.authservice.properties.DatabaseProperties;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class DatabaseConfig {

    private final ExternalPropertiesLoader propertiesLoader;

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
