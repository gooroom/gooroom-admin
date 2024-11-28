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

package kr.gooroom.gpms.client.controller;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

/**
 * Handles requests for the client management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientController {

	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

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
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * change client information(properties).
	 * <p>
	 * changeable property is client name and client comment.
	 * 
	 * @param paramVO ClientVO from web browser
	 * @param model   ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/updateClientInfo")
	public @ResponseBody ResultVO updateClientInfo(@ModelAttribute("paramVO") ClientVO paramVO, ModelMap model) {

		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientService.updateClientInfo(paramVO);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in updateClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client data list.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientList")
	public @ResponseBody ResultPagingVO readClientList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;

		String clientType = req.getParameter("clientType");
		String groupId = req.getParameter("groupId");
		String searchKey = req.getParameter("searchKey");

		HashMap<String, Object> options = new HashMap<>();

		if ("REVOKED".equalsIgnoreCase(clientType)) {
			// revoked client list
			options.put("clientType", GPMSConstants.STS_REVOKED);
			options.put("clientSecure", "0");
		} else if ("SECURE".equalsIgnoreCase(clientType)) {
			// protect attacked client list
			options.put("clientSecure", "1");
		} else if ("ONLINE".equalsIgnoreCase(clientType)) {
			// online client list
			options.put("isOnline", "1");
		} else {
			// normal client list
			options.put("clientType", GPMSConstants.STS_USABLE);
			options.put("clientSecure", "0");
		}

		// grouped client list
		if (groupId == null || groupId.length() < 1) {
			options.put("groupId", "");
		} else {
			String[] groups = groupId.split(",");
			for (String group : groups) {
				if ((GPMSConstants.DEFAULT_GROUPID).equals(group)) {
					options.put("hasDefaultGroup", "Y");
				}
			}
			options.put("groupId", groups);
		}

		// search keyword
		options.put("searchKey", searchKey);

		String paramOrderColumn = req.getParameter("columns[" + req.getParameter("order[0][column]") + "][data]");
		String paramOrderDir = req.getParameter("order[0][dir]");
		String paramStart = req.getParameter("start");
		String paramLength = req.getParameter("length");
		String paramDraw = req.getParameter("draw");
		if (paramDraw == null || "".equals(paramDraw)) {
			paramOrderColumn = "clientGroupName";
			paramOrderDir = "desc";
		}

		if ("clientStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.STATUS_CD");

		} else if ("2".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "IS_PROTECTOR");

		} else if ("isOn".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "ISON");

		} else if ("clientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.CLIENT_ID");

		} else if ("clientName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.CLIENT_NM");

		} else if ("clientGroupName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CGM.GRP_NM");

		} else if ("regDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.REG_DT");

		} else if ("totalCnt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TOTAL_CNT");

		} else if ("updateTargetCnt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UPDATE_TARGET_CNT");

		} else if ("loginId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LOGIN_ID");

		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientService.getClientList(options);
			resultVO.setDraw(paramDraw);

		} catch (Exception ex) {
			logger.error("error in readClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate client data list.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientListPaged")
	public @ResponseBody ResultPagingVO readClientListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<>();

		// << options >>
		String clientType = req.getParameter("clientType");
		String viewType = req.getParameter("viewType");
		String groupId = req.getParameter("groupId");
		String userId = req.getParameter("userId");
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");

		options.put("userId", StringUtils.isEmpty(userId) ? null : userId);

		if ("REVOKED".equalsIgnoreCase(clientType)) {
			// revoked client list
			options.put("clientType", GPMSConstants.STS_REVOKED);
			// options.put("clientSecure", "0");
		} else if ("SECURE".equalsIgnoreCase(clientType)) {
			// protect attacked client list
			options.put("clientSecure", "1");
		} else if ("ONLINE".equalsIgnoreCase(clientType)) {
			// online client list
			options.put("isOnline", "1");
		} else if ("ALL".equalsIgnoreCase(clientType)) {
			// online client list - all
		} else {
			// normal client list
			options.put("clientType", GPMSConstants.STS_USABLE);
			options.put("clientSecure", "0");
		}

		//viewType
		if("NORMAL".equalsIgnoreCase(viewType)) {
			options.put("viewType", "0");
		} else if("VIOLATED".equalsIgnoreCase(viewType)) {
			options.put("viewType", "1");
		} else { //ALL
			options.put("viewType", "2");
		}

		// grouped client list
		if (groupId == null || groupId.length() < 1) {
			options.put("groupId", "");
		} else {
			String[] groups = groupId.split(",");
			options.put("groupId", groups);
		}

		// search keyword
		options.put("searchKey", searchKey);

		// << paging >>
		String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
		String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");

		if ("CNT_VIOLATED".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn",
					"(CSS.BOOT_PROTECTOR_CNT + CSS.EXE_PROTECTOR_CNT + CSS.OS_PROTECTOR_CNT + CSS.MEDIA_PROTECTOR_CNT)");
		} else if ("STATUS_CD".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "STATUS_CD");
		} else if ("CLIENT_NM".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_NM");
		} else if ("CLIENT_ID".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_ID");
		} else if ("LOGIN_ID".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LOGIN_ID");
		} else if ("GROUP_NAME".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GROUP_NAME");
		} else if ("LAST_LOGIN_TIME".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LAST_LOGIN_TIME");
		} else if ("CLIENT_IP".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_IP");
		} else if ("TOTAL_CNT".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TOTAL_CNT");
		} else if ("UPDATE_TARGET_CNT".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UPDATE_TARGET_CNT");
		} else {
			options.put("paramOrderColumn", "CLIENT_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {

			resultVO = clientService.getClientList(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readClientListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate client data list in selected group
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientListForGroups")
	public @ResponseBody ResultPagingVO readClientListForGroups(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();

		// selected group id
		String groupId = req.getParameter("groupId");
		HashMap<String, Object> options = new HashMap<>();

		if (groupId == null || groupId.length() < 1) {
			options.put("groupId", "");
		} else {
			String[] groups = groupId.split(",");
			for (String group : groups) {
				if ((GPMSConstants.DEFAULT_GROUPID).equals(group)) {
					options.put("hasDefaultGroup", "Y");
				}
			}
			options.put("groupId", groups);
		}

		try {
			resultVO = clientService.selectClientListForGroups(options);
		} catch (Exception ex) {
			logger.error("error in readClientListForGroups : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readOnlineClientList")
	public @ResponseBody ResultPagingVO readOnlineClientList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();

		HashMap<String, Object> options = new HashMap<>();
		options.put("isOnline", "1");

		String paramOrderColumn = req.getParameter("columns[" + req.getParameter("order[0][column]") + "][data]");
		String paramOrderDir = req.getParameter("order[0][dir]");
		String paramStart = req.getParameter("start");
		String paramLength = req.getParameter("length");
		String paramDraw = req.getParameter("draw");

		if ("clientStatus".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.STATUS_CD");

		} else if ("2".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "IS_PROTECTOR");

		} else if ("isOn".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "ISON");

		} else if ("clientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.CLIENT_ID");

		} else if ("clientName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.CLIENT_NM");

		} else if ("clientGroupName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CGM.GRP_NM");

		} else if ("regDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "T.REG_DT");

		} else if ("totalCnt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "TOTAL_CNT");

		} else if ("updateTargetCnt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UPDATE_TARGET_CNT");

		} else if ("updateMainOsCnt".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "UPDATE_MAINOS_CNT");

		} else if ("loginId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "LOGIN_ID");

		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientService.getClientList(options);
			resultVO.setDraw(paramDraw);

		} catch (Exception ex) {
			logger.error("error in readOnlineClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientStatusForDashboard")
	public @ResponseBody ResultVO readClientStatusForDashboard(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = clientService.getClientStatusSummary();
		} catch (Exception ex) {
			logger.error("error in readClientStatusForDashboard : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data for insert to group
	 * 
	 * @param groupId String selected group id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientListForAddingGroup")
	public @ResponseBody ResultVO readClientListForAddingGroup(
			@RequestParam(value = "groupId") String groupId) {

		ResultVO resultVO = new ResultVO();
		try {

			resultVO = clientService.getClientListForAddingGroup(groupId);

		} catch (Exception ex) {
			logger.error("error in readClientListForAddingGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate selected client information data
	 * 
	 * @param clientId String selected client id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/readClientInfo")
	public @ResponseBody ResultVO readClientInfo(@RequestParam(value = "clientId") String clientId) {

		ResultVO resultVO = new ResultVO();
		try {

			resultVO = clientService.selectClientInfo(clientId);

		} catch (Exception ex) {
			logger.error("error in readClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * check for duplicate client id.
	 * 
	 * @param clientId String selected client id
	 * @return ResultVO result data bean
	 * 
	 */
	@PostMapping(value = "/isDupClientId")
	public @ResponseBody ResultVO isDupClientId(@RequestParam(value = "clientId") String clientId) {

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = clientService.isExistClientId(clientId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in isDupClientId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client status list
	 * <p>
	 * json format, use combobox.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return pageJsonView json string
	 * 
	 */
	@PostMapping(value = "/readClientStatusList")
	public String readClientStatusList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		StatusVO statusVO = new StatusVO();
		statusVO.setResult(GPMSConstants.MSG_SUCCESS);

		ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

		HashMap<String, String> hm = new HashMap<>();
		hm.put("statusCode", GPMSConstants.STS_INITIALIZE);
		hm.put("statusName", MessageSourceHelper.getMessage("client.status.init"));
		resultList.add(hm);
		hm = new HashMap<>();
		hm.put("statusCode", GPMSConstants.STS_USABLE);
		hm.put("statusName", MessageSourceHelper.getMessage("client.status.use"));
		resultList.add(hm);
		hm = new HashMap<>();
		hm.put("statusCode", GPMSConstants.STS_REVOKED);
		hm.put("statusName", MessageSourceHelper.getMessage("client.status.revoked"));
		resultList.add(hm);
		hm = new HashMap<>();
		hm.put("statusCode", GPMSConstants.STS_EXPIRE);
		hm.put("statusName", MessageSourceHelper.getMessage("client.status.expire"));
		resultList.add(hm);

		model.addAttribute("status", statusVO);
		model.addAttribute("data", resultList);

		return "pageJsonView";
	}

	/**
	 * revoke client certificate and update client status data
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/updateClientCertToRevoke")
	public @ResponseBody ResultVO updateClientCertToRevoke(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		String clientId = req.getParameter("clientId");

		ResultVO resultVO = new ResultVO();
		try {

			StatusVO status = clientService.revokeClientCertificate(clientId);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateClientCertToRevoke : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;

	}

	/**
	 * revoke client certificates and update client status data
	 * <p>
	 * multi clients.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/deleteClientsCertToRevoke")
	public @ResponseBody ResultVO deleteClientsCertToRevoke(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		String clientIds = req.getParameter("clientIds");

		ResultVO resultVO = new ResultVO();

		String[] clients = clientIds.split(",");
		int totalCnt = clients.length;
		int successCnt = 0;
		int failCnt = 0;
		for (String client : clients) {

			try {
				StatusVO status = clientService.revokeClientCertificate(client);
				// compare result
				if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(status.getResult())) {
					successCnt++;
				} else {
					failCnt++;
				}
			} catch (Exception e) {
				failCnt++;
			}
		}

		// create result
		if (totalCnt == successCnt) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
					MessageSourceHelper.getMessage("client.result.deleted")));
		} else if (successCnt == 0) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
					MessageSourceHelper.getMessage("client.result.nodeleted")));
		} else if (successCnt > 0 && failCnt > 0) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
					MessageSourceHelper.getMessage("client.result.subdeleted")));
		}

		return resultVO;

	}

	/**
	 * generate client list data in group specified.
	 * 
	 * @param groupId string group id
	 * @param model   ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readClientListInGroup")
	public @ResponseBody ResultVO readClientListInGroup(
			@RequestParam(value = "groupId", required = false) String groupId, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			if (groupId == null) {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
						MessageSourceHelper.getMessage("client.result.noclientingroup")));

			} else {
				resultVO = clientService.getClientListInGroup(groupId);
			}

		} catch (Exception ex) {
			logger.error("error in readClientListInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data that attacked risk.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readViolatedClientList")
	public @ResponseBody ResultPagingVO readViolatedClientList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<>();
		try {

			// << options >>
			String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
			// search keyword
			options.put("searchKey", searchKey);

			// << paging >>
			String paramOrderColumn = req.getParameter("orderColumn");
			String paramOrderDir = req.getParameter("orderDir");
			String paramStart = ObjectUtils.defaultIfNull(req.getParameter("start"), "0");
			String paramLength = ObjectUtils.defaultIfNull(req.getParameter("length"), "10");

			if ("chAAAAA".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CLIENT_ID");
			} else if ("chBBBBBB".equalsIgnoreCase(paramOrderColumn)) {
				options.put("paramOrderColumn", "CLIENT_NM");
			} else {
				options.put("paramOrderColumn", "CLIENT_ID");
			}

			if ("DESC".equalsIgnoreCase(paramOrderDir)) {
				options.put("paramOrderDir", "DESC");
			} else {
				options.put("paramOrderDir", "ASC");
			}
			options.put("paramStart", Integer.parseInt(paramStart));
			options.put("paramLength", Integer.parseInt(paramLength));

			resultVO = clientService.getViolatedClientList(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readViolatedClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate client count that attacked risk.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readViolatedClientCount")
	public @ResponseBody ResultVO readViolatedClientCount(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = clientService.getViolatedClientCount();
		} catch (Exception ex) {
			logger.error("error in readViolatedClientCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * create job for reset client status that attacked risk.
	 * 
	 * @param clientId string client id
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/createResetViolatedClient")
	public @ResponseBody ResultVO createResetViolatedClient(
			@RequestParam(value = "clientId", required = false) String clientId) {

		ResultVO resultVO = new ResultVO();

		try {

			// create Job
			Job[] jobs = new Job[1];
			jobs[0] = Job.generateJob("log", "clear_security_alarm");

			String jsonStr = "";
			try (StringWriter outputWriter = new StringWriter()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientController.createResetViolatedClient (make json) Exception occurred. ", jsonex);
			}

			JobVO jobVO = new JobVO();
			jobVO.setJobData(jsonStr);
			jobVO.setJobName("clear_security_alarm");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// register target
			String[] clientArray = new String[1];
			clientArray[0] = clientId;

			jobVO.setClientIds(clientArray);

			StatusVO status = jobService.createJob(jobVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createResetViolatedClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data that need package update.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readUpdatePackageClientList")
	public @ResponseBody ResultVO readUpdatePackageClientList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = clientService.getUpdatePackageClientList();

		} catch (Exception ex) {
			logger.error("error in readUpdatePackageClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

}
