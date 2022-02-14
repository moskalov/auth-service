package lv.redsails.authservice.exception.token;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "INVALID_TOKEN")
public class TokenNotValidException extends RuntimeException {

}
