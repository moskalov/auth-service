package lv.redsails.authservice.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lv.redsails.authservice.email.EmailOptions;

import java.util.HashMap;


public class EmailProperties {

    @JsonProperty("password_reset")
    private HashMap<String, String> passwordReset;

    @JsonProperty("email_confirmation")
    private HashMap<String, String> emailConfirmation;

    @JsonIgnore
    public EmailOptions getEmailConfirmationOptions() {
        return getOptions(emailConfirmation);
    }

    @JsonIgnore
    public EmailOptions getPasswordResetOptions() {
        return getOptions(passwordReset);
    }

    @JsonIgnore
    private EmailOptions getOptions(HashMap<String, String> options) {
        return new EmailOptions()
                .setTemplatePath(options.get("html_template_path"))
                .setSubject(options.get("subject"))
                .setFrom(options.get("from"));
    }

}
