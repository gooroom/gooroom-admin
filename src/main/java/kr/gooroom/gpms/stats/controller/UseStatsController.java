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

package kr.gooroom.gpms.stats.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.UseStatsService;

/**
 * Handles requests for the amount used data management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class UseStatsController {

	private static final Logger logger = LoggerFactory.getLogger(UseStatsController.class);

	@Resource(name = "useStatsService")
	private UseStatsService useStatsService;

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
	 * response daily count for login job by date period.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readLoginDailyCount")
	public @ResponseBody ResultVO readLoginDailyCount(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();

		String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
		String toDate = StringUtils.defaultString(req.getParameter("toDate"));

		try {

			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -10);
				fromDate = dateFormat.format(cal.getTime());
			}

			resultVO = useStatsService.getLoginDailyCount(fromDate, toDate);

			HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
			fromDateHm.put("name", "fromDate");
			fromDateHm.put("value", fromDate);
			HashMap<String, Object> toDateHm = new HashMap<String, Object>();
			toDateHm.put("name", "toDate");
			toDateHm.put("value", toDate);
			resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

		} catch (Exception ex) {
			logger.error("error in readLoginDailyCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response daily count for login job by specified date.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readLoginList")
	public @ResponseBody ResultVO readLoginList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String searchType = StringUtils.defaultString(req.getParameter("searchType"));
		String searchDate = StringUtils.defaultString(req.getParameter("searchDate"));
		try {
			resultVO = useStatsService.getLoginList(searchType, searchDate);
		} catch (Exception ex) {
			logger.error("error in readLoginList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response daily count for login job by specified date and paged.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readLoginListPaged")
	public @ResponseBody ResultPagingVO readLoginListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("searchDate", StringUtils.defaultString(req.getParameter("searchDate")));

			// << paging >>
			options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

			// << order >>
			String paramOrderColumn = req.getParameter("orderColumn");
			String paramOrderDir = req.getParameter("orderDir");
			if ("GRP_NM".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CGM.GRP_NM");
			} else if ("CLIENT_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "ULH.CLIENT_ID");
			} else if ("DEPT_NM".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "DM.DEPT_NM");
			} else if ("USER_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "ULH.USER_ID");
			} else if ("RESPONSE_CD".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "ULH.RESPONSE_CD");
			} else {
				options.put("paramOrderColumn", "ULH.HIST_SEQ");
			}
			options.put("paramOrderDir", paramOrderDir);

			resultVO = useStatsService.getLoginListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
			resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
			resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));
		} catch (Exception ex) {
			logger.error("error in readLoginListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response daily create client count and revoke count by date period.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientMngCount")
	public @ResponseBody ResultVO readClientMngCount(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();

		String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
		String toDate = StringUtils.defaultString(req.getParameter("toDate"));

		try {

			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -10);
				fromDate = dateFormat.format(cal.getTime());
			}

			resultVO = useStatsService.getClientMngCount(fromDate, toDate);

			HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
			fromDateHm.put("name", "fromDate");
			fromDateHm.put("value", fromDate);
			HashMap<String, Object> toDateHm = new HashMap<String, Object>();
			toDateHm.put("name", "toDate");
			toDateHm.put("value", toDate);
			resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

		} catch (Exception ex) {
			logger.error("error in readClientMngCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client list data paged
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientMngListPaged")
	public @ResponseBody ResultPagingVO readClientMngListPaged(HttpServletRequest req) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("searchType", StringUtils.defaultString(req.getParameter("searchType")));
			options.put("searchDate", StringUtils.defaultString(req.getParameter("searchDate")));

			// << paging >>
			options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

			// << order >>
			String paramOrderColumn = req.getParameter("orderColumn");
			String paramOrderDir = req.getParameter("orderDir");
			if ("CLIENT_ID".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CMH.CLIENT_ID");
			} else if ("CLIENT_NM".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "T.CLIENT_NM");
			} else if ("GRP_NM".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CGM.GRP_NM");
			} else {
				options.put("paramOrderColumn", "CMH.CLIENT_ID");
			}
			options.put("paramOrderDir", paramOrderDir);

			resultVO = useStatsService.getClientMngListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
			resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
			resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));
		} catch (Exception ex) {
			logger.error("error in readClientMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
