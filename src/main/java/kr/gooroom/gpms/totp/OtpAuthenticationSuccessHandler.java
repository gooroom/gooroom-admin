package kr.gooroom.gpms.totp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class OtpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource(name = "adminUserDAO")
    AdminUserDAO adminUserDao;

    private static final Logger logger = LoggerFactory.getLogger(OtpAuthenticationSuccessHandler.class);

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 참고 (Username, Password 이용한 인증 성공 시, 세션에 Context 보관)
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

//        super.successfulAuthentication(request, response, chain, authentication);
        handle(request, response, authentication);

        // save sessionId, adn connetIp
        WebAuthenticationDetails detail = (WebAuthenticationDetails) authentication.getDetails();
        try {
            long rePresentCnt = adminUserDao.insertAdminUserPresentData(authentication.getName(), detail.getRemoteAddress(),
                    request.getSession().getId());

            // 세션에 사용자 정보 저장
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // init login trial time
            HashMap<String, Object> trialParams = new HashMap<String, Object>();
            trialParams.put("adminId", authentication.getName());
            adminUserDao.updateOtpLoginTrialInit(trialParams);

            // update renew QR when login succeeded
            HashMap<String, Object> savedParams = new HashMap<>();
            savedParams.put("adminId", authentication.getName());
            savedParams.put("isSaved", 1);
            adminUserDao.updateOtpSecretSaved(savedParams);
        } catch (SQLException e) {
             e.printStackTrace();
        }
        clearAuthenticationAttributes(request);
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        String targetUrl = determineTargetUrl(request, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {

        boolean isUser = false;
        boolean isSuperAdmin = false;
        boolean isAdmin = false;
        boolean isPartAdmin = false;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_SUPER")) {
                isSuperAdmin = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_PART")) {
                isPartAdmin = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                isUser = true;
                break;
            }
        }

        if (isSuperAdmin) {
            return "/super";
        } else if (isAdmin) {
            return "/home";
        } else if (isPartAdmin) {
            return "/part";
        } else if (isUser) {
            return "/user";
        } else {
            throw new IllegalStateException();
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
