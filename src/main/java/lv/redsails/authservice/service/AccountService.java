package lv.redsails.authservice.service;

public interface AccountService {

    void sendConfirmationEmail(String recipientEmail, String clientConfirmUrl);

    void confirmEmailByToken(String confirmationToken);

    void sendPasswordResetEmail(String recipientEmail, String clientResetUrl);

    void confirmPasswordReset(String confirmationToken, String newPassword);

}
