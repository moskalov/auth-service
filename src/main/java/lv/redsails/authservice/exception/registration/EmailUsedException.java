package lv.redsails.authservice.exception.registration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "EMAIL_EXIST")
public class EmailUsedException extends RegistrationException {

}
