package kr.gooroom.gpms.login;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import kr.gooroom.gpms.common.service.impl.GpmsCommonDAO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		handle(request, response, authentication);

		// save sessionId, adn connetIp
		WebAuthenticationDetails detail = (WebAuthenticationDetails) authentication.getDetails();
		try {
			long rePresentCnt = adminUserDao.insertAdminUserPresentData(authentication.getName(), detail.getRemoteAddress(),
					request.getSession().getId());
			
		} catch (SQLException e) {
			logger.error("FAIL SAVE USER PRESENT OR LOGIN LOG...");
			// e.printStackTrace();
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

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}
}
