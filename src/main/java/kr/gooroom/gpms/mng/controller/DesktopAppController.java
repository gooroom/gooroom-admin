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

package kr.gooroom.gpms.mng.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.DesktopAppService;
import kr.gooroom.gpms.mng.service.DesktopAppVO;

/**
 * Handles requests for the desktop application management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class DesktopAppController {

	private static final Logger logger = LoggerFactory.getLogger(DesktopAppController.class);

	@Resource(name = "desktopAppService")
	private DesktopAppService desktopAppService;

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
	 * create(insert) new desktop app configuration data.
	 * 
	 * @param desktopAppVO DesktopAppVO desktop configuration data bean
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createDesktopApp")
	public @ResponseBody ResultVO createDesktopApp(@ModelAttribute("paramVO") DesktopAppVO desktopAppVO,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = desktopAppService.createDesktopApp(desktopAppVO);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * create cloned desktop app configuration data.
	 * 
	 * @param appId String app id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneDesktopApp")
	public @ResponseBody ResultVO cloneDesktopApp(@RequestParam(value = "appId") String appId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = desktopAppService.cloneDesktopApp(appId);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete desktop app configuration data.
	 * 
	 * @param desktopAppId string desktop configuration id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteDesktopApp")
	public @ResponseBody ResultVO deleteDesktopApp(
			@RequestParam(value = "desktopAppId") String desktopAppId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = desktopAppService.deleteDesktopApp(desktopAppId);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify desktop app configuration data.
	 * 
	 * @param desktopAppVO DesktopAppVO desktop configuration data bean
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateDesktopApp")
	public @ResponseBody ResultVO updateDesktopApp(@ModelAttribute("paramVO") DesktopAppVO desktopAppVO,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = desktopAppService.editDesktopApp(desktopAppVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response desktop app list data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopAppList")
	public @ResponseBody ResultVO readDesktopAppList(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopAppService.getDesktopAppList();
		} catch (Exception ex) {
			logger.error("error in readDesktopAppList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response desktop app list data for paging.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopAppListPaged")
	public @ResponseBody ResultVO readDesktopAppListPaged(HttpServletRequest req) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<>();

		options.put("GR_CONTEXT", createContextAddress(req));

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		options.put("searchKey", searchKey);
		String status = req.getParameter("status");
		options.put("status", status);

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"));
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chAppId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DAI.APP_ID");
		} else if ("chAppNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DAI.APP_NM");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DAI.MOD_DT");
		} else {
			options.put("paramOrderColumn", "DAI.APP_NM");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = desktopAppService.getDesktopAppListPaged(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readDesktopAppListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create context address for desktop application information.
	 * 
	 * @param req HttpServletRequest
	 * @return string context url.
	 *
	 */
	private String createContextAddress(HttpServletRequest req) {
		return GPMSConstants.ICON_SERVER_PROTOCOL + "://" +
				GPMSConstants.ICON_SERVERPATH + "/";
	}

	/**
	 * response desktop application information data.
	 * 
	 * @param desktopAppId string application id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopAppData")
	public @ResponseBody ResultVO readDesktopAppData(
			@RequestParam(value = "desktopAppId") String desktopAppId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopAppService.getDesktopAppData(desktopAppId);
		} catch (Exception ex) {
			logger.error("error in readDesktopAppData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
