package lv.redsails.authservice.security.model;

public interface PasswordEncoder {

    boolean matches(String raw, String encoded);

    String encode(String password);

}
