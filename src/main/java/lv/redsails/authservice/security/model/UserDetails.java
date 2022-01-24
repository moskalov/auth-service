package lv.redsails.authservice.security.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@Data
@Accessors(chain = true)
public class UserDetails implements Serializable {
    private String email;
    private String password;
    private Boolean isActivated;
    private Collection<? extends GrantedAuthority> authorities;
}
