package lv.redsails.authservice.controller;


import lv.redsails.authservice.exception.UserAlreadyExistException;
import lv.redsails.authservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserAlreadyExistException.class)
    public ErrorResponse userAlreadyExist(UserAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse errorResponse(RuntimeException e) {
        e.printStackTrace();
        return new ErrorResponse("Something goes wrong");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse notFound(RuntimeException e) {
        e.printStackTrace();
        return new ErrorResponse("NOT FOUND");
    }

    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception ex) {
        return new ErrorResponse("Request payload handling error");
    }

}
