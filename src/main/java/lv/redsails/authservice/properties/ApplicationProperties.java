package lv.redsails.authservice.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApplicationProperties {

    @JsonProperty("app_first_start")
    private Boolean isAppFirstStart;

}
