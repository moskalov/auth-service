package lv.redsails.authservice.security.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.jwt.JwtTokensManager;
import lv.redsails.authservice.jwt.model.JwtTokenPayload;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.*;

@RequiredArgsConstructor
public class TokenAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokensManager jwtTokensManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        System.out.println("TokenAuthorizationFilter");
        boolean isLoginPath = request.getServletPath().equals("/api/v1/client/login");
        if (isLoginPath) filterChain.doFilter(request, response);
        else applyFilter(request, response, filterChain);
    }

    private void applyFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (hasBearerToken(request)) {
            try {
                String token = extractToken(request);
                JwtTokenPayload tokenPayload = jwtTokensManager.checkAccessToken(token);
                UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(tokenPayload);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                String message = e.getClass().equals(TokenExpiredException.class) ? e.getMessage() : "access token could not be verified";
                addErrorToResponse(response, message);
            }
        } else {
            addErrorToResponse(response, "no access token was specified");
            filterChain.doFilter(request, response);
        }
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authorizationHeader.substring("Bearer ".length());
    }

    private void addErrorToResponse(HttpServletResponse response, String errorMessage) throws IOException {
        Map<String, String> error = Map.of("error_message", errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseOutStream = response.getOutputStream();
        new ObjectMapper().writeValue(responseOutStream, error);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(JwtTokenPayload tokenPayload) {
        Collection<SimpleGrantedAuthority> authorities = tokenPayload.getRoles()
                .stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                tokenPayload.getEmail(),
                null,
                authorities
        );
    }

}
