package kr.gooroom.gpms.totp;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.login.exception.OtpTrialExceedException;

public class OtpAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception instanceof OtpTrialExceedException) {
            SecurityContextHolder.clearContext();
            response.sendRedirect("/gpms/login?s=fail&c=" + exception.getMessage());
        } else {
            response.sendRedirect("/gpms/otp/login?s=fail&c=" + exception.getMessage());
        }
    }
}
