package lv.redsails.authservice.exception.authentication;

import javax.naming.AuthenticationException;

public class ConfirmationTokenExpired extends RuntimeException {
    public ConfirmationTokenExpired(String explanation) {
        super(explanation);
    }

    public ConfirmationTokenExpired() {
        super();
    }
}
