package playgorund;


import com.fasterxml.jackson.databind.ObjectMapper;
import lv.redsails.authservice.jwt.model.TokenProperties;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class Jackson {

    @Test
    public void example() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("./config/jwt-token.json");

        String json = objectMapper.readTree(file).findPath("access").toString();
        TokenProperties tokenProperties = objectMapper.readValue(json, TokenProperties.class);

        System.out.println(tokenProperties);

    }

}
