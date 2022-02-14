package lv.redsails.authservice.service.impl;

import com.github.javafaker.Faker;
import lv.redsails.authservice.domain.ConfirmationToken;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.exception.token.TokenNotValidException;
import lv.redsails.authservice.repository.ConfirmationTokenRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.DisabledException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    Faker faker = new Faker();

    @Mock JavaMailSender mailSender;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock ConfirmationTokenRepository tokenRepository;

    @InjectMocks
    AccountServiceImpl accountService;

    @Captor ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldActivateUserAfterEmailConfirmation() {
        ConfirmationToken token = generateRandomConfirmationToken();
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        accountService.confirmEmailByToken(token.getToken());
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isEnabled());
    }

    @Test
    void shouldThrowExceptionWhileEmailConfirmationIfTokenAlreadyUsed() {
        ConfirmationToken token = generateRandomConfirmationToken();
        token.setConfirmedAt(LocalDateTime.now());

        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        Exception exception = assertThrows(Exception.class, () -> {
            String tokenValue = token.getToken();
            accountService.confirmEmailByToken(tokenValue);
        });

        assertEquals(exception.getClass(), TokenNotValidException.class);
        assertEquals("TOKEN_USED", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhileEmailConfirmationIfTokenExpired() {
        ConfirmationToken token = generateRandomConfirmationToken();
        token.setExpiresAt(LocalDateTime.now().minusHours(20));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));

        Exception exception = assertThrows(TokenNotValidException.class, () -> {
            String tokenValue = token.getToken();
            accountService.confirmEmailByToken(tokenValue);
        });

        assertEquals(exception.getClass(), TokenNotValidException.class);
        assertEquals("TOKEN_EXPIRED", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhileEmailConfirmationIfTokenNotExist() {
        String notExistedToken = UUID.randomUUID().toString();
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> {
            accountService.confirmEmailByToken(notExistedToken);
        });

        assertEquals(exception.getClass(), TokenNotValidException.class);
    }


    @Test
    void shouldChangeUserPasswordWhilePasswordReset() {
        ConfirmationToken token = generateRandomConfirmationToken();
        String passwordBefore = token.getUser().getPassword();

        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded_password");

        accountService.confirmPasswordReset("new_password", token.getToken());
        verify(userRepository).save(userCaptor.capture());

        String passwordAfter = userCaptor.getValue().getPassword();
        assertNotEquals(passwordBefore, passwordAfter);
        assertNotNull(passwordAfter);
    }

    @Test
    void shouldSaveEncodedPasswordWhilePasswordReset() {
        ConfirmationToken token = generateRandomConfirmationToken();
        String newRawPassword = faker.pokemon().name();

        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");

        accountService.confirmPasswordReset(newRawPassword, token.getToken());
        verify(userRepository).save(userCaptor.capture());

        String passwordBeforeSave = userCaptor.getValue().getPassword();
        assertNotEquals(newRawPassword, passwordBeforeSave);
    }

    @Test
    void shouldThrowExceptionIfTokenExpiredWhilePasswordChange() {
        ConfirmationToken token = generateRandomConfirmationToken();
        token.setExpiresAt(LocalDateTime.now().minusHours(20));

        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        String newRawPassword = faker.pokemon().name();

        Exception exception = assertThrows(Exception.class, () -> {
            accountService.confirmPasswordReset(newRawPassword, token.getToken());
        });

        assertEquals(TokenNotValidException.class, exception.getClass());
    }

    @Test
    void shouldThrowExceptionIfAccountIsNotActivatedWhilePasswordChange() {
        User user = generateRandomUser().setEnabled(false);
        String fakeEmail = faker.internet().emailAddress();

        when(userRepository.findByEmail(fakeEmail)).thenReturn(Optional.of(user));
        Exception exception = assertThrows(Exception.class, () -> {
            accountService.sendPasswordResetEmail(fakeEmail, "");
        });

        assertEquals(DisabledException.class, exception.getClass());
        assertEquals(exception.getMessage(), "USER_INACTIVE");
    }

    @Test
    void shouldThrowExceptionWhilePasswordResetIfTokenNotFound() {
        ConfirmationToken token = generateRandomConfirmationToken();
        token.setExpiresAt(LocalDateTime.now().minusHours(20));

        when(tokenRepository.findByToken(token.getToken())).thenReturn(Optional.of(token));
        String newRawPassword = faker.pokemon().name();

        Exception exception = assertThrows(Exception.class, () ->
                accountService.confirmPasswordReset(newRawPassword, token.getToken()));

        assertEquals(TokenNotValidException.class, exception.getClass());
    }

    private ConfirmationToken generateRandomConfirmationToken() {
        String token = UUID.randomUUID().toString();
        return new ConfirmationToken()
                .setExpiresAt(LocalDateTime.now().plusHours(24))
                .setCreatedAt(LocalDateTime.now())
                .setUser(generateRandomUser())
                .setToken(token)
                .setId(1L);
    }

    private User generateRandomUser() {
        Faker faker = new Faker();
        return new User().setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password())
                .setUid(faker.random().toString())
                .setId(1L);
    }
}