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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import kr.gooroom.gpms.user.service.AdminUserService;
import kr.gooroom.gpms.user.service.AdminUserVO;

/**
 * Handles requests for the administrator user management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class AdminUserController {

	private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

	@Resource(name = "adminUserService")
	private AdminUserService adminUserService;

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;

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
	 * response administrator user information list data
	 * 
	 * @return ResultVO result data bean
	 */
	@PostMapping(value = "/readAdminUserList")
	public @ResponseBody ResultVO readAdminUserList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = adminUserService.readAdminUserList();
		} catch (Exception ex) {
			logger.error("error in readAdminUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response administrator user information list data for paging
	 * 
	 * @return ResultVO result data bean
	 */
	@PostMapping(value = "/readAdminUserListPaged")
	public @ResponseBody ResultPagingVO readAdminUserListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("adminType", req.getParameter("adminType"));
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("status", req.getParameter("status"));

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chAdminId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "AM.ADMIN_ID");
		} else if ("chAdminNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "AM.ADMIN_NM");
		} else if ("chStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "AM.STATUS_CD");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "AM.REG_DT");
		} else {
			options.put("paramOrderColumn", "AM.ADMIN_NM");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = adminUserService.getAdminUserListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readAdminUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * check duplicate administrator user id
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/isExistAdminUserId")
	public @ResponseBody ResultVO isExistAdminUserId(@RequestParam(value = "adminId", required = true) String adminId) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = adminUserService.isExistAdminUserId(adminId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in isExistAdminUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new administrator user data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/createAdminUser")
	public @ResponseBody ResultVO createAdminUser(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();

		AdminUserVO paramVO = new AdminUserVO();

		paramVO.setAdminId(req.getParameter("adminId"));
		paramVO.setAdminNm(req.getParameter("adminNm"));
		paramVO.setAdminPw(req.getParameter("adminPw"));
		paramVO.setAdminTp(req.getParameter("adminTp"));

		paramVO.setIsClientAdmin(req.getParameter("isClientAdmin"));
		paramVO.setIsUserAdmin(req.getParameter("isUserAdmin"));
		paramVO.setIsDesktopAdmin(req.getParameter("isDesktopAdmin"));
		paramVO.setIsNoticeAdmin(req.getParameter("isNoticeAdmin"));
		paramVO.setIsPortableAdmin(req.getParameter("isPortableAdmin"));

		// array : conect ip
		String[] connIps = req.getParameterValues("connIps[]");
		if (connIps != null && connIps.length > 0) {
			paramVO.setConnIps(new ArrayList<>(Arrays.asList(connIps)));
		}

		// array : client group id
		String[] grpInfoList = req.getParameterValues("grpInfoList[][value]");
		ArrayList<String> grpList = new ArrayList<String>();
		if (grpInfoList != null && grpInfoList.length > 0) {
			grpList = new ArrayList<>(Arrays.asList(grpInfoList));
		}
		grpList.add(GPMSConstants.DEFAULT_GROUPID);
		paramVO.setGrpIds(grpList);

		// array : dept cd
		String[] deptInfoList = req.getParameterValues("deptInfoList[][value]");
		ArrayList<String> deptList = new ArrayList<String>();
		if (deptInfoList != null && deptInfoList.length > 0) {
			deptList = new ArrayList<>(Arrays.asList(deptInfoList));
		}
		deptList.add(GPMSConstants.DEFAULT_DEPTCD);
		paramVO.setDeptCds(deptList);

		try {

			// check duplicate
			StatusVO dupStatus = adminUserService.isExistAdminUserId(paramVO.getAdminId());
			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {
				StatusVO status = adminUserService.createAdminUser(paramVO);
				resultVO.setStatus(status);

			} else {

				resultVO.setStatus(dupStatus);
			}

		} catch (Exception ex) {

			logger.error("error in createAdminUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify administrator user data
	 * 
	 * @param paramVO AdminUserVO data bean
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateAdminUserData")
	public @ResponseBody ResultVO updateAdminUserData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		AdminUserVO paramVO = new AdminUserVO();
		paramVO.setAdminId(req.getParameter("adminId"));
		paramVO.setAdminNm(req.getParameter("adminNm"));
		paramVO.setAdminPw(req.getParameter("adminPw"));
		paramVO.setAdminTp(req.getParameter("adminTp"));

		paramVO.setIsClientAdmin(req.getParameter("isClientAdmin"));
		paramVO.setIsUserAdmin(req.getParameter("isUserAdmin"));
		paramVO.setIsDesktopAdmin(req.getParameter("isDesktopAdmin"));
		paramVO.setIsNoticeAdmin(req.getParameter("isNoticeAdmin"));
		paramVO.setIsPortableAdmin(req.getParameter("isPortableAdmin"));

		// array : conect ip
		String[] connIps = req.getParameterValues("connIps[]");
		if (connIps != null && connIps.length > 0) {
			paramVO.setConnIps(new ArrayList<>(Arrays.asList(connIps)));
		}

		// array : client group id
		String[] grpInfoList = req.getParameterValues("grpInfoList[][value]");
		if (grpInfoList != null && grpInfoList.length > 0) {
			paramVO.setGrpIds(new ArrayList<>(Arrays.asList(grpInfoList)));
		}

		// array : dept cd
		String[] deptInfoList = req.getParameterValues("deptInfoList[][value]");
		if (deptInfoList != null && deptInfoList.length > 0) {
			paramVO.setDeptCds(new ArrayList<>(Arrays.asList(deptInfoList)));
		}

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = adminUserService.updateAdminUserData(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify current administrator user data (pollingCycle)
	 * 
	 * @param paramVO AdminUserVO data bean
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateCurrentAdminUserData")
	public @ResponseBody ResultVO updateCurrentAdminUserData(@ModelAttribute("paramVO") AdminUserVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = adminUserService.setCurrentAdminUserData(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateCurrentAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete administrator user data
	 * 
	 * @param adminId String user id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/deleteAdminUserData")
	public @ResponseBody ResultVO deleteAdminUserData(
			@RequestParam(value = "adminId", required = true) String adminId) {

		ResultVO resultVO = new ResultVO();

		try {
			AdminUserVO vo = new AdminUserVO();
			vo.setAdminId(adminId);
			vo.setStatus(GPMSConstants.STS_DELETE_USER);

			StatusVO status = adminUserService.deleteAdminUserData(vo);
			resultVO.setStatus(status);

		} catch (Exception ex) {

			logger.error("error in deleteAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate administrator user data by user id
	 * 
	 * @param adminId String user id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readAdminUserData")
	public @ResponseBody ResultVO readAdminUserData(@RequestParam(value = "adminId", required = true) String adminId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = adminUserService.selectAdminUserData(adminId);
		} catch (Exception ex) {
			logger.error("error in readAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate current administrator user data
	 * 
	 * @param adminId String user id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readCurrentAdminUserData")
	public @ResponseBody ResultVO readCurrentAdminUserData(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = adminUserService.selectAdminUserData(LoginInfoHelper.getUserId());
		} catch (Exception ex) {
			logger.error("error in readAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate administrator user action log list data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readAdminActListPaged")
	public @ResponseBody ResultVO readAdminActListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		
		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			String adminId = StringUtils.defaultString(req.getParameter("adminId"));
			String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
			String paramOrderDir = StringUtils.defaultString(req.getParameter("orderDir"));
			String toDate = StringUtils.defaultString(req.getParameter("toDate"));
			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -20);
				fromDate = dateFormat.format(cal.getTime());
			}

			options.put("adminId", adminId);
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
			options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

			// << order >>
			options.put("paramOrderColumn", "LOG_SEQ");

			if ("DESC".equalsIgnoreCase(paramOrderDir)) {
				options.put("paramOrderDir", "DESC");
			} else {
				options.put("paramOrderDir", "ASC");
			}

			resultVO = adminUserService.getAdminActListPaged(options);

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
			resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));

		} catch (Exception ex) {
			logger.error("error in readAdminActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate administrator user action log list data Paging
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readAdminRecordListPaged")
	public @ResponseBody ResultPagingVO readAdminRecordListPaged(HttpServletRequest req) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("fromDate", req.getParameter("fromDate"));
		options.put("toDate", req.getParameter("toDate"));
		options.put("adminId", req.getParameter("adminId"));

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");

		if ("chLogSeq".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LAA.LOG_SEQ");
		} else if ("chActDt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LAA.ACT_DT");
		} else if ("chActItem".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LAA.ACT_ITEM");
		} else if ("chActTp".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LAA.ACT_TP");
		} else {
			options.put("paramOrderColumn", "LAA.LOG_SEQ");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = adminUserService.getAdminRecordListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

//	    if ("".equals(fromDate) || "".equals(toDate)) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		Calendar cal = Calendar.getInstance();
//		toDate = dateFormat.format(cal.getTime());
//		cal.add(Calendar.DATE, -3);
//		fromDate = dateFormat.format(cal.getTime());
//	    }

		} catch (Exception ex) {
			logger.error("error in readAdminRecordListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify administrator user access network information avaliable.
	 * 
	 * @param adminAddresses String array address list
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateAdminAddress")
	public @ResponseBody ResultVO createAdminAddress(
			@RequestParam(value = "adminAddresses[]", required = true) List<String> adminAddresses) {

		ResultVO resultVO = new ResultVO();

		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(GPMSConstants.CTRL_ITEM_AVAILCONNECT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
			itemVo.setObjNm("CONNADDRESS");
			itemVo.setComment("connectable address");
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_AVAILCONNECT_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_AVAILCONNECT_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			if (adminAddresses != null && adminAddresses.size() > 0) {
				for (String address : adminAddresses) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "address", address, "",
							LoginInfoHelper.getUserId()));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			ResultVO oldData = ctrlMstService
					.readCtrlItem(GPMSConstants.CTRL_ITEM_AVAILCONNECT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
			if (oldData != null && oldData.getData().length > 0) {
				StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
				resultVO.setStatus(status);
			} else {
				StatusVO status = ctrlMstService.createCtrlDefaultItem(itemVo, props);
				resultVO.setStatus(status);
			}

		} catch (Exception ex) {

			logger.error("error in createAdminAddress : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	
	/**
	 * clear administrator login trial info
	 * 
	 * @param paramVO AdminUserVO data bean
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateLoginTrialData")
	public @ResponseBody ResultVO updateLoginTrialData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = adminUserService.updateLoginTrialData();
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateLoginTrialData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
