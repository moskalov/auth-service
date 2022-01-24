package lv.redsails.authservice.service.impl;

import lombok.AllArgsConstructor;
import lv.redsails.authservice.domain.ConfirmationToken;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.email.EmailMessageGenerator;
import lv.redsails.authservice.exception.authentication.ConfirmationTokenExpired;
import lv.redsails.authservice.repository.ConfirmationTokenRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;
import lv.redsails.authservice.service.AccountService;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    private final EmailMessageGenerator messageGenerator = new EmailMessageGenerator();

    @Override
    @Transactional
    public void sendConfirmationEmail(String email, String confirmUrl) {
        ConfirmationToken token = createConfirmationToken();
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        MimeMessage mimeMessage = generateEmailConfirmationMessage(user, token, confirmUrl);
        saveConfirmationTokenToUser(user, token);
        mailSender.send(mimeMessage);
    }

    private MimeMessage generateEmailConfirmationMessage(User user, ConfirmationToken token, String confirmUrl) {
        return messageGenerator.generateEmailConfirmationMessage(mailSender)
                .setClientEmailConfirmUrl(confirmUrl)
                .setRecipientEmail(user.getEmail())
                .setFullName(user.getFullName())
                .setToken(token.getToken())
                .getMessage();
    }

    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        ConfirmationToken token = createConfirmationToken();

        if (user.isEnabled()) {
            saveConfirmationTokenToUser(user, token);
            MimeMessage passwordResetMessage = generatePasswordResetMessage(user, token);
            mailSender.send(passwordResetMessage);
        }
    }

    private MimeMessage generatePasswordResetMessage(User user, ConfirmationToken token) {
        return messageGenerator.generatePasswordResetMessage(mailSender)
                .setRecipientEmail(user.getEmail())
                .setFullName(user.getFullName())
                .getMessage();
    }

    @Override
    @Transactional
    public void confirmEmailByToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository
                .findByToken(token).orElseThrow(EntityNotFoundException::new);

        throwIfTokenExpired(confirmationToken);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        User user = confirmationToken.getUser();
        enableUser(user);
    }

    private void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void confirmPasswordReset(String newPassword, String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository
                .findByToken(token).orElseThrow(EntityNotFoundException::new);

        throwIfTokenExpired(confirmationToken);
        User tokenOwner = confirmationToken.getUser();
        encodePasswordAndReplace(tokenOwner, newPassword);
        userRepository.save(tokenOwner);
    }

    private void encodePasswordAndReplace(User user, String rawNewPassword) {
        String encodedPassword = passwordEncoder.encode(rawNewPassword);
        user.setPassword(encodedPassword);
    }

    private void throwIfTokenExpired(ConfirmationToken token) throws ConfirmationTokenExpired {
        boolean isExpired = token.getExpiresAt().isBefore(LocalDateTime.now());
        if (isExpired) throw new ConfirmationTokenExpired("Token has been expired");
    }

    private void saveConfirmationTokenToUser(User user, ConfirmationToken token) {
        user.addToken(token);
        userRepository.save(user);
    }

    private ConfirmationToken createConfirmationToken() {
        String tokenValue = UUID.randomUUID().toString();
        return new ConfirmationToken()
                .setCreatedAt(LocalDateTime.now())
                .setExpiresAt(LocalDateTime.now().plusHours(24))
                .setToken(tokenValue);
    }

}
