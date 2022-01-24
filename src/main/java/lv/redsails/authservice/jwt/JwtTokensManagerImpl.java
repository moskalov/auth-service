package lv.redsails.authservice.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lv.redsails.authservice.properties.ExternalPropertiesLoader;
import lv.redsails.authservice.properties.JwtTokenProperties;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import lv.redsails.authservice.jwt.model.TokenProperties;
import lv.redsails.authservice.maps.JwtTokenMap;
import lv.redsails.authservice.maps.impl.JwtTokenMapImpl;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokensManagerImpl implements JwtTokensManager {
    private final JwtTokenMap tokenMap = new JwtTokenMapImpl();

    private final TokenProperties accessToken;
    private final TokenProperties refreshToken;

    public JwtTokensManagerImpl(ExternalPropertiesLoader loader) {
        JwtTokenProperties configuration = loader.readProperty(JwtTokenProperties.class);
        accessToken = configuration.getAccess();
        refreshToken = configuration.getRefresh();
    }

    @Override
    public String generateAccessToken(JwtTokenPayload payload) {
        Date expTime = new Date(System.currentTimeMillis() + accessToken.getExpirationAfterMs());
        return generateToken(payload, expTime, accessToken.getSecret());
    }

    @Override
    public String generateRefreshToken(JwtTokenPayload payload) {
        Date expTime = new Date(System.currentTimeMillis() + refreshToken.getExpirationAfterMs());
        return generateToken(payload, expTime, refreshToken.getSecret());
    }

    private String generateToken(JwtTokenPayload tokenPayload, Date expTime, String secret) {
        Algorithm secretAlgorithm = Algorithm.HMAC512(secret);
        return JWT.create().withSubject(tokenPayload.getBaseUrl())
                .withIssuer(tokenPayload.getEmail())
                .withClaim("roles", tokenPayload.getRoles())
                .withExpiresAt(expTime)
                .sign(secretAlgorithm);
    }

    @Override
    public JwtTokenPayload checkAccessToken(String token) {
        Algorithm secretAlgorithm = Algorithm.HMAC512(this.accessToken.getSecret());
        JWTVerifier jwtVerifier = JWT.require(secretAlgorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return tokenMap.map(decodedJWT);
    }

    @Override
    public JwtTokenPayload checkRefreshToken(String token) {
        Algorithm refreshSecret = Algorithm.HMAC512(this.refreshToken.getSecret());
        JWTVerifier jwtVerifier = JWT.require(refreshSecret).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return tokenMap.map(decodedJWT);
    }

    @Override
    public Long getRefreshLifetimeMs() {
        return refreshToken.getExpirationAfterMs();
    }

    @Override
    public Long getAccessLifetimeMs() {
        return accessToken.getExpirationAfterMs();
    }

}

