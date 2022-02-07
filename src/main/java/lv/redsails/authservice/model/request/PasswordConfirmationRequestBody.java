package lv.redsails.authservice.model.request;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class PasswordChangeRequestBody {

    @NotBlank
    private String confirmationToken;

    @NotBlank
    private String password;

}
