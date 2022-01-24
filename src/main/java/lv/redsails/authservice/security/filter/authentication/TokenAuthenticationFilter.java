package lv.redsails.authservice.security.filter.authentication;

import lv.redsails.authservice.jwt.JwtTokensManager;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import lv.redsails.authservice.security.authentication.RefreshAuthenticationToken;
import lv.redsails.authservice.security.utils.WebUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;


public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtTokensManager jwtTokensManager;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokensManager jwtTokensManager) {
        super("/login", authenticationManager);
        this.jwtTokensManager = jwtTokensManager;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean hasUrlMatch = super.requiresAuthentication(request, response);
        boolean hasRefreshToken = WebUtils.extractCookieValue(request, "refresh").isPresent();
        return hasUrlMatch && hasRefreshToken;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        Optional<String> refreshTokenOptional = WebUtils.extractCookieValue(request, "refresh");
        String token = refreshTokenOptional.orElseThrow(() -> new AuthenticationCredentialsNotFoundException("refresh token not found"));
        RefreshAuthenticationToken refreshToken = new RefreshAuthenticationToken(token);
        return this.getAuthenticationManager().authenticate(refreshToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {

        JwtTokenPayload refreshTokenPayload = (JwtTokenPayload) authResult.getDetails();
        String newAccessToken = jwtTokensManager.generateAccessToken(refreshTokenPayload);
        WebUtils.putJsonInPayload(response, Map.of("access", newAccessToken));
    }

}


