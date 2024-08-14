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

package kr.gooroom.gpms.job.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
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
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * Handles requests for the gooroom job management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class JobController {

	private static final Logger logger = LoggerFactory.getLogger(JobController.class);

	@Resource(name = "jobService")
	private JobService jobService;

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
	 * cancel gooroom job.
	 * 
	 * @param paramVO JobVO job data bean.
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/updateJobToCancel")
	public @ResponseBody ResultVO updateJobToCancel(@ModelAttribute("paramVO") JobVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = jobService.setJobToCancel(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateJobToCancel : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom job list data.
	 * 
	 * @param job_status string status of job.
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/readJobList")
	public @ResponseBody ResultVO readJobList(@RequestParam(value = "job_status", required = false) String job_status) {

		ResultVO resultVO = new ResultVO();

		if (job_status == null) {
			job_status = "";
		}

		try {

			resultVO = jobService.getJobList(job_status);
		} catch (Exception ex) {
			logger.error("error in readJobList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * response gooroom job list data for paging.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO
	 *
	 */
	@PostMapping(value = "/readJobListPaged")
	public @ResponseBody ResultPagingVO readJobListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		String jobStatus = req.getParameter("jobStatus");

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));

		// grouped client list
		if (jobStatus == null || jobStatus.length() < 1) {
			options.put("jobStatus", "");
		} else {
			String[] statusList = jobStatus.split(",");
			options.put("jobStatus", statusList);
		}
		options.put("jobType", ((req.getParameter("jobType") != null ? req.getParameter("jobType") : "")));

		// << paging >>
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chJobNo".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "JOB_NO");
		} else if ("chJobName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "JOB_NM");
		} else if ("chReadyCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "READY_COUNT");
		} else if ("chClientCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_COUNT");
		} else if ("chErrorCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "ERROR_COUNT");
		} else if ("chCompCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "COMP_COUNT");
		} else if ("chRegUserId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_USER_ID");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_DT");
		} else {
			options.put("paramOrderColumn", "JOB_NO");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {

			resultVO = jobService.getJobListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readJobListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get job information data.
	 * 
	 * @param jobNo string job number.
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/readJobInfo")
	public @ResponseBody ResultVO readJobInfo(@RequestParam(value = "jobNo", required = true) String jobNo) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = jobService.getJobInfo(jobNo);
		} catch (Exception ex) {
			logger.error("error in readJobInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get gooroom job list data.
	 * 
	 * @param jobNo string job number.
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/readClientListInJob")
	public @ResponseBody ResultVO readClientListInJob(@RequestParam(value = "jobNo", required = true) String jobNo) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = jobService.readClientListInJob(jobNo);
		} catch (Exception ex) {

			logger.error("error in readClientListInJob : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get gooroom job list data for paging.
	 * 
	 * @param jobNo string job number.
	 * @return ResultVO
	 *
	 */
	@PostMapping(value = "/readClientListInJobPaged")
	public @ResponseBody ResultPagingVO readClientListInJobPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>

		options.put("jobNo", req.getParameter("jobNo"));
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("jobStatus", req.getParameter("jobStatus"));

		// << paging >>
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chClientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_ID");
		} else if ("chGroupName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GRP_NM");
		} else if ("chJobStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "JOB_STAT");
		} else {
			options.put("paramOrderColumn", "CLIENT_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {

			resultVO = jobService.getClientListInJobPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readClientListInJobPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

}
