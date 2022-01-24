package lv.redsails.authservice.jwt;


import lv.redsails.authservice.jwt.model.JwtTokenPayload;

public interface JwtTokensManager {

    String generateAccessToken(JwtTokenPayload payload);

    String generateRefreshToken(JwtTokenPayload payload);

    JwtTokenPayload checkAccessToken(String token);

    JwtTokenPayload checkRefreshToken(String token);

    Long getRefreshLifetimeMs();

    Long getAccessLifetimeMs();

}
