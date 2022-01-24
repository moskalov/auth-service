package lv.redsails.authservice.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class RefreshAuthenticationToken extends AbstractAuthenticationToken {

    private final String refreshToken;

    public RefreshAuthenticationToken(String refreshToken) {
        super(null);
        this.refreshToken = refreshToken;
    }

    public RefreshAuthenticationToken(String refreshToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return refreshToken;
    }

    @Override
    public Object getCredentials() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }

}
