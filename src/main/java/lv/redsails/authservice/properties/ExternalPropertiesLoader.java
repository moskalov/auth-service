package lv.redsails.authservice.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ExternalPropertiesLoader {

    private final Path configFolder;
    private HashMap<Class<?>, String> properties = new HashMap<>();

    public ExternalPropertiesLoader(String rootFolder, HashMap<Class<?>, String> properties) {
        this.configFolder = Path.of(rootFolder);
        this.properties = properties;
    }

    public ExternalPropertiesLoader(String rootFolder) {
        this.configFolder = Path.of(rootFolder);
    }

    @SneakyThrows
    public <T> T readProperty(Class<?> property) {
        String fileName = properties.get(property);
        String json = Files.readString(configFolder.resolve(fileName));
        return new ObjectMapper()
                .readerFor(property)
                .readValue(json);
    }

    public <T> void updateProperty(T updatedConfig) throws IOException {
        String fileName = properties.get(updatedConfig.getClass());
        Path path = configFolder.resolve(fileName);
        new ObjectMapper().writeValue(path.toFile(), updatedConfig);
    }

    public void registerProperties(HashMap<Class<?>, String> properties) {
        this.properties.putAll(properties);
    }

    public void registerProperty(Class<?> property, String fileName) {
        this.properties.put(property, fileName);
    }

}

