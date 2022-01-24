package lv.redsails.authservice.security.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    CryptPasswordEncoder passwordEncoderImpl = new CryptPasswordEncoder();

    @Test
    void exampleToUnderstand() {
        String string = "my_string";
        String firstEncoding = passwordEncoderImpl.encode(string);
        String secondEncoding = passwordEncoderImpl.encode(string);

        assertEquals(60, firstEncoding.length());
        assertEquals(60, secondEncoding.length());

        boolean isEqualWithFirstEncoding = passwordEncoderImpl.matches(string, secondEncoding);
        boolean isEqualWithSecondEncoding = passwordEncoderImpl.matches(string, secondEncoding);

        assertTrue(isEqualWithFirstEncoding);
        assertTrue(isEqualWithSecondEncoding);

        assertNotEquals(firstEncoding, secondEncoding);
    }

}