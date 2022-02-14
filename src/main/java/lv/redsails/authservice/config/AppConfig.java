package lv.redsails.authservice.config;

import lv.redsails.authservice.email.EmailMessageGenerator;
import lv.redsails.authservice.email.EmailOptions;
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
    public EmailMessageGenerator emailMessageGenerator() {
        ExternalPropertiesLoader loader = getPropertiesLoader();
        EmailProperties emailProperties = loader.readProperty(EmailProperties.class);
        EmailOptions emailConfirmOptions = emailProperties.getEmailConfirmationOptions();
        EmailOptions passwordResetOptions = emailProperties.getPasswordResetOptions();
        return new EmailMessageGenerator(emailConfirmOptions, passwordResetOptions);
    }

    @Bean
    public ExternalPropertiesLoader getPropertiesLoader() {
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
        properties.put(EmailProperties.class, "emails.json");
        loader.registerProperties(properties);
    }

}
