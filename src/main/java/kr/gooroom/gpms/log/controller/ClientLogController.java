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

package kr.gooroom.gpms.log.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.log.service.ClientLogService;

/**
 * Handles requests for the logging management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientLogController {

	private static final Logger logger = LoggerFactory.getLogger(ClientLogController.class);

	@Resource(name = "clientLogService")
	private ClientLogService clientLogService;

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
	 * generate security log list data for paging
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityLogListPaged")
	public @ResponseBody ResultPagingVO readSecurityLogListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
			String toDate = StringUtils.defaultString(req.getParameter("toDate"));
			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -20);
				fromDate = dateFormat.format(cal.getTime());
			}

			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("fromDate", fromDate);
			options.put("toDate", toDate);
			String logItem = StringUtils.defaultString(req.getParameter("logItem"));
			if ("OS".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "os_log" });
			} else if ("MEDIA".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "media_log" });
			} else if ("EXE".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "exe_log" });
			} else if ("BOOT".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "boot_log" });
			} else if ("AGENT".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "agent_log" });
			} else {
				options.put("logItems", new String[] { "os_log", "media_log", "exe_log", "boot_log", "agent_log" });
			}

			// << paging >>
			options.put("paramStart", Integer.parseInt(ObjectUtils.defaultIfNull(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(ObjectUtils.defaultIfNull(req.getParameter("length"), "10")));

			// << order >>
			String paramOrderColumn = req.getParameter("orderColumn");
			String paramOrderDir = req.getParameter("orderDir");
			if ("CLIENT_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CLIENT_ID");
			} else if ("USER_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "USER_ID");
			} else if ("LOG_TP".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "LOG_TP");
			} else if ("LOG_VALUE".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "LOG_VALUE");
			} else {
				options.put("paramOrderColumn", "LOG_SEQ");
			}

			if ("DESC".equalsIgnoreCase(paramOrderDir)) {
				options.put("paramOrderDir", "DESC");
			} else {
				options.put("paramOrderDir", "ASC");
			}

			resultVO = clientLogService.getSecurityLogListPaged(options);

			HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
			fromDateHm.put("name", "fromDate");
			fromDateHm.put("value", fromDate);
			HashMap<String, Object> toDateHm = new HashMap<String, Object>();
			toDateHm.put("name", "toDate");
			toDateHm.put("value", toDate);
			resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
			resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
			resultVO.setRowLength(ObjectUtils.defaultIfNull(req.getParameter("length"), "10"));

		} catch (Exception ex) {
			logger.error("error in readSecurityLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate general log list data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readGeneralLogList")
	public @ResponseBody ResultVO readGeneralLogList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
		String toDate = StringUtils.defaultString(req.getParameter("toDate"));

		String logItem = StringUtils.defaultString(req.getParameter("logItem"));

		try {

			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -1);
				fromDate = dateFormat.format(cal.getTime());
			}

			resultVO = clientLogService.getGeneralLogList(fromDate, toDate, logItem);

		} catch (Exception ex) {
			logger.error("error in readGeneralLogList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate general log list data for paging
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readGeneralLogListPaged")
	public @ResponseBody ResultPagingVO readGeneralLogListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
			String toDate = StringUtils.defaultString(req.getParameter("toDate"));
			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -1);
				fromDate = dateFormat.format(cal.getTime());
			}

			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("fromDate", fromDate);
			options.put("toDate", toDate);
			String logItem = StringUtils.defaultString(req.getParameter("logItem"));
			if ("BROWSER".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "gooroom-browser" });
			} else if ("AGENT".equalsIgnoreCase(logItem)) {
				options.put("logItems", new String[] { "gooroom-agent" });
			} else {
				options.put("logItems", new String[] { "gooroom-browser", "gooroom-agent" });
			}

			// << paging >>
			options.put("paramStart", Integer.parseInt(ObjectUtils.defaultIfNull(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(ObjectUtils.defaultIfNull(req.getParameter("length"), "10")));

			// << order >>
			String paramOrderColumn = req.getParameter("orderColumn");
			String paramOrderDir = req.getParameter("orderDir");
			if ("CLIENT_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CLIENT_ID");
			} else if ("USER_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "USER_ID");
			} else if ("LOG_TP".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "LOG_TP");
			} else if ("LOG_VALUE".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "LOG_VALUE");
			} else {
				options.put("paramOrderColumn", "LOG_SEQ");
			}

			if ("DESC".equalsIgnoreCase(paramOrderDir)) {
				options.put("paramOrderDir", "DESC");
			} else {
				options.put("paramOrderDir", "ASC");
			}

			resultVO = clientLogService.getGeneralLogListPaged(options);

			HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
			fromDateHm.put("name", "fromDate");
			fromDateHm.put("value", fromDate);
			HashMap<String, Object> toDateHm = new HashMap<String, Object>();
			toDateHm.put("name", "toDate");
			toDateHm.put("value", toDate);
			resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
			resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
			resultVO.setRowLength(ObjectUtils.defaultIfNull(req.getParameter("length"), "10"));

		} catch (Exception ex) {
			logger.error("error in readGeneralLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete user client use hist
	 * <p>
	 * multi clients.
	 *
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/deleteUserClientUseHist")
	public @ResponseBody ResultVO deleteUserClientUseHist(@RequestParam(value = "userId", required = true) String userId,
														  @RequestParam(value = "clientIds", required = true) String clientIds) throws Exception {
		ResultVO resultVO = new ResultVO();

		HashMap<String, Object> options = new HashMap<>();
		options.put("userId", userId);
		options.put("clientIds", clientIds.split(","));

		try {
			StatusVO status = clientLogService.deleteUserClientUseHist(options);
			if (status.getResult().equals(GPMSConstants.MSG_SUCCESS)) {

				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("common.result.delete")));
			} else {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("common.result.nodelete")));
			}
		} catch (Exception ex) {
			logger.error("error in deleteUserClientUseHist : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;

	}

}
