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

package kr.gooroom.gpms.account.controller;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Handles requests for login process
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	private static final String ERR_MSG = "system.login.error";
	private static final String ERR_ACCOUNT_MSG = "system.login.error.account";
	private static final String ERR_DENIED_MSG = "system.login.denied";
	private static final String ERR_DENIED_GPMS_MSG = "system.common.deniedgpms";
	private static final String ERR_LOCK_MSG = "system.login.error.lock";
	private static final String ERR_LOCK_TIME_MSG = "system.login.error.lock.time";
	private static final String ERR_DUPLICATE_MSG = "system.login.duplicate";
	private static final String ERR_TRIAL_MSG = "system.login.login.trial2";
	private static final String ERR_NOTMATCH_MSG = "system.login.error.notmatch";

	/**
	 * login process
	 * 
	 * @return main/login
	 */
	@GetMapping("/login")
	public ModelAndView loginGet() {
		ModelAndView mv = new ModelAndView("main/login");
		return mv;
	}

	@PostMapping("/login")
	public ModelAndView login(
			@RequestAttribute(value = "s", required = false) String status,
			@RequestAttribute(value = "c", required = false) String error) {

		final int MAX_B64_LEN = 512;
		final int MAX_TEXT_LEN = 256;
		final Pattern B64_RE = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
		final Pattern CODE_RE = Pattern.compile("^[A-Z][0-9]{2}$");

		ModelAndView mv = new ModelAndView("main/login");

		if (!GPMSConstants.MSG_FAIL.equalsIgnoreCase(status)) {
			if ("denied".equalsIgnoreCase(status)) {
				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_DENIED_GPMS_MSG));
			}
			return mv;
		}

		if (error == null || error.isBlank()) {
			mv.setViewName("/");
			return mv;
		}

		if (error.length() > MAX_B64_LEN) {
			logger.warn("Error parameter too long (base64 length)");
			mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
			return mv;
		}

		if (!B64_RE.matcher(error).matches()) {
			logger.warn("Invalid Base64 format");
			mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
			return mv;
		}

		try {

			byte[] decoded = Base64.getDecoder().decode(error);
			String decodedStr = new String(decoded, StandardCharsets.UTF_8);

			if (decodedStr.length() > MAX_TEXT_LEN) {
				logger.warn("Decoded error data too long");
				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
				return mv;
			}

			String[] parts = decodedStr.split(";", 3);
			String code = parts[0];

			if (!CODE_RE.matcher(code).matches()) {
				logger.warn("Invalid error code format: {}", code);
				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
				return mv;
			}

			if (GPMSConstants.ERR_LOGIN_PASSWORD.equalsIgnoreCase(code)
					|| GPMSConstants.ERR_LOGIN_USER.equalsIgnoreCase(code)
					|| GPMSConstants.ERR_LOGIN_ACCOUNT.equalsIgnoreCase(code)) {

				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));

			} else if (GPMSConstants.ERR_LOGIN_DENIED.equalsIgnoreCase(code)) {

				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_DENIED_MSG));

			} else if (GPMSConstants.ERR_LOGIN_DUPLICATE.equalsIgnoreCase(code)) {

				String p1 = (parts.length > 1) ? parts[1] : "";
				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_DUPLICATE_MSG, p1));
				mv.addObject("force", "1");

			} else if (GPMSConstants.ERR_LOGIN_LOCK.equalsIgnoreCase(code)) {

				String p1 = (parts.length > 1) ? parts[1] : "";
				mv.addObject("msg",  MessageSourceHelper.getMessage(ERR_LOCK_MSG));
				mv.addObject("msg2", MessageSourceHelper.getMessage(ERR_LOCK_TIME_MSG, p1));

			} else if (GPMSConstants.ERR_LOGIN_PASSWORD_TRIAL.equalsIgnoreCase(code)) {

				String p1 = (parts.length > 1) ? parts[1] : "";
				String p2 = (parts.length > 2) ? parts[2] : "";
				mv.addObject("msg",  MessageSourceHelper.getMessage(ERR_NOTMATCH_MSG, new String[]{p1, p2}));
				mv.addObject("msg2", MessageSourceHelper.getMessage(ERR_TRIAL_MSG, p2));

			} else if (GPMSConstants.ERR_LOGIN_ETC.equalsIgnoreCase(code)) {

				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_MSG));

			} else {

				logger.warn("Unhandled error code: {}", code);
				mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
			}

		} catch (IllegalArgumentException e) {
			logger.warn("Base64 decoding failed", e);
			mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
		} catch (Exception e) {
			logger.error("Login error handling failed", e);
			mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
		}

		return mv;
	}
}
