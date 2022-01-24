package lv.redsails.authservice.security.filter.authentication;

import lv.redsails.authservice.model.request.AuthenticationRequestBody;
import lv.redsails.authservice.jwt.JwtTokensManager;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import lv.redsails.authservice.maps.JwtTokenMap;
import lv.redsails.authservice.maps.impl.JwtTokenMapImpl;
import lv.redsails.authservice.security.model.UserDetails;
import lv.redsails.authservice.security.authentication.EmailPasswordAuthenticationToken;
import lv.redsails.authservice.security.utils.WebUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.FilterChain;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokensManager jwtTokensManager;
    private final JwtTokenMap mapper = new JwtTokenMapImpl();

    public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokensManager jwtTokensManager) {
        super(authenticationManager);
        this.jwtTokensManager = jwtTokensManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        Optional<Object> bodyOptional = WebUtils.extractBody(request, AuthenticationRequestBody.class);
        AuthenticationRequestBody credentials = (AuthenticationRequestBody) bodyOptional.orElseThrow(() -> new AuthenticationCredentialsNotFoundException(""));

        EmailPasswordAuthenticationToken authToken = new EmailPasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        return this.getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authentication) throws IOException {

        String url = extractBaseUrl(request);
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        JwtTokenPayload payload = mapper.map(userDetails, url);

        String accessToken = jwtTokensManager.generateAccessToken(payload);
        String refreshToken = jwtTokensManager.generateRefreshToken(payload);

        putRefreshTokenInCookie(response, refreshToken);
        WebUtils.putJsonInPayload(response, Map.of("access", accessToken));
    }

    private String extractBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request).replacePath(null)
                .toUriString();
    }

    private void putRefreshTokenInCookie(HttpServletResponse response, String refresh) {
        long expireTimeMs = jwtTokensManager.getRefreshLifetimeMs() / 1000;
        Cookie cookie = new Cookie("refresh", refresh);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge((int) expireTimeMs);
        response.addCookie(cookie);
    }

}
