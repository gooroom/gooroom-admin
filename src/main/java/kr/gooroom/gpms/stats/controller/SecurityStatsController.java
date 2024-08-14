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

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.SecurityStatsService;

/**
 * Handles requests for the security statistics management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class SecurityStatsController {

	private static final Logger logger = LoggerFactory.getLogger(SecurityStatsController.class);

	@Resource(name = "securityStatsService")
	private SecurityStatsService securityStatsService;

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
	 * generate daily count data by date period
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readViolatedDailyCount")
	public @ResponseBody ResultVO readViolatedDailyCount(HttpServletRequest req) {

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

			resultVO = securityStatsService.getViolatedCount(fromDate, toDate, "day");

			HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
			fromDateHm.put("name", "fromDate");
			fromDateHm.put("value", fromDate);
			HashMap<String, Object> toDateHm = new HashMap<String, Object>();
			toDateHm.put("name", "toDate");
			toDateHm.put("value", toDate);
			resultVO.setExtend(new Object[] { fromDateHm, toDateHm });

		} catch (Exception ex) {
			logger.error("error in readViolatedDailyCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate protector attacked data list by specified date.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readViolatedList")
	public @ResponseBody ResultVO readViolatedList(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();

		String searchType = req.getParameter("searchType");
		String searchDate = req.getParameter("searchDate");

		try {

			resultVO = securityStatsService.getViolatedList(searchType, searchDate);

		} catch (Exception ex) {
			logger.error("error in readViolatedList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate protector attacked data list by specified date and paged.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readViolatedListPaged")
	public @ResponseBody ResultPagingVO readViolatedListPaged(HttpServletRequest req) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("searchType", StringUtils.defaultString(req.getParameter("searchType")));
			options.put("searchDate", StringUtils.defaultString(req.getParameter("searchDate")));
			options.put("defaultViolatedLogType", GPMSConstants.DEFAULT_VIOLATED_LOGTYPE);

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

			resultVO = securityStatsService.getViolatedListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
			resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
			resultVO.setRowLength(ObjectUtils.defaultIfNull(req.getParameter("length"), "10"));

		} catch (Exception ex) {
			logger.error("error in readViolatedListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate daily count data for dashboard
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readViolatedClientStatus")
	public @ResponseBody ResultVO readViolatedClientStatus(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();
		String countType = StringUtils.defaultString(req.getParameter("countType"));
		if ("".equals(countType)) {
			countType = "day";
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
//	cal.set(Calendar.YEAR, 2018);	
//	cal.set(Calendar.MONTH, 10);
//	cal.set(Calendar.DATE, 7);
		String toDate = dateFormat.format(cal.getTime());
		if ("day".equalsIgnoreCase(countType)) {
			cal.add(Calendar.DATE, -7);
		} else if ("week".equalsIgnoreCase(countType)) {
			cal.add(Calendar.WEEK_OF_YEAR, -7);
		} else if ("month".equalsIgnoreCase(countType)) {
			cal.add(Calendar.MONTH, -7);
			cal.set(Calendar.DATE, 1);
		}
		String fromDate = dateFormat.format(cal.getTime());

		try {

			resultVO = securityStatsService.getViolatedCount(fromDate, toDate, countType);

		} catch (Exception ex) {
			logger.error("error in readViolatedDailyCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
