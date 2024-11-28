package kr.gooroom.spring.security;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import jakarta.servlet.http.HttpServletRequest;

public class MfaAuthorizationFilter extends AuthorizationFilter {
    
    public MfaAuthorizationFilter(AuthorizationManager<HttpServletRequest> authorizationManager) {
        super(authorizationManager);
    }
}
