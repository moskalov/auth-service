package lv.redsails.authservice.config;

import lv.redsails.authservice.properties.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.HashMap;

@Configuration
public class AppConfig {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    @Bean
    public ExternalPropertiesLoader configurationLoader() {
        ExternalPropertiesLoader loader = new ExternalPropertiesLoader("./configuration");
        registeredProperties(loader);
        return loader;
    }

    private void registeredProperties(ExternalPropertiesLoader loader) {
        HashMap<Class<?>, String> properties = new HashMap<>();
        properties.put(ApplicationProperties.class, "application.json");
        properties.put(CorsProperties.class, "cors.json");
        properties.put(JwtTokenProperties.class, "jwt-token.json");
        properties.put(DatabaseProperties.class, "database.json");
        loader.registerProperties(properties);
    }

}
