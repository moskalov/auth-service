package lv.redsails.authservice.config.model;

import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import lv.redsails.authservice.properties.ApplicationProperties;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

// todo REFACTORING
class ExternalPropertiesLoaderTest {

    private final ExternalPropertiesLoader loader;

    public ExternalPropertiesLoaderTest() {
        String rootPath = getAbsolutePathToTestResources();
        HashMap<Class<?>, String> properties = new HashMap<>();
        properties.put(ApplicationProperties.class, "service.json");
        loader = new ExternalPropertiesLoader(rootPath, properties);
    }

    @Test
    void shouldReadExistConfigurationFile() throws IOException {
        ApplicationProperties configuration = loader.readProperty(ApplicationProperties.class);
        Assertions.assertTrue(configuration.getIsAppFirstStart());
    }

    @Test
    void shouldUpdateExistConfigurationFile() throws IOException {
        ApplicationProperties configuration = new ApplicationProperties();
        configuration.setIsAppFirstStart(true);
        loader.updateProperty(configuration);
    }

    private String getAbsolutePathToTestResources() {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        return resourceDirectory.toFile().getAbsolutePath();
    }

}