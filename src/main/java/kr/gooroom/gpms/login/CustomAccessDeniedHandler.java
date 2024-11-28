package kr.gooroom.gpms.login;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.config.service.impl.ClientConfDAO;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Resource(name = "clientConfDAO")
    ClientConfDAO clientConfDAO;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        try {
            if(clientConfDAO.selectSiteLoginOtpEnable("") == 1) {
                logger.info("[CustomAccessDeniedHandler] : otp enabled, go to logout");
                response.sendRedirect("logout");
            } else {
                logger.info("[CustomAccessDeniedHandler] : otp not enabled, go to login failure");
                response.sendRedirect("/login?s=fail&c=" + exception.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
