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

import java.util.Calendar;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.user.service.AdminUserService;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;

/**
 * Intercepter class for MVC framework.
 * <p>
 * create global object.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class GPMSInterceptor implements HandlerInterceptor {

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	@Resource(name = "adminUserService")
	private AdminUserService adminUserService;

	/**
	 * pre handle method.
	 * 
	 * @param req     HttpServletRequest
	 * @param res     HttpServletResponse
	 * @param handler Object
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

		String actItem = req.getRequestURI();

		// check Authority
		String adminId = LoginInfoHelper.getUserId();
		String adminRule = "";
		if (actItem.equalsIgnoreCase("/readUserList")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserListPaged")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/isExistUserId")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUser")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUserWithRule")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/updateUserData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/updateUserLoginTrialCount")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/deleteUserData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserListInDept")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserListPagedInDept")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserListInOnline")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUsersInDept")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/deleteUserInDept")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/deleteUsersInDept")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUserDataFromFile")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUserFileFromData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/createUserSampleFileFromData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserReqList")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserReqListPaged")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/approvalUserReq")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readUserReqActListPaged")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readCurrentUserData")) {
			adminRule = "user_admin";
		} else if (actItem.equalsIgnoreCase("/readViolatedClientCount")) {
			adminRule = "client_admin";
		} else if (actItem.equalsIgnoreCase("/createResetViolatedClient")) {
			adminRule = "client_admin";
		} else if (actItem.equalsIgnoreCase("/readUpdatePackageClientList")) {
			adminRule = "client_admin";
		} else if (actItem.equalsIgnoreCase("/createNotice")) {
			adminRule = "notice_admin";
		} else if (actItem.equalsIgnoreCase("/updateNotice")) {
			adminRule = "notice_admin";
		} else if (actItem.equalsIgnoreCase("/deleteNotice")) {
			adminRule = "notice_admin";
		} else if (actItem.equalsIgnoreCase("/readNoticeListPaged")) {
			adminRule = "notice_admin";
		} else if (actItem.equalsIgnoreCase("/registerPortableDataList")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/readPortableDataList")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/readPortableDataListPaged")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/readReapproveStatus")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/updateApprove")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/deletePortableDataList")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/registerPortableData")) {
			adminRule = "portable_Admin";
		} else if (actItem.equalsIgnoreCase("/readPortableData")) {
			adminRule = "portable_Admin";
		}

		if (!adminRule.equalsIgnoreCase("")) {
			ResultVO adminRuleRe = adminUserService.getAuthority(adminId, adminRule);
			if (GPMSConstants.MSG_FAIL.equals(adminRuleRe.getStatus().getResult())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * post handle method.
	 * 
	 * @param req     HttpServletRequest
	 * @param res     HttpServletResponse
	 * @param handler Object
	 * @return StatusVO result status
	 */
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) {

		// for version
		if (modelAndView != null) {

			// get locale for message
			modelAndView.addObject("gpmsLanguage", req.getLocale().getLanguage());

			modelAndView.addObject("gpmsVersion", String.valueOf(Calendar.getInstance().getTimeInMillis()));
			// modelAndView.addObject("gpmsVersion", "1");
			if (req != null && req.getParameter("b") != null) {
				String[] menus = req.getParameter("b").split(":");
				modelAndView.addObject("GRMENUS", menus);
			}
		}
	}
}
