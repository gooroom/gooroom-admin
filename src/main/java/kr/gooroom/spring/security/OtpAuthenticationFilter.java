package kr.gooroom.spring.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.totp.OtpAuthenticationFailureHandler;
import kr.gooroom.gpms.totp.OtpAuthenticationSuccessHandler;
import kr.gooroom.gpms.totp.Service.OtpAuthenticationToken;

public class OtpAuthenticationFilter extends OncePerRequestFilter {

    private OtpAuthenticationSuccessHandler otpAuthenticationSuccessHandler;
    private OtpAuthenticationFailureHandler otpAuthenticationFailureHandler;
    private AuthenticationManager authenticationManager;

    public void setOtpAuthenticationSuccessHandler(OtpAuthenticationSuccessHandler successHandler) {
        this.otpAuthenticationSuccessHandler = successHandler;
    }

    public void setOtpAuthenticationFailureHandler(OtpAuthenticationFailureHandler failureHandler) {
        this.otpAuthenticationFailureHandler = failureHandler;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().contains("/gpms/otp/authenticate") && "POST".equalsIgnoreCase(request.getMethod())) {
            try {
                String otpCode = request.getParameter("userCode");

                OtpAuthenticationToken otpAuthenticationToken = new OtpAuthenticationToken(Integer.parseInt(otpCode));

                Authentication authentication = this.authenticationManager.authenticate(otpAuthenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                otpAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            } catch (AuthenticationException ex) {
                otpAuthenticationFailureHandler.onAuthenticationFailure(request, response, ex);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
