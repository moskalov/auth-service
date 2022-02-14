package lv.redsails.authservice.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetRequestBody {

    @NotBlank
    private String token;

    @NotBlank
    private String password;

}
