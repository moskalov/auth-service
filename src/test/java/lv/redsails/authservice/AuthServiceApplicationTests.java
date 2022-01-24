package lv.redsails.authservice;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        String sha256hex = DigestUtils.md5Hex("qmoskalov.a@gmail.com");
        System.out.println(sha256hex);

    }

}
