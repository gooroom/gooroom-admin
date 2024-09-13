package kr.gooroom.gpms.login;

import java.util.Collection;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;

public class MfaAuthorizationManager implements AuthorizationManager<HttpServletRequest> {

    private static final Logger logger = LoggerFactory.getLogger(MfaAuthorizationManager.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, HttpServletRequest request) {
        //  OtpEnable == true : 1차 인증 끝난 경우 => ACCESS_DENIED
        //  OtpEnable == false : 1차 인증 끝난 경우 => ACCESS_GRANTED
        //  SUPER 계정 허용 조건
        //  1. SITE_MSTR OTP_ENABLED
        //  2. ROLE_ADMIN
        Authentication authentication = supplier.get();
        if(authentication.isAuthenticated() && authentication instanceof CustomAuthenticationToken) {
            try {
                CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;

                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority grantedAuthority : authorities) {
                    if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                        // ROLE_ADMIN
                        if(customAuthenticationToken.getIsOtpEnabled()) {
                            String requestUrl = request.getRequestURI();
                            if(!requestUrl.contains("otp")) {
                                // Base64.Encoder encoder = Base64.getEncoder();
                                // byte[] encodedBytes = encoder
                                //         .encode((new StringBuffer(GPMSConstants.ERR_LOGIN_ETC)).append(";NONE").toString().getBytes());
                                // throw new AccessDeniedException(encodedBytes);
                                return new AuthorizationDecision(false);
                            }
                        }
                    }
                }
            } 
            // catch(AccessDeniedException e) {
            //     return new AuthorizationDecision(false);
            // } 
            catch(Exception e) {
                logger.error(e.getMessage());
                return new AuthorizationDecision(false);
            }
        }

        return new AuthorizationDecision(true);
    }

    // @Override
    // public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
    //     // OtpEnable == true : 1차 인증 끝난 경우 => ACCESS_DENIED
    //     // OtpEnable == false : 1차 인증 끝난 경우 => ACCESS_GRANTED
    //     // SUPER 계정 허용 조건
    //     //  1. SITE_MSTR OTP_ENABLED
    //     //  2. ROLE_ADMIN
    //     if(authentication.isAuthenticated() && authentication instanceof CustomAuthenticationToken) {
    //         CustomAuthenticationToken customAuthenticationToken = (CustomAuthenticationToken) authentication;

    //         Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    //         for (GrantedAuthority grantedAuthority : authorities) {
    //             if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
    //                 // ROLE_ADMIN 케이스 처리
    //                 if(customAuthenticationToken.getIsOtpEnalbed()) {
    //                     String requestUrl = fi.getRequestUrl();
    //                     if(!requestUrl.contains("otp")) {
    //                         Base64.Encoder encoder = Base64.getEncoder();
    //                         byte[] encodedBytes = encoder
    //                                 .encode((new StringBuffer(GPMSConstants.ERR_LOGIN_ETC)).append(";NONE").toString().getBytes());
    //                         throw new AccessDeniedException(new String(encodedBytes));
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     return ACCESS_ABSTAIN;
    // }

    // @Override
    // public boolean supports(ConfigAttribute configAttribute) {
    //     return configAttribute != null && SecurityConfig.createList("ROLE_ADMIN").contains(configAttribute.getAttribute());
    // }

    // @Override
    // public boolean supports(Class<?> clazz) {
    //     return FilterInvocation.class.isAssignableFrom(clazz);
    // }
}
