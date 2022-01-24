package lv.redsails.authservice.properties;

import lombok.Data;
import lv.redsails.authservice.jwt.model.TokenProperties;

@Data
public class JwtTokenProperties {
    private TokenProperties access;
    private TokenProperties refresh;
}
