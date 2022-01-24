package lv.redsails.authservice.service;

public interface AccountService {

    void sendConfirmationEmail(String userEmail, String clientConfirmUrl);

    void confirmEmailByToken(String token);

    void sendPasswordResetEmail(String email);

    void confirmPasswordReset(String newPassword, String confirmationToken);

}
