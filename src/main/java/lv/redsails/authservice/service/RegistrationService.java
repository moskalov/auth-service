package lv.redsails.authservice.service;


import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.model.request.RegistrationRequestBody;

public interface RegistrationService {

    User registerUserAccount(RegistrationRequestBody credentials);

}
