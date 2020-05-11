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

package kr.gooroom.gpms.common.utils;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import kr.gooroom.gpms.account.service.AccountVO;

/**
 * Helper class for login information service.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("loginInfoHelper")
public class LoginInfoHelper {

	public static AccountVO getAccountVO() {
		try {
			return (AccountVO) RequestContextHolder.getRequestAttributes().getAttribute("AccountVO",
					RequestAttributes.SCOPE_SESSION);
		} catch (Exception e) {
			// DO NOTHING
		}

		return null;
	}

	/**
	 * check if user is auchenticated
	 * 
	 * @return Boolean true when user was authenticated before
	 *
	 */
	public static Boolean isAuthenticated() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return authentication.isAuthenticated();
		} catch (Exception e) {
			// DO NOTHING
		}
		return false;
	}

	/**
	 * get user id from account information.
	 * 
	 * @return Sting user id.
	 *
	 */
	public static String getUserId() {
		String result = "";
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object obj = authentication.getDetails();
			result = authentication.getName();
		} catch (Exception e) {
			// DO NOTHING
		}

		return result;
	}
	
	public static String getUserGRRole() {
		String result = "";
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			for (GrantedAuthority grantedAuthority : authorities) {
				if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
					result = "ADMIN";
					break;
				} else if (grantedAuthority.getAuthority().equals("ROLE_SUPER")) {
					result = "SUPER";
					break;
				} else if (grantedAuthority.getAuthority().equals("ROLE_PART")) {
					result = "PART";
					break;
				}
			}
		} catch (Exception e) {
			// DO NOTHING
		}

		return result;
	}
}
