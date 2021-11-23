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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.gooroom.gpms.common.service.ExcelCommonService;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.dept.service.DeptService;
import kr.gooroom.gpms.dept.service.DeptVO;
import kr.gooroom.gpms.user.service.UserReqService;
import kr.gooroom.gpms.user.service.UserReqVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;

/**
 * Handles requests for the user management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "userReqService")
	private UserReqService userReqService;

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "deptService")
	private DeptService deptService;

	@Resource(name = "excelCommonService")
	private ExcelCommonService excelCommonService;

	@Inject
	private CustomJobMaker jobMaker;

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
	 * response user information list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUserList")
	public @ResponseBody ResultVO readUserList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = userService.readUserList();

		} catch (Exception ex) {
			logger.error("error in readUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response user information list data
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO
	 */
	@PostMapping(value = "/readUserListPaged")
	public @ResponseBody ResultVO readUserListPaged(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("status", req.getParameter("status"));

		// << paging >>
		options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
		options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chUserId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.USER_ID");
		} else if ("chUserNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.USER_NM");
		} else if ("chStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.STATUS");
		} else if ("chLastLoginDt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.LAST_LOGIN_DT");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.REG_DT");
		} else {
			options.put("paramOrderColumn", "UM.USER_NM");
		}
		options.put("paramOrderDir", paramOrderDir);

		try {
			resultVO = userService.getUserListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength((String) options.get("paramLength"));
		} catch (Exception ex) {
			logger.error("error in readUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * check duplicate user id data
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/isExistUserId")
	public @ResponseBody ResultVO isExistUserId(@RequestParam(value = "userId", required = true) String userId) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = userService.isNoExistUserId(userId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in isExistUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new user data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createUser")
	public @ResponseBody ResultVO createUser(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();

		UserVO vo = new UserVO();
		vo.setUserId(req.getParameter("userId"));
		vo.setUserNm(req.getParameter("userNm"));
		vo.setUserPasswd(req.getParameter("userPasswd"));

		try {
			// check duplicate
			StatusVO dupStatus = userService.isNoExistUserId(vo.getUserId());
			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {
				StatusVO status = userService.createUserData(vo, false);
				resultVO.setStatus(status);
			} else {
				resultVO.setStatus(dupStatus);
			}
		} catch (Exception ex) {
			logger.error("error in createUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new user data with rule info
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createUserWithRule")
	public @ResponseBody ResultVO createUserWithRule(@ModelAttribute("paramVO") UserVO userVO) {

		ResultVO resultVO = new ResultVO();
		try {
			// check duplicate USERID
			StatusVO dupStatus = userService.isNoExistUserId(userVO.getUserId());
			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {
				StatusVO status = userService.createUserDataWithRule(userVO);
				resultVO.setStatus(status);
			} else {
				// DUPLICATE USER ID - NO CREATE
				resultVO.setStatus(dupStatus);
			}

		} catch (Exception ex) {
			logger.error("error in createUserWithRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

//    /**
//     * append user in organization.
//     * 
//     * @param req HttpServletRequest
//     * @return ResultVO result data bean
//     *
//     */
//    @PostMapping(value = "/createUserInDept")
//    public @ResponseBody ResultVO createUserInDept(HttpServletRequest req) {
//
//	ResultVO resultVO = new ResultVO();
//
//	UserVO vo = new UserVO();
//
//	vo.setDeptCd(req.getParameter("deptCd"));
//	vo.setUserId(req.getParameter("userId"));
//	vo.setUserNm(req.getParameter("userNm"));
//	vo.setUserPasswd(req.getParameter("userPasswd"));
//
//	try {
//
//	    // check duplicate
//	    StatusVO dupStatus = userService.isNoExistUserId(vo.getUserId());
//
//	    if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {
//
//		StatusVO status = userService.createUserData(vo);
//		resultVO.setStatus(status);
//
//	    } else {
//
//		resultVO.setStatus(dupStatus);
//	    }
//
//	} catch (Exception ex) {
//	    logger.error("error in createUserInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
//		    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
//	    if (resultVO != null) {
//		resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
//			MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
//	    }
//	}
//
//	return resultVO;
//    }

	/**
	 * modify user data
	 * 
	 * @param paramVO UserVO
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateUserData")
	public @ResponseBody ResultVO updateUserData(@ModelAttribute("paramVO") UserVO paramVO) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = userService.updateUserData(paramVO);
			resultVO.setStatus(status);

			// create job
			jobMaker.createJobForUseRuleByChangeUser(paramVO.getUserId());

		} catch (Exception ex) {
			logger.error("error in updateUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * reset user login trial count
	 * 
	 * @param paramVO UserVO
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateUserLoginTrialCount")
	public @ResponseBody ResultVO updateUserLoginTrialCount(@RequestParam(value = "userId", required = true) String userId) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = userService.updateUserLoginTrialCount(userId);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateUserLoginTrialCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete user data
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteUserData")
	public @ResponseBody ResultVO deleteUserData(@RequestParam(value = "userId", required = true) String userId) {
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = userService.deleteUserData(userId);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in deleteUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response user information data
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUserData")
	public @ResponseBody ResultVO readUserData(@RequestParam(value = "userId", required = true) String userId) {

		ResultVO resultVO = new ResultVO();
		try {
			resultVO = userService.readUserData(userId);
		} catch (Exception ex) {
			logger.error("error in readUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response user list data in organization
	 * 
	 * @param deptCd String organization id
	 * @return Result4VO result data bean
	 *
	 */
	@PostMapping(value = "/readUserListInDept")
	public @ResponseBody ResultVO readUserListInDept(@RequestParam(value = "deptCd", required = true) String deptCd,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			if ("".equals(StringUtils.defaultString(deptCd))) {
				Object[] o = new Object[0];
				resultVO.setData(o);
			} else {
				resultVO = userService.getUserListInDept(deptCd);
			}
		} catch (Exception ex) {
			logger.error("error in readUserListInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response user information list data
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO
	 */
	@PostMapping(value = "/readUserListPagedInDept")
	public @ResponseBody ResultVO readUserListPagedInDept(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		options.put("searchKey", searchKey);
		options.put("status", req.getParameter("status"));
		
		String deptCd = req.getParameter("deptCd");
		if (deptCd == null || deptCd.length() < 1) {
			options.put("deptCd", "");
		} else {
			String[] deptCds = deptCd.split(",");
			options.put("deptCd", deptCds);
		}

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chUserId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.USER_ID");
		} else if ("chUserNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.USER_NM");
		} else if ("chStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.STATUS");
		} else if ("chLastLoginDt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.LAST_LOGIN_DT");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.REG_DT");
		} else if ("chLastClientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UM.CLIENT_ID");
		} else if ("chDeptNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DM.DEPT_NM");
		} else {
			options.put("paramOrderColumn", "UM.USER_NM");
		}
		options.put("paramOrderDir", paramOrderDir);

		try {
			resultVO = userService.getUserListPagedInDept(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response user list data in online
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUserListInOnline")
	public @ResponseBody ResultVO readUserListInOnline() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = userService.getUserListInOnline();

		} catch (Exception ex) {
			logger.error("error in readUserListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * insert into user(s) data in organization
	 * 
	 * @param deptCd String organization id
	 * @param users  String user id data that separate comma
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createUsersInDept")
	public @ResponseBody ResultVO createUsersInDept(@RequestParam(value = "deptCd", required = true) String deptCd,
			@RequestParam(value = "users", required = true) String users, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			String[] user_list = users.split(",");

			StatusVO status = userService.createUsersInDept(deptCd, user_list);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// create job
				jobMaker.createJobForUseRuleByChangeDept(user_list);
			}
		} catch (Exception ex) {
			logger.error("error in createUsersInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete user data from organization
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteUserInDept")
	public @ResponseBody ResultVO deleteUserInDept(@RequestParam(value = "userId", required = true) String userId,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			String[] user_list = new String[1];
			user_list[0] = userId;

			StatusVO status = userService.createUsersInDept(GPMSConstants.DEFAULT_DEPTCD, user_list);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteUserInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete users data (array) from organization
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteUsersInDept")
	public @ResponseBody ResultVO deleteUsersInDept(@RequestParam(value = "users", required = true) String users,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			String[] user_list = users.split(",");

			StatusVO status = userService.createUsersInDept(GPMSConstants.DEFAULT_DEPTCD, user_list);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteUserInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
	
	
	/**
	 * create new user data from file
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createUserDataFromFile")
	public @ResponseBody ResultVO createUserDataFromFile(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();
		
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) req;
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
		
		if (iterator.hasNext()) {
			
			String fileName = (String) iterator.next();
			MultipartFile multipartFile = multipartHttpServletRequest.getFile(fileName);

			List<UserVO> userList = new ArrayList<UserVO>();
			List<List<String>> dataList = null;
			try {

				// read data to List<List<String>> from file
				dataList = excelCommonService.read(multipartFile, GPMSConstants.RULE_GRADE_USER);
				if(dataList == null) {
					if (resultVO != null) {
						resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
								"읽을 수 없는 데이터가 존재합니다."));
					}
					return resultVO;
				}

				resultVO = userService.isCanUpdateUserDataFromFile(dataList);
				if(resultVO.getStatus().getResult().equals(GPMSConstants.MSG_FAIL)) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							resultVO.getStatus().getMessage()));
					return resultVO;
				}
				// set deptList from resultVO
				Object[] objs = resultVO.getData();
				for(int i = 0; i < objs.length; i++) {
					UserVO vo = (UserVO) objs[i];
					userList.add(vo);
				}

				if(userList.size() > 0) {
					// insert into database
					StatusVO status = userService.updateUserDataFromFile(userList);
					resultVO.setStatus(status);
				}

			} catch (Exception ex) {

				logger.error("error in createUesrDataFromFile : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
				}
			}
			
		} else {
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						"저장할 파일이 없음"));
			}
		}

		return resultVO;
	}

	/**
	 * create(insert) new user list file by data
	 * <p>
	 * use file downloader.
	 *
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createUserFileFromData")
	public @ResponseBody void createUserFileFromData(HttpServletRequest req, HttpServletResponse res) {
		//set file name
		String fileName = "USER_gooroom_" + CommonUtils.convertDataToString(new Date());

		res.reset();
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/vnd.ms-excel");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

		try{
			XSSFWorkbook workbook = userService.createUserFileFromData();

			workbook.write(res.getOutputStream());
			workbook.close();

			res.flushBuffer();

		} catch (Exception ex) {

			logger.error("error in createUserFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
	}

	/**
	 * create(insert) new user sample file for upload
	 * <p>
	 * use file downloader.
	 *
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createUserSampleFileFromData")
	public @ResponseBody void createUserSampleFileFromData(HttpServletRequest req, HttpServletResponse res) {
		//set file name
		String fileName = "USER_SAMPLE_gooroom_" + CommonUtils.convertDataToString(new Date());

		res.reset();
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/vnd.ms-excel");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

		try{
			XSSFWorkbook workbook = userService.createUserSampleFileFromData();

			workbook.write(res.getOutputStream());
			workbook.close();

			res.flushBuffer();

		} catch (Exception ex) {

			logger.error("error in createUserSampleFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
	}

	/**
	 * get user registration(usb media) request.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUserReqList")
	public @ResponseBody ResultVO readUserReqList(HttpServletRequest req, HttpServletResponse res,
	                                                   ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = userReqService.getUserReqList(req.getParameter("userId"));
		} catch (Exception ex) {
			logger.error("error in readUserReqList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	/**
	 * get user registration(usb media) request.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUserReqListPaged")
	public @ResponseBody ResultVO readUserReqListPaged(HttpServletRequest req, HttpServletResponse res,
														  ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		// search keyword
		options.put("searchKey", searchKey);

		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");

		if("chReqSeq".equals(paramOrderColumn)) {
			options.put("paramOrderColumn", "REQ_SEQ");
		} else if ("chUserId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "USER_ID");
		} else if ("chReqDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_DT");
		} else if ("chActionType".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "ACTION_TYPE");
		} else if ("chAction".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "ADMIN_CHECK");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "MOD_DT");
		}

		options.put("paramOrderDir", paramOrderDir);
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = userReqService.getUserReqListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);
		} catch (Exception ex) {
			logger.error("error in readUserReqListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
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
	@PostMapping(value = "/readUserReqActListPaged")
	public @ResponseBody ResultVO readUserReqActListPaged(HttpServletRequest req, HttpServletResponse res,
	                                                    ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		try {
			// << options >>
			String reqSeq = StringUtils.defaultString(req.getParameter("reqSeq"));
			String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
			String toDate = StringUtils.defaultString(req.getParameter("toDate"));
			if ("".equals(fromDate) || "".equals(toDate)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				toDate = dateFormat.format(cal.getTime());
				cal.add(Calendar.DATE, -20);
				fromDate = dateFormat.format(cal.getTime());
			}

			options.put("reqSeq", reqSeq);
			UserReqVO userReqVO = (UserReqVO) userReqService.getUserReqData(reqSeq).getData()[0];
			options.put("userId", userReqVO.getUserId());
			options.put("usbSerialNo", userReqVO.getUsbSerialNo());

			options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));
			options.put("fromDate", fromDate);
			options.put("toDate", toDate);

			// << paging >>
			options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
			options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

			// << order >>
			options.put("paramOrderColumn", "REQ_SEQ");
			options.put("paramOrderDir", StringUtils.defaultString(req.getParameter("orderDir")));

			resultVO = userReqService.getUserReqActListPaged(options);

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
			logger.error("error in readUserReqActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * 사용자 USB 등록/삭제 요청 승인 - array
	 *
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/approvalUserReq")
	public @ResponseBody ResultVO approvalUserReq(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String[] reqSeqs = req.getParameterValues("reqSeqs[]");
		ResultVO resultVO = new ResultVO();

		try {
			StatusVO status = userReqService.approvalUserReq(reqSeqs);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * 사용자 USB 등록/삭제 요청 반려 - array
	 *
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/denyUserReq")
	public @ResponseBody ResultVO denyUserReq(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String[] reqSeqs = req.getParameterValues("reqSeqs[]");
		ResultVO resultVO = new ResultVO();

		try {
			StatusVO status = userReqService.denyUserReq(reqSeqs);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * usb 권한 회수
	 *
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/revokeUsbPermissionFromAdmin")
	public @ResponseBody ResultVO revokeUsbPermissionFromAdmin(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String reqSeq = req.getParameter("reqSeq");
		ResultVO resultVO = new ResultVO();

		try {
			  StatusVO status = userReqService.revokeUsbPermissionFromAdmin(reqSeq);
			  resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * current user data
	 *
	 * @return ResultVO
	 */
	@PostMapping(value = "/readCurrentUserData")
	public @ResponseBody ResultVO readCurrentUserData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO;

		try {
			resultVO = userService.readUserData(LoginInfoHelper.getUserId());
		} catch (Exception ex) {
			resultVO = new ResultVO();
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}
}