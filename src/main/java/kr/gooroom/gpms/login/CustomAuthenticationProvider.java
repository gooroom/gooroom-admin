package kr.gooroom.gpms.login;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.login.exception.BadAccessIpException;
import kr.gooroom.gpms.login.exception.DuplicateAccessIpException;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private Pbkdf2PasswordEncoder passwordEncoder;

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		// String sessionId = ((WebAuthenticationDetails)
		// authentication.getDetails()).getSessionId();

		// check password.
		User user;
		Collection<? extends GrantedAuthority> authorities;
		try {
			user = userLoginService.loadUserByUsername(username);

			// check password
			if (!passwordEncoder.matches(password, user.getPassword())) {
				throw new BadCredentialsException(GPMSConstants.ERR_LOGIN_PASSWORD);
			}

			// check connection ip
			final WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			String myIp = details.getRemoteAddress();
			boolean isOk = false;
			try {
				List<String> connectIpList = user.getConnectIpList();
				if (connectIpList != null && connectIpList.size() > 0) {
					for (String connIp : connectIpList) {
						if (connIp != null && connIp.contains("*")) {
							connIp = connIp.substring(0, connIp.indexOf("*"));
							if (myIp.startsWith(connIp)) {
								isOk = true;
								break;
							}
						} else {
							if (myIp.equals(connIp)) {
								isOk = true;
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				// System.out.println("ex ::: " + e);
				// e.printStackTrace();
				isOk = false;
			}
			if (!isOk && !user.isUserAccount()) {
				throw new BadAccessIpException(GPMSConstants.ERR_LOGIN_DENIED);
			}

			// check duplicate login by session Id
//			if (user.getSessionId() != null && !"".equals(user.getSessionId())) {
			// compare sessionId
			// if (!myIp.equals(user.getConnectIp()) &&
			// !sessionId.equals(user.getSessionId())) {
			if (user.getConnectIp() != null && !myIp.equals(user.getConnectIp())) {
				// reject login as duplicate login in different client
				ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
						.currentRequestAttributes();
				if (attr != null && attr.getRequest() != null && "1".equals(attr.getRequest().getParameter("force"))) {
					// reset admin preset
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(),
							false);
					// List<SessionInformation> sessions = sessionRegistry.getAllSessions(user,
					// false);
					if (sessions != null) {
						for (SessionInformation sessionInformation : sessions) {
							List<String> sessionsToClose = new ArrayList<>();
							sessionsToClose.add(sessionInformation.getSessionId());

							// logging.
							// accessLogDAO.closeAccessLog(sessionsToClose);

							sessionInformation.expireNow();
						}
					}
				} else {
					adminUserDao.updateDuplicateReqLoginData(user.getUsername(), myIp);
					throw new DuplicateAccessIpException((new StringBuffer(GPMSConstants.ERR_LOGIN_DUPLICATE))
							.append(";").append(user.getConnectIp()).toString());
				}
			}
//			}

			authorities = user.getAuthorities();
		} catch (UsernameNotFoundException e) {
			// logger.info(e.toString());
			Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder
					.encode((new StringBuffer(GPMSConstants.ERR_LOGIN_USER)).append(";NONE").toString().getBytes());
			throw new UsernameNotFoundException(new String(encodedBytes));
		} catch (BadCredentialsException e) {
			// logger.info(e.toString());
			Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder
					.encode((new StringBuffer(GPMSConstants.ERR_LOGIN_ACCOUNT)).append(";NONE").toString().getBytes());
			throw new BadCredentialsException(new String(encodedBytes));
		} catch (BadAccessIpException e) {
			// logger.info(e.toString());
			Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder
					.encode((new StringBuffer(GPMSConstants.ERR_LOGIN_DENIED)).append(";NONE").toString().getBytes());
			throw new BadAccessIpException(new String(encodedBytes));
		} catch (DuplicateAccessIpException e) {
			// logger.info(e.toString());
			Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder.encode(e.getMessage().getBytes());
			throw new DuplicateAccessIpException(new String(encodedBytes));
		} catch (Exception e) {
			// logger.info(e.toString());
			Encoder encoder = Base64.getEncoder();
			byte[] encodedBytes = encoder
					.encode((new StringBuffer(GPMSConstants.ERR_LOGIN_ETC)).append(";NONE").toString().getBytes());
			throw new RuntimeException(new String(encodedBytes));
		}

		return new UsernamePasswordAuthenticationToken(username, user.getPassword(), authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
