package lv.redsails.authservice.service.impl;

import lombok.AllArgsConstructor;
import lv.redsails.authservice.domain.ConfirmationToken;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.email.EmailMessageGenerator;
import lv.redsails.authservice.exception.token.TokenNotValidException;
import lv.redsails.authservice.repository.ConfirmationTokenRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;
import lv.redsails.authservice.service.AccountService;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;

    private final JavaMailSender mailSender;
    private final EmailMessageGenerator messageGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void sendConfirmationEmail(String recipientEmail, String confirmUrl) {
        ConfirmationToken confirmationToken = createConfirmationToken();
        User user = userRepository.findByEmail(recipientEmail).orElseThrow(TokenNotValidException::new);
        MimeMessage mimeMessage = generateEmailConfirmationMessage(user, confirmationToken, confirmUrl);
        saveConfirmationTokenToUser(user, confirmationToken);
        mailSender.send(mimeMessage);
    }

    private MimeMessage generateEmailConfirmationMessage(User user, ConfirmationToken token, String confirmUrl) {
        return messageGenerator.generateEmailConfirmationMessage(mailSender)
                .setClientAppEmailConfirmUrl(confirmUrl)
                .setRecipientEmail(user.getEmail())
                .setFullName(user.getFullName())
                .setToken(token.getToken())
                .getMessage();
    }

    @Override
    @Transactional
    public void confirmEmailByToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository
                .findByToken(token)
                .orElseThrow(TokenNotValidException::new);

        throwIfTokenUsedOrExpired(confirmationToken);
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
    public void sendPasswordResetEmail(String recipientEmail, String clientResetUrl) {
        User user = userRepository.findByEmail(recipientEmail).orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));
        throwIfUserInactive(user);

        ConfirmationToken token = createConfirmationToken();
        saveConfirmationTokenToUser(user, token);
        MimeMessage passwordResetMessage = generatePasswordResetMessage(user, token, clientResetUrl);
        mailSender.send(passwordResetMessage);
    }

    private void throwIfUserInactive(User user) {
        if (!user.isEnabled()) throw new DisabledException("USER_INACTIVE");
    }

    private MimeMessage generatePasswordResetMessage(User user, ConfirmationToken token, String clientResetPageUrl) {
        return messageGenerator.generatePasswordResetMessage(mailSender)
                .setPasswordResetUrl(clientResetPageUrl)
                .setRecipientEmail(user.getEmail())
                .setFullName(user.getFullName())
                .setToken(token.getToken())
                .getMessage();
    }

    @Override
    @Transactional
    public void confirmPasswordReset(String newPassword, String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(TokenNotValidException::new);
        throwIfTokenUsedOrExpired(confirmationToken);

        User tokenOwner = confirmationToken.getUser();
        encodePasswordAndReplace(tokenOwner, newPassword);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        userRepository.save(tokenOwner);
    }

    private void throwIfTokenUsedOrExpired(ConfirmationToken token) {
        boolean isExpired = token.getExpiresAt().isBefore(LocalDateTime.now());
        boolean isUsed = Optional.ofNullable(token.getConfirmedAt()).isPresent();
        if (isUsed || isExpired) throw new TokenNotValidException();
    }

    private ConfirmationToken createConfirmationToken() {
        String tokenValue = UUID.randomUUID().toString();
        return new ConfirmationToken()
                .setCreatedAt(LocalDateTime.now())
                .setExpiresAt(LocalDateTime.now().plusHours(24))
                .setToken(tokenValue);
    }

    private void encodePasswordAndReplace(User user, String rawNewPassword) {
        String encodedPassword = passwordEncoder.encode(rawNewPassword);
        user.setPassword(encodedPassword);
    }

    private void saveConfirmationTokenToUser(User user, ConfirmationToken token) {
        user.addToken(token);
        userRepository.save(user);
    }

}
