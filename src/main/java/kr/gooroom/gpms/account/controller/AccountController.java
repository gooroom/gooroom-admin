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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * Handles requests for login process
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class AccountController {

	/**
	 * login process
	 * 
	 * @param status string for login result, success or fail
	 * @return main/login
	 */
	@GetMapping(value = "/login")
	public ModelAndView login(@RequestParam(value = "s", required = false) String status,
			@RequestParam(value = "c", required = false) String error) {

		ModelAndView mv = new ModelAndView("main/login");

		if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(status)) {

			if(error != null ) {
				Decoder decoder = Base64.getDecoder();
				byte[] decodedBytes = decoder.decode(error);

				String[] errorCode = (new String(decodedBytes)).split(";");

				if (GPMSConstants.ERR_LOGIN_PASSWORD.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.error.account"));
				} else if (GPMSConstants.ERR_LOGIN_USER.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.error.account"));
				} else if (GPMSConstants.ERR_LOGIN_ACCOUNT.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.error.account"));
				} else if (GPMSConstants.ERR_LOGIN_DENIED.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.denied"));
				} else if (GPMSConstants.ERR_LOGIN_DUPLICATE.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.duplicate", errorCode[1]));
					mv.addObject("force", "1");
				} else if (GPMSConstants.ERR_LOGIN_ETC.equalsIgnoreCase(errorCode[0])) {
					mv.addObject("msg", MessageSourceHelper.getMessage("system.login.error"));
				}
			} else {
				mv.setViewName("/");
			}
		} else if ("denied".equalsIgnoreCase(status)) {
			mv.addObject("msg", MessageSourceHelper.getMessage("system.common.deniedgpms"));
		}

		return mv;
	}
}
