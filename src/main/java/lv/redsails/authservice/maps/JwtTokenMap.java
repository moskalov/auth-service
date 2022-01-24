package lv.redsails.authservice.maps;

import com.auth0.jwt.interfaces.DecodedJWT;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;

import lv.redsails.authservice.security.model.UserDetails;
import lv.redsails.authservice.security.authentication.EmailPasswordAuthenticationToken;


public interface JwtTokenMap {

    JwtTokenPayload map(UserDetails user, String baseUrl);

    JwtTokenPayload map(DecodedJWT decodedJWT);

    EmailPasswordAuthenticationToken map(User user);

}
