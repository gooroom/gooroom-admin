package kr.gooroom.gpms.login;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.gooroom.gpms.config.service.impl.ClientConfDAO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Resource(name = "clientConfDAO")
	private ClientConfDAO clientConfDAO;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		try {
			// initialize 
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("loginId", authentication.getName());
			adminUserDao.updateLoginTrialInit(paramMap);	
			adminUserDao.updateOtpLoginTrialInit(paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		handle(request, response, authentication);

		// save sessionId, adn connetIp
		WebAuthenticationDetails detail = (WebAuthenticationDetails) authentication.getDetails();
		try {
			long rePresentCnt = adminUserDao.insertAdminUserPresentData(authentication.getName(), detail.getRemoteAddress(),
					request.getSession().getId());

			// 세션에 사용자 정보 저장
			request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		} catch (SQLException e) {
			logger.error("FAIL SAVE USER PRESENT OR LOGIN LOG...");
			// e.printStackTrace();
		}
		clearAuthenticationAttributes(request);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {

		String targetUrl = "";
		try {
			

			targetUrl = determineTargetUrl(request, authentication);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
			try {
				if(clientConfDAO.selectSiteLoginOtpEnable("") == 1) {
					return "/otp/login";
				} else {
					return "/home";
				}
			} catch (SQLException e) {
				return "/home";
			}
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
