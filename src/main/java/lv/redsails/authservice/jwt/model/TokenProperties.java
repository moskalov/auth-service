package lv.redsails.authservice.jwt.model;
import lombok.Data;

@Data
public class TokenProperties {
    private String secret;
    private Long expirationAfterMs;
    private String prefix;
}
