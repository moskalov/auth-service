package lv.redsails.authservice.jwt.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class JwtTokenPayload {
    String email;
    String baseUrl;
    List<String> roles;
}
