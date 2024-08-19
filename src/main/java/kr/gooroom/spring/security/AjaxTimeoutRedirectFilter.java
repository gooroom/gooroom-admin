package kr.gooroom.spring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class AjaxTimeoutRedirectFilter extends GenericFilterBean {

	private static final Logger logger = LoggerFactory.getLogger(AjaxTimeoutRedirectFilter.class);

	private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	private int customSessionExpiredErrorCode = 901;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException {

		try {
			chain.doFilter(request, response);
			// logger.debug("Chain processed normally");
		} catch (IOException ex) {
			throw ex;
		} catch (Exception ex) {
			Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
			RuntimeException ase = (AuthenticationException) throwableAnalyzer
					.getFirstThrowableOfType(AuthenticationException.class, causeChain);

			if (ase == null) {
				ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class,
						causeChain);
			}

			if (ase != null) {
				if (ase instanceof AuthenticationException) {
					throw ase;
				} else if (ase instanceof AccessDeniedException) {

					if (authenticationTrustResolver
							.isAnonymous(SecurityContextHolder.getContext().getAuthentication())) {
						logger.info("User session expired or not logged in yet");

						if ("application/x-www-form-urlencoded"
								.equals(request.getContentType())) {
							logger.info("Ajax call detected, send {} error code", this.customSessionExpiredErrorCode);
							HttpServletResponse resp = (HttpServletResponse) response;
							resp.sendError(this.customSessionExpiredErrorCode);
						} else {
							logger.info("Redirect to login page");
							throw ase;
						}
					} else {
						throw ase;
					}
				}
			}

		}
	}

	private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {
		/**
		 * @see org.springframework.security.web.util.ThrowableAnalyzer#initExtractorMap()
		 */
		protected void initExtractorMap() {
			super.initExtractorMap();

			registerExtractor(ServletException.class, throwable -> {
				ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
				return ((ServletException) throwable).getRootCause();
			});
		}

	}

	public void setCustomSessionExpiredErrorCode(int customSessionExpiredErrorCode) {
		this.customSessionExpiredErrorCode = customSessionExpiredErrorCode;
	}

}
