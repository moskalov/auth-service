package lv.redsails.authservice.controller;


import lv.redsails.authservice.exception.registration.RegistrationException;
import lv.redsails.authservice.exception.token.TokenNotValidException;
import lv.redsails.authservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({RegistrationException.class, TokenNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleRegistration(RegistrationException e) {
        Map<String, Object> response = getResponseMessage(e);
        HttpStatus status = getStatus(e);
        return new ResponseEntity<>(response, status);
    }

    private HttpStatus getStatus(Exception e) {
        return e.getClass()
                .getAnnotation(ResponseStatus.class)
                .code();
    }

    private Map<String, Object> getResponseMessage(Exception e) {
        HashMap<String, Object> response = new HashMap<>();
        Optional.ofNullable(e.getClass().getAnnotation(ResponseStatus.class)).ifPresent(a -> {
            response.put("reason", a.reason());
        });
        return response;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccountStatusException.class})
    public ErrorResponse handleAccountException(Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception ex) {
        return new ErrorResponse("INVALID_PAYLOAD");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse notFound(RuntimeException e) {
        e.printStackTrace();
        return new ErrorResponse("NOT FOUND");
    }


}
