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

package kr.gooroom.gpms.user.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.AdminUserService;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;

/**
 * Handles requests for the user RST service.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@RestController
public class UserRestController {

	private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "adminUserService")
	private AdminUserService adminUserService;

	/**
	 * initialize binder for date format
	 * <p>
	 * ex) date format : 2017-10-04
	 * 
	 * @param binder WebDataBinder
	 * @return void
	 *
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * register secret code for NFC data
	 * 
	 * @param nfc_secret_data string nfc secret data
	 * @param admin_id        string admin id
	 * @param admin_pw        string admin password
	 * @param user_id         string target user id
	 * @return ResultVO result data bean
	 *
	 */
	@GetMapping("/v1/gpms/nfc_reg")
	public ResultVO registerNFCCode(@RequestParam(value = "nfc_secret_data") String nfc_secret_data,
			@RequestParam(value = "admin_id") String admin_id,
			@RequestParam(value = "admin_pw") String admin_pw,
			@RequestParam(value = "user_id") String user_id) {

		ResultVO resultVO = new ResultVO();

		// validation
		if (nfc_secret_data == null || "".equals(nfc_secret_data)) {

			return new ResultVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
					MessageSourceHelper.getMessage("nfc.error.type1"));

		} else if (admin_id == null || "".equals(admin_id)) {

			return new ResultVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
					MessageSourceHelper.getMessage("nfc.error.type2"));

		} else if (admin_pw == null || "".equals(admin_pw)) {

			return new ResultVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
					MessageSourceHelper.getMessage("nfc.error.type3"));

		} else {

			// check admin authority
			try {
				ResultVO authRe = adminUserService.getAdminUserAuthAndInfo(admin_id, admin_pw);

				if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(authRe.getStatus().getResult())) {
					// register NFC CODE
					UserVO vo = new UserVO();
					vo.setUserId(user_id);
					vo.setNfcSecretData(nfc_secret_data);

					StatusVO status = userService.editUserNfcData(vo);
					resultVO.setStatus(status);

				} else {
					resultVO = authRe;
				}
			} catch (Exception ex) {

				logger.error("error in registerNFCCode : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
				}
			}

			return resultVO;
		}
	}

}
