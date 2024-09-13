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
import org.springframework.web.util.ContentCachingRequestWrapper;

import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.common.utils.LogUtil;
import kr.gooroom.gpms.log.filter.CustomHttpServletResponseWrapper;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

        ContentCachingRequestWrapper httpsServletRequest = new ContentCachingRequestWrapper(request);
        CustomHttpServletResponseWrapper httpServletResponse = new CustomHttpServletResponseWrapper(response);

		setDefaultTargetUrl("/login");

		String reqURL = LogUtil.getURLString(request);
		String actType = LogUtil.getActType(reqURL);
		String actTarget = LogUtil.getTarget(reqURL);
		String connectedIP = request.getRemoteAddr();
		String actData = LogUtil.getActData(httpsServletRequest, httpServletResponse);
		boolean isSuccess = httpServletResponse.getStatusCode() < 400 ? true : false;
		String adminId = authentication.getName();

		gpmsCommonService.createUserActLogHistory(actType, reqURL, actData, connectedIP, adminId, actTarget, isSuccess);
		
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
