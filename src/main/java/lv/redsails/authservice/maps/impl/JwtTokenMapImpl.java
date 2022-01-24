package lv.redsails.authservice.maps.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import lv.redsails.authservice.maps.JwtTokenMap;
import lv.redsails.authservice.security.model.UserDetails;
import lv.redsails.authservice.security.authentication.EmailPasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenMapImpl implements JwtTokenMap {

    public JwtTokenPayload map(UserDetails user, String baseUrl) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtTokenPayload()
                .setBaseUrl(user.getEmail())
                .setEmail(baseUrl)
                .setRoles(roles);
    }

    @Override
    public JwtTokenPayload map(DecodedJWT decodedJWT) {
        List<String> roles = getRoles(decodedJWT);
        return new JwtTokenPayload()
                .setEmail(decodedJWT.getSubject())
                .setBaseUrl(decodedJWT.getIssuer())
                .setRoles(roles);
    }

    private List<String> getRoles(DecodedJWT decodedJWT) {
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        return Arrays.stream(roles).collect(Collectors.toList());
    }

    public EmailPasswordAuthenticationToken map(User user) {
        List<SimpleGrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new EmailPasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                grantedAuthorities
        );
    }


}
