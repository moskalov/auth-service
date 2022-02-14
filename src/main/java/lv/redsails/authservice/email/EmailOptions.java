package lv.redsails.authservice.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailOptions {

    @JsonProperty("html_template_path")
    private String templatePath;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("from")
    private String from;

}
