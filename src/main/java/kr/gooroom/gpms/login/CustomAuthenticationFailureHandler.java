package kr.gooroom.gpms.login;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.gooroom.gpms.job.service.impl.JobDAO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {

		if(exception instanceof ProviderNotFoundException) {
			response.sendRedirect("otp/login");
		} else {
			response.sendRedirect("login?s=fail&c=" + exception.getMessage());
		}
	}
}
