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

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.nodes.Job;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;
import kr.gooroom.gpms.mng.service.ClientMngService;
import kr.gooroom.gpms.mng.service.ClientProfileSetVO;
import kr.gooroom.gpms.mng.service.ClientRegKeyVO;
import kr.gooroom.gpms.mng.service.ClientSoftwareVO;

/**
 * Handles requests for the theme file management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientMngController {

	private static final Logger logger = LoggerFactory.getLogger(ClientMngController.class);

	@Resource(name = "clientMngService")
	private ClientMngService clientMngService;

	@Resource(name = "clientService")
	private ClientService clientService;

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
	 * get client registration key data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readRegKeyInfoListPaged")
	public @ResponseBody ResultVO readRegKeyInfoListPaged(HttpServletRequest req, HttpServletResponse res,
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

		if ("chRegKey".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REGKEY_NO");
		} else if ("chValidDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "VALID_DT");
		} else if ("chExpireDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "EXPIRE_DT");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "MOD_DT");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REG_DT");
		} else {
			options.put("paramOrderColumn", "REGKEY_NO");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientMngService.getRegKeyList(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readRegKeyInfoListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * create client registration key data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/generateRegKeyNumber")
	public @ResponseBody ResultVO generateRegKeyNumber() {

		ResultVO resultVO = new ResultVO();
		try {

			UUID uuid_r = UUID.randomUUID();

			StatusVO statusVO = new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
					MessageSourceHelper.getMessage("system.common.selectdata"));
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("key", uuid_r.toString());
			Object[] re = new Object[] { map };
			resultVO.setResultInfo(statusVO, re);

		} catch (Exception ex) {
			logger.error("error in generateRegKeyNumber : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}
		return resultVO;
	}

	/**
	 * create(insert) new client registration key information data.
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO client registration key information.
	 * @param request        HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createRegKeyData")
	public @ResponseBody ResultVO createRegKeyData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ClientRegKeyVO clientRegKeyVO = new ClientRegKeyVO();

		clientRegKeyVO.setComment(String.valueOf(req.getParameter("comment")));
		clientRegKeyVO.setIpRange(String.valueOf(req.getParameter("ipRange")));
		clientRegKeyVO.setRegKeyNo(String.valueOf(req.getParameter("regKeyNo")));

		clientRegKeyVO.setValidDate(new Date(Long.parseLong(String.valueOf(req.getParameter("validDate")))));
		clientRegKeyVO.setExpireDate(new Date(Long.parseLong(String.valueOf(req.getParameter("expireDate")))));

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.createRegKeyData(clientRegKeyVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit(update) client registration key information data.
	 * 
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/editRegKeyData")
	public @ResponseBody ResultVO editRegKeyData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ClientRegKeyVO clientRegKeyVO = new ClientRegKeyVO();

		clientRegKeyVO.setComment(String.valueOf(req.getParameter("comment")));
		clientRegKeyVO.setIpRange(String.valueOf(req.getParameter("ipRange")));
		clientRegKeyVO.setRegKeyNo(String.valueOf(req.getParameter("regKeyNo")));

		clientRegKeyVO.setValidDate(new Date(Long.parseLong(String.valueOf(req.getParameter("validDate")))));
		clientRegKeyVO.setExpireDate(new Date(Long.parseLong(String.valueOf(req.getParameter("expireDate")))));

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = clientMngService.editRegKeyData(clientRegKeyVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in editRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete client registration key information data.
	 * 
	 * @param regKeyNo String registration key id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteRegKeyData")
	public @ResponseBody ResultVO deleteRegKeyData(@RequestParam(value = "regKeyNo", required = true) String regKeyNo) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.deleteRegKeyData(regKeyNo);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * create profile set by reference client id, start job.
	 * 
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createProfileSet")
	public @ResponseBody ResultVO createProfileSet(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ClientProfileSetVO vo = new ClientProfileSetVO();
		vo.setClientId(StringUtils.defaultString(req.getParameter("clientId"), ""));
		vo.setProfileNm(StringUtils.defaultString(req.getParameter("profileNm"), ""));
		vo.setProfileCmt(StringUtils.defaultString(req.getParameter("profileCmt"), ""));

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.createProfileSetService(vo);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createProfileSet : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * create profile job for profiling to clients.
	 * 
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createProfileJob")
	public @ResponseBody ResultVO createProfileJob(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String profileNo = StringUtils.defaultString(req.getParameter("profileNo"), "");
		String targetClientIds = StringUtils.defaultString(req.getParameter("targetClientIds"), "");
		String targetClientGroupIds = StringUtils.defaultString(req.getParameter("targetClientGroupIds"), "");
		String isRemoval = StringUtils.defaultString(req.getParameter("isRemoval"), "");

		ResultVO resultVO = new ResultVO();
		try {
			// make job
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("profile_no", profileNo);
			map.put("removal", isRemoval);

			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJobWithMap("package", "profiling", map);
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientMngController.createProfileJob (make json) Exception occurred. ", jsonex);
			} finally {
				try {
					if (outputWriter != null) {
						outputWriter.close();
					}
				} catch (Exception finalex) {
				}
			}

			JobVO jobVO = new JobVO();
			jobVO.setJobData(jsonStr);
			jobVO.setJobName("profiling");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (targetClientGroupIds != null && targetClientGroupIds.length() > 0) {
				String[] groupIdArray = targetClientGroupIds.split(",");
				for (int m = 0; m < groupIdArray.length; m++) {
					ResultVO clientResultVO = clientService.getClientListInGroup(groupIdArray[m]);
					ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
					for (int i = 0; i < clients.length; i++) {
						clientList.add(clients[i].getClientId());
					}
				}
			}
			if (targetClientIds != null && targetClientIds.length() > 0) {
				String[] clientIdArray = targetClientIds.split(",");
				for (int m = 0; m < clientIdArray.length; m++) {
					if (!(clientList.contains(clientIdArray[m]))) {
						clientList.add(clientIdArray[m]);
					}
				}
			}

			String[] clientArray = new String[clientList.size()];
			clientArray = clientList.toArray(clientArray);

			jobVO.setClientIds(clientArray);

			resultVO.setStatus(jobService.createJob(jobVO));

		} catch (Exception ex) {
			logger.error("error in createProfileJob : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get profile set data list.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readProfileSetListPaged")
	public @ResponseBody ResultPagingVO readProfileSetListPaged(HttpServletRequest req, HttpServletResponse res,
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

		// << Order >>
		if ("chProfileSetNo".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPM.PROFILE_NO");
		} else if ("chProfileSetName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPM.PROFILE_NM");
		} else if ("chClientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPM.CLIENT_ID");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPM.REG_DT");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPM.MOD_DT");
		} else {
			options.put("paramOrderColumn", "CPM.PROFILE_NO");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientMngService.getProfileSetListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readProfileSetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * generate package list data in profile set.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readProfilePackageListPaged")
	public @ResponseBody ResultPagingVO readProfilePackageListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("clientId", req.getParameter("clientId"));
		options.put("profileNo", req.getParameter("profileNo"));
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));

		// << paging >>
		options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
		options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chClientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPD.CLIENT_ID");
		} else if ("chPackageId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPD.PACKAGE_ID");
		} else if ("chPackageArch".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPD.PACKAGE_ARCH");
		} else if ("chInstallVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPD.INSTALL_VER");
		} else if ("chPackageLastVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CPD.PACKAGE_LAST_VER");
		} else {
			options.put("paramOrderColumn", "CPD.PACKAGE_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = clientMngService.getProfilePackageListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(String.valueOf(options.get("paramLength")));
		} catch (Exception ex) {
			logger.error("error in readProfilePackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete profile set data.
	 * 
	 * @param profileNo String profile set no.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteProfileSetData")
	public @ResponseBody ResultVO deleteProfileSetData(
			@RequestParam(value = "profileNo", required = true) String profileNo) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.deleteProfileSetData(profileNo);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit(update) client profile set data.
	 * 
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/editProfileSetData")
	public @ResponseBody ResultVO editProfileSetData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ClientProfileSetVO vo = new ClientProfileSetVO();

		vo.setProfileNo(StringUtils.defaultString(req.getParameter("profileNo"), ""));
		vo.setClientId(StringUtils.defaultString(req.getParameter("clientId"), ""));
		vo.setProfileNm(StringUtils.defaultString(req.getParameter("profileNm"), ""));
		vo.setProfileCmt(StringUtils.defaultString(req.getParameter("profileCmt"), ""));

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = clientMngService.editProfileSetData(vo);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in editProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * create(insert) new client software app data.
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO client registration key information.
	 * @param request        HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/createClientSoftware")
	public @ResponseBody ResultVO createClientSoftware(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ClientSoftwareVO clientSoftwareVO = new ClientSoftwareVO();

		clientSoftwareVO.setSwName(req.getParameter("swName"));
		clientSoftwareVO.setSwNameForKorean(req.getParameter("swNameForKorean"));
		clientSoftwareVO.setSwTag(req.getParameter("swTag"));

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.createClientSoftware(clientSoftwareVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete client software information data.
	 * 
	 * @param swId String software id.
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/deleteClientSoftware")
	public @ResponseBody ResultVO deleteClientSoftware(@RequestParam(value = "swId", required = true) String swId) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientMngService.deleteClientSoftware(swId);

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * edit(update) client software information data.
	 * 
	 * @param request HttpServletRequest
	 * @return ResultVO result data bean.
	 *
	 */
	@PostMapping(value = "/editClientSoftware")
	public @ResponseBody ResultVO editClientSoftware(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ClientSoftwareVO clientSoftwareVO = new ClientSoftwareVO();

		clientSoftwareVO.setSwId(req.getParameter("swId"));
		clientSoftwareVO.setSwName(req.getParameter("swName"));
		clientSoftwareVO.setSwNameForKorean(req.getParameter("swNameForKorean"));
		clientSoftwareVO.setSwTag(req.getParameter("swTag"));

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = clientMngService.editClientSoftware(clientSoftwareVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in editClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get client software list data paged
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientSoftwareListPaged")
	public @ResponseBody ResultVO readClientSoftwareListPaged(HttpServletRequest req, HttpServletResponse res,
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
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "5");

		if ("chSwId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "REGKEY_NO");
		} else if ("chValidDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "VALID_DT");
		} else if ("chExpireDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "EXPIRE_DT");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "MOD_DT");
//	} else {
//	    options.put("paramOrderColumn", "REGKEY_NO");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientMngService.getClientSoftwareList(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readClientSoftwareListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

}
