package lv.redsails.authservice.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CorsProperties {

    @JsonProperty("allowedMethods")
    private List<String> allowMethods;

    @JsonProperty("allowedOrigins")
    List<String> allowedOrigins;

    @JsonProperty("allowedCredentials")
    Boolean allowedCredentials;

    @JsonProperty("allowedHeader")
    List<String> allowedHeader;

}
