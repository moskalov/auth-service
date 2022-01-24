package lv.redsails.authservice.model.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthenticationRequestBody {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
