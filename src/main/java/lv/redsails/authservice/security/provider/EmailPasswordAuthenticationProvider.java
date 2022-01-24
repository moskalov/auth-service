package lv.redsails.authservice.security.provider;

import lombok.RequiredArgsConstructor;
import lv.redsails.authservice.domain.User;
import lv.redsails.authservice.maps.JwtTokenMap;
import lv.redsails.authservice.repository.UserRepository;
import lv.redsails.authservice.security.model.CryptPasswordEncoder;
import lv.redsails.authservice.security.model.UserDetails;
import lv.redsails.authservice.security.authentication.EmailPasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final CryptPasswordEncoder passwordEncoderImpl;
    private final JwtTokenMap jwtTokenMap;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String providedPassword = extractProvidedPassword(authentication);
        String providedEmail = authentication.getPrincipal().toString();

        User user = findUserByEmailInDatabase(providedEmail);
        if (!user.isEnabled()) throw new DisabledException("Account is not activated");

        comparePasswords(providedPassword, user.getPassword());
        return createSuccessAuthentication(user);
    }

    private String extractProvidedPassword(Authentication authentication) {
        return Optional.ofNullable(authentication.getCredentials())
                .orElseThrow(() -> new BadCredentialsException("No password provided"))
                .toString();
    }

    private User findUserByEmailInDatabase(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Failed to authenticate since user not found"));
    }

    private void comparePasswords(String originalProvided, String encodedOriginal) {
        if (!passwordEncoderImpl.matches(originalProvided, encodedOriginal)) {
            throw new BadCredentialsException("Failed to authenticate since password does not match stored value");
        }
    }

    private Authentication createSuccessAuthentication(User user) {
        EmailPasswordAuthenticationToken authentication = jwtTokenMap.map(user);
        UserDetails userDetails = new UserDetails()
                .setIsActivated(true)
                .setEmail(user.getEmail())
                .setAuthorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()));

        authentication.setDetails(userDetails);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(EmailPasswordAuthenticationToken.class);
    }

}
