package kr.gooroom.gpms.login;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private boolean isOtpEnabled;

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, boolean isOtpEnabled) {
        super(principal, credentials, authorities);
        this.isOtpEnabled = isOtpEnabled;
    }

    public boolean getIsOtpEnabled() {
        return this.isOtpEnabled;
    }
}
