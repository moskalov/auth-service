package lv.redsails.authservice.config;


import lombok.AllArgsConstructor;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@AllArgsConstructor
public class HibernateConfig {

    private final ExternalPropertiesLoader propertiesLoader;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("developer-01");
        dataSource.setPassword("insertF12MAS");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/auth?createDatabaseIfNotExist=true");
        return dataSource;
    }

}
