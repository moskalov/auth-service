package lv.redsails.authservice.repository;

import lv.redsails.authservice.domain.ConfirmationToken;
import lv.redsails.authservice.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConfirmationTokenRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Autowired
    ConfirmationTokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findByToken() {
        User user = new User()
                .setEmail("qmoskalov.a@gmail.com")
                .setPassword("passowrd")
                .setUid("uid");

        User saved = userRepository.save(user);

        ConfirmationToken token = new ConfirmationToken()
                .setCreatedAt(LocalDateTime.now())
                .setExpiresAt(LocalDateTime.now())
                .setToken("token")
                .setUser(saved);

        tokenRepository.save(token);
    }
}