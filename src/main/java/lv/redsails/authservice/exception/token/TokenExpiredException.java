package lv.redsails.authservice.exception;

public class ConfirmationTokenException extends RuntimeException {

    public ConfirmationTokenException(String explanation) {
        super(explanation);
    }

    public ConfirmationTokenException() {
        super();
    }

}
