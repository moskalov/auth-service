package lv.redsails.authservice.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class RegistrationRequestBody {

    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "message")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String emailConfirmUrl;

}
