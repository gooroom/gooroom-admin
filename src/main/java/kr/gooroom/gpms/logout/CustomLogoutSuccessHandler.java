package kr.gooroom.gpms.logout;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		setDefaultTargetUrl("/login");
		
		// save sessionId, adn connetIp
		WebAuthenticationDetails detail = (WebAuthenticationDetails) authentication.getDetails();

		try {
			adminUserDao.deleteAdminUserPresentData(authentication.getName(), detail.getRemoteAddress(),
					(String) authentication.getPrincipal());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		super.handle(request, response, authentication);
	}

}
