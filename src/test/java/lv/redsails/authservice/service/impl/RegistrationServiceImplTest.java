package lv.redsails.authservice.service.impl;

import com.github.javafaker.Faker;
import lv.redsails.authservice.domain.ConfirmationToken;
import lv.redsails.authservice.domain.Role;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.model.request.RegistrationRequestBody;
import lv.redsails.authservice.event.RegistrationCompleteEvent;
import lv.redsails.authservice.exception.registration.EmailUsedException;
import lv.redsails.authservice.repository.RoleRepository;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.PasswordEncoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ApplicationEventPublisher publisher;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private RegistrationServiceImpl registrationService;

    @Captor ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        when(roleRepository.findByName("CLIENT")).thenReturn(new Role(1L, "CLIENT"));
    }

    @Test
    void shouldEncodePasswordBeforeSaveInDatabase() {
        RegistrationRequestBody registrationRequestBody = getRandomRegistrationRequest();
        when(roleRepository.findByName("CLIENT")).thenReturn(new Role(1L, "CLIENT"));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        registrationService.registerUserAccount(registrationRequestBody);
        verify(userRepository).save(userCaptor.capture());

        String rawPassword = registrationRequestBody.getPassword();
        String passwordBeforeSave = userCaptor.getValue().getPassword();
        assertNotEquals(rawPassword, passwordBeforeSave);
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExist() {
        RegistrationRequestBody regRequestBody = getRandomRegistrationRequest();
        when(userRepository.findByEmail(regRequestBody.getEmail())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(EmailUsedException.class, () -> {
            registrationService.registerUserAccount(regRequestBody);
        });

        String expectedMessage = "Email already exist";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldPublishRegistrationCompleteEventAfterUserSave() {
        RegistrationRequestBody requestBody = getRandomRegistrationRequest();

        registrationService.registerUserAccount(requestBody);
        verify(publisher, times(1)).publishEvent(ArgumentMatchers.any(RegistrationCompleteEvent.class));
    }

    @Test
    void shouldSaveUserWithEmailConfirmationToken() {
        RegistrationRequestBody requestBody = getRandomRegistrationRequest();
        registrationService.registerUserAccount(requestBody);

        verify(userRepository, times(1)).save(userCaptor.capture());
        List<ConfirmationToken> confirmationTokenList = userCaptor.getValue().getTokens();
        ConfirmationToken token = confirmationTokenList.get(0);

        assertNotEquals("", token.getToken());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiresAt());
    }

    private RegistrationRequestBody getRandomRegistrationRequest() {
        Faker faker = new Faker();
        return new RegistrationRequestBody()
                .setEmail(faker.internet().safeEmailAddress())
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setPassword(faker.animal().name());
    }

}