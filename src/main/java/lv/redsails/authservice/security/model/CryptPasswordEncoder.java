package lv.redsails.authservice.security.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class CryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean matches(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }

    @Override
    public String encode(String password) {
        return this.encoder.encode(password);
    }

}
