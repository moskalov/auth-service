package lv.redsails.authservice.exception.registration;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "This email is already in use")
public class EmailInUseException extends RegistrationException {

}
