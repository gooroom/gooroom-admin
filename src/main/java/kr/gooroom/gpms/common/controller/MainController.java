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

package kr.gooroom.gpms.common.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientSummaryVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles requests for main process
 * <p>
 * home page service.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "gpmsCommonService")
	private GpmsCommonService gpmsCommonService;

	/**
	 * initialize binder for date format
	 * <p>
	 * ex) date format : 2017-10-04
	 * 
	 * @param binder WebDataBinder
	 *
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * show(generate) dashboard page
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ModelAndView home
	 */
	@GetMapping(value = "/pageDashboard")
	public ModelAndView pageDashboard(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ModelAndView mv = new ModelAndView("pageDashboard");
		ClientSummaryVO vo = new ClientSummaryVO();

		// dashboard items
		try {

			// 1. client status
			ResultVO result = clientService.getClientStatusSummary();
			if (GPMSConstants.MSG_SUCCESS.equals(result.getStatus().getResult())) {

				ClientSummaryVO reVO = (ClientSummaryVO) result.getData()[0];
				vo.setTotalCount(reVO.getTotalCount());
				vo.setOnCount(reVO.getOnCount());
				vo.setOffCount(reVO.getOffCount());
			}

			// 2. user login status
			result = clientService.getLoginStatusSummary();
			if (GPMSConstants.MSG_SUCCESS.equals(result.getStatus().getResult())) {

				ClientSummaryVO reVO = (ClientSummaryVO) result.getData()[0];
				vo.setLoginCount(reVO.getLoginCount());
				vo.setUserCount(reVO.getUserCount());
			}

			// 3. package status
			result = clientService.getUpdatePackageSummary();
			if (GPMSConstants.MSG_SUCCESS.equals(result.getStatus().getResult())) {

				ClientSummaryVO reVO = (ClientSummaryVO) result.getData()[0];
				vo.setNoUpdateCount(reVO.getNoUpdateCount());
				vo.setUpdateCount(reVO.getUpdateCount());
				vo.setMainUpdateCount(reVO.getMainUpdateCount());
			}

			mv.addObject("clientSummary", vo);

		} catch (Exception e) {
			e.printStackTrace();
		}

		mv.addObject("clientData", "{\"total\":\"123\"}");

		return mv;
	}

	/**
	 * response available network data for gpms connect
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readGpmsAvailableNetwork")
	public @ResponseBody ResultVO readGpmsAvailableNetwork(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = gpmsCommonService.getGpmsAvailableNetwork();
		} catch (Exception ex) {
			logger.error("error in readGpmsAvailableNetwork : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * show(generate) refuse page
	 * <p>
	 * not available network client
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ModelAndView home
	 *
	 */
	@PostMapping(value = "/refusePage")
	public ModelAndView pageClient(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		return new ModelAndView("refusePage");
	}

}
