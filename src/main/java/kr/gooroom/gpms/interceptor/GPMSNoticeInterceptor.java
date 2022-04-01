/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.interceptor;

import java.security.PublicKey;
import java.security.Signature;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.errors.BadRequestException;
import kr.gooroom.gpms.common.errors.NotAcceptableException;
import kr.gooroom.gpms.common.errors.UnauthorizedException;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.utils.PemUtil;
import kr.gooroom.gpms.user.service.UserTokenService;
import kr.gooroom.gpms.user.service.UserTokenVO;

/**
 * Intercepter class for Notice.
 * <p>
 * 
 */

public class GPMSNoticeInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(GPMSNoticeInterceptor.class);

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "userTokenService")
	private UserTokenService userTokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Cookie cookieSigning = WebUtils.getCookie(request, "SIGNING");
		Cookie cookieSessionId = WebUtils.getCookie(request, "SESSION_ID");
		Cookie cookieClientId = WebUtils.getCookie(request, "CLIENT_ID");
		Cookie cookieLangCode = WebUtils.getCookie(request, "LANG_CODE");
		if (cookieSigning == null || cookieClientId == null) {
			throw new NotAcceptableException("Cookie not exist");
		}

		String signing = cookieSigning.getValue();
		String sessionId = cookieSessionId != null ? cookieSessionId.getValue() : "";
		String clientId = cookieClientId.getValue();
		String langCode = cookieLangCode != null ? cookieLangCode.getValue() : "en";

		logger.debug("Cookie values: {}, {}, {}, {}", signing, sessionId, clientId, langCode);

		String certificatePem = null;
		try {
			ResultVO resultVO = clientService.selectClientInfo(clientId);
			ClientVO clientVO = ((ClientVO[]) resultVO.getData())[0];
			certificatePem = clientVO.getKeyInfo();
		} catch (Exception ex) {
			throw new BadRequestException("ClientId not found");
		}

		PublicKey publicKey = PemUtil.getPublicKey(certificatePem);
		String text = clientId + "&" + sessionId;
		Signature sigVerify = Signature.getInstance("SHA256withRSA");
		sigVerify.initVerify(publicKey);
		sigVerify.update(text.getBytes());

		if (!sigVerify.verify(Base64.decode(signing.getBytes()))) {
			throw new UnauthorizedException();
		}

		String userId = null;
		if (!ObjectUtils.isEmpty(sessionId)) {
			try {
				UserTokenVO userTokenVO = userTokenService.selectTokenByTokenId(clientId, sessionId,
						GPMSConstants.TOKEN_LOGIN_STATUS_CODE_VALID);
				if (userTokenVO == null || ObjectUtils.isEmpty(userTokenVO.getUserId())) {
					throw new UnauthorizedException();
				}
				userId = userTokenVO.getUserId();
			} catch (Exception ex) {
				throw new UnauthorizedException();
			}
		}

		request.setAttribute("CLIENT_ID", clientId);
		request.setAttribute("USER_ID", userId);
		request.setAttribute("LANG_CODE", langCode);

		return true;
	}
}
