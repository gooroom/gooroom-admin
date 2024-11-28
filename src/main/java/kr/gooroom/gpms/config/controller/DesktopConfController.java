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

package kr.gooroom.gpms.config.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.gooroom.gpms.client.service.ClientGroupService;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.DesktopConfService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Handles requests for the desktop configuration management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class DesktopConfController {

	private static final Logger logger = LoggerFactory.getLogger(DesktopConfController.class);

	@Resource(name = "desktopConfService")
	private DesktopConfService desktopConfService;

	@Resource(name = "clientGroupService")
	private ClientGroupService clientGroupService;

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
	 * get desktop configuration list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConfList")
	public @ResponseBody ResultVO readDesktopConfList() {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = desktopConfService.readDesktopConfList();
		} catch (Exception ex) {
			logger.error("error in readDesktopConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop configuration list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConfListPaged")
	public @ResponseBody ResultPagingVO readDesktopConfListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<>();

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");

		// search keyword
		options.put("searchKey", searchKey);

		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");

		// << Order >>
		if ("chConfName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.CONF_NM");
		} else if ("chConfId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.CONF_ID");
		} else if ("chThemeName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TM.THEME_NM");
		} else if ("chModUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.MOD_USER_ID");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.MOD_DT");
		} else if ("chRegUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.REG_USER_ID");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DCI.REG_DT");
		} else {
			options.put("paramOrderColumn", "DCI.CONF_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		if ("desc".equalsIgnoreCase(paramOrderDir)) {
			options.put("defaultOrderValue", "힣힣힣힣힣힣힣");
			options.put("defaultOrderSecondValue", "힣힣힣힣힣힣힢");
		} else {
			options.put("defaultOrderValue", null);
			options.put("defaultOrderSecondValue", null);
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = desktopConfService.getDesktopConfListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readDesktopConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * set new desktop configuration data.
	 * 
	 * @param desktopConfNm String new desktop configuration name.
	 * @param desktopTheme  theme id
	 * @param appDatas      app data array
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createDesktopConf")
	public @ResponseBody ResultVO createDesktopConf(
			@RequestParam(value = "desktopConfNm") String desktopConfNm,
			@RequestParam(value = "desktopTheme", required = false) String desktopTheme,
			@RequestParam(value = "appDatas[]", required = false) String[] appDatas,
			@RequestParam(value = "adminType", required = false) String adminType) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = desktopConfService.createDesktopConf(desktopConfNm, desktopTheme, appDatas, adminType);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify(edit) desktop configuration data.
	 * 
	 * @param desktopConfId desktop configuration id.
	 * @param desktopConfNm desktop configuration name.
	 * @param desktopTheme  theme id.
	 * @param appDatas      app data is array.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateDesktopConf")
	public @ResponseBody ResultVO updateDesktopConf(
			@RequestParam(value = "desktopConfId") String desktopConfId,
			@RequestParam(value = "desktopConfNm") String desktopConfNm,
			@RequestParam(value = "desktopTheme", required = false) String desktopTheme,
			@RequestParam(value = "appDatas[]", required = false) String[] appDatas) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = desktopConfService.updateDesktopConf(desktopConfId, desktopConfNm, desktopTheme,
					appDatas);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop configuration data by configuration id.
	 * 
	 * @param desktopConfId String new desktop configuration id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConf")
	public @ResponseBody ResultVO readDesktopConf(
			@RequestParam(value = "desktopConfId") String desktopConfId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopConfService.getDesktopConfData(desktopConfId);
		} catch (Exception ex) {
			logger.error("error in readDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete desktop configuration data by configuration id.
	 * 
	 * @param desktopConfId String new desktop configuration id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteDesktopConf")
	public @ResponseBody ResultVO deleteDesktopConf(
			@RequestParam(value = "desktopConfId") String desktopConfId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = desktopConfService.deleteDesktopConfData(desktopConfId);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in deleteDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop configuration data by client group id.
	 * 
	 * @param groupId String client group id.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConfByGroupId")
	public @ResponseBody ResultVO readDesktopConfByGroupId(
			@RequestParam(value = "groupId") String groupId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopConfService.getDesktopConfByGroupId(groupId);
		} catch (Exception ex) {
			logger.error("error in readDesktopConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop configuration data by dept cd.
	 * 
	 * @param deptCd String department code.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConfByDeptCd")
	public @ResponseBody ResultVO readDesktopConfByDeptCd(
			@RequestParam(value = "deptCd") String deptCd) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopConfService.getDesktopConfByDeptCd(deptCd);
		} catch (Exception ex) {
			logger.error("error in readDesktopConfByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop configuration data by dept cd.
	 * 
	 * @param userId String
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readDesktopConfByUserId")
	public @ResponseBody ResultVO readDesktopConfByUserId(
			@RequestParam(value = "userId") String userId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = desktopConfService.getDesktopConfByUserId(userId);
		} catch (Exception ex) {
			logger.error("error in readDesktopConfByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * set new desktop configuration data.
	 * 
	 * @param desktopConfId String
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneDesktopConf")
	public @ResponseBody ResultVO cloneDesktopConf(
			@RequestParam(value = "desktopConfId") String desktopConfId) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = desktopConfService.cloneDesktopConf(desktopConfId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in cloneDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
