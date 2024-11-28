package kr.gooroom.gpms.totp.Service;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OtpAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Integer otpCode;

    public OtpAuthenticationToken(Integer otpCode) {
        super(null, null);
        this.otpCode = otpCode;
        super.setAuthenticated(false);
    }

    public OtpAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Integer otpCode) {
        super(principal, credentials, authorities);
        this.otpCode = otpCode;
    }

    public Integer getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(Integer otpCode) {
        this.otpCode = otpCode;
    }
}
