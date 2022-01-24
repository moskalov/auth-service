package lv.redsails.authservice.security.provider;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import lv.redsails.authservice.jwt.JwtTokensManager;
import lv.redsails.authservice.security.authentication.RefreshAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokensManager tokensManager;

    public RefreshTokenAuthenticationProvider(JwtTokensManager tokensManager) {
        this.tokensManager = tokensManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RefreshAuthenticationToken refreshToken = (RefreshAuthenticationToken) authentication;
        JwtTokenPayload refreshTokenPayload = checkRefreshToken(refreshToken.toString());
        return createSuccessfulAuthentication(refreshToken.toString(), refreshTokenPayload);
    }

    private RefreshAuthenticationToken createSuccessfulAuthentication(String refreshToken, JwtTokenPayload payload) {
        RefreshAuthenticationToken successAuthentication = new RefreshAuthenticationToken(refreshToken);
        successAuthentication.setDetails(payload);
        return successAuthentication;
    }

    private JwtTokenPayload checkRefreshToken(String refreshToken) {
        try {
            return tokensManager.checkRefreshToken(refreshToken);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RefreshAuthenticationToken.class);
    }

}
