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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.inject.Inject;
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

import kr.gooroom.gpms.client.service.ClientGroupService;
import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.job.service.JobService;

/**
 * Handles requests for the client management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientGroupController {

	private static final Logger logger = LoggerFactory.getLogger(ClientGroupController.class);

	@Resource(name = "clientGroupService")
	private ClientGroupService clientGroupService;

	@Resource(name = "jobService")
	private JobService jobService;

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;

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
	 * generate client group list data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readClientGroupList")
	public @ResponseBody ResultVO readClientGroupList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = clientGroupService.selectClientGroupList();

		} catch (Exception ex) {
			logger.error("error in readClientGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get client group list data for paging.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO
	 * 
	 */
	@PostMapping(value = "/readClientGroupListPaged")
	public @ResponseBody ResultVO readClientGroupListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		options.put("searchKey", searchKey);

		// << paging >>
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chGrpNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.GRP_NM");
		} else if ("chClientCount".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_COUNT");
		} else if ("chDesktopConfigNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "DESKTOP_CONFIG_NM");
		} else if ("chClientConfigNm".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CLIENT_CONFIG_NM");
		} else if ("chRegDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "GM.REG_DT");
		} else {
			options.put("paramOrderColumn", "GM.GRP_NM");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {

			resultVO = clientGroupService.getClientGroupListPaged(options);

			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readClientGroupListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate client group information by selected group id.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readClientGroupData")
	public @ResponseBody ResultVO readClientGroupData(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String groupId = StringUtils.defaultString(req.getParameter("groupId"));
		if ("".equals(groupId)) {
			groupId = GPMSConstants.DEFAULT_GROUPID;
		}

		try {
			resultVO = clientGroupService.readClientGroupData(groupId);
		} catch (Exception ex) {
			logger.error("error in readClientGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate client group information by selected group id list.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/readClientGroupNodeList")
	public @ResponseBody ResultVO readClientGroupNodeList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		
		ResultVO resultVO = new ResultVO();
		String[] groupIds = req.getParameterValues("groupIds[]");

		try {
			resultVO = clientGroupService.readClientGroupNodeList(groupIds);
		} catch (Exception ex) {
			logger.error("error in readClientGroupNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create client group information
	 * <p>
	 * check group name duplicate before create.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/createClientGroup")
	public @ResponseBody ResultVO createClientGroup(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String groupName = req.getParameter("groupName");
		String groupComment = req.getParameter("groupComment");
		String regClientIp = req.getParameter("regClientIp");
		String uprGrpId = req.getParameter("uprGrpId");

		String clientConfigId = req.getParameter("clientConfigId");
		String hostNameConfigId = req.getParameter("hostNameConfigId");
		String updateServerConfigId = req.getParameter("updateServerConfigId");

		String browserRuleId = req.getParameter("browserRuleId");
		String mediaRuleId = req.getParameter("mediaRuleId");
		String securityRuleId = req.getParameter("securityRuleId");
		String filteredSoftwareRuleId = req.getParameter("filteredSoftwareRuleId");
		String ctrlCenterItemRuleId = req.getParameter("ctrlCenterItemRuleId");
		String policyKitRuleId = req.getParameter("policyKitRuleId");
		String desktopConfId = req.getParameter("desktopConfId");

		ResultVO resultVO = new ResultVO();

		try {

			// check duplicate
			StatusVO dupStatus = clientGroupService.isExistGroupNameByParentId(uprGrpId, groupName);

			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {

				ClientGroupVO groupVO = new ClientGroupVO();
				groupVO.setGrpNm(groupName);
				groupVO.setComment(groupComment);
				groupVO.setRegClientIp(regClientIp);
				groupVO.setUprGrpId(uprGrpId);

				groupVO.setClientConfigId(clientConfigId);
				groupVO.setHostNameConfigId(hostNameConfigId);
				groupVO.setUpdateServerConfigId(updateServerConfigId);

				groupVO.setBrowserRuleId(browserRuleId);
				groupVO.setMediaRuleId(mediaRuleId);
				groupVO.setSecurityRuleId(securityRuleId);
				groupVO.setFilteredSoftwareRuleId(filteredSoftwareRuleId);
				groupVO.setCtrlCenterItemRuleId(ctrlCenterItemRuleId);
				groupVO.setPolicyKitRuleId(policyKitRuleId);

				groupVO.setDesktopConfigId(desktopConfId);

				groupVO.setRegUserId(LoginInfoHelper.getUserId());

				StatusVO status = clientGroupService.createClientGroup(groupVO, false);
				resultVO.setStatus(status);

			} else {
				resultVO.setStatus(dupStatus);
			}

		} catch (Exception ex) {
			logger.error("error in createClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete client group information.
	 * <p>
	 * delete group master and mapping table that connect client and group.
	 * 
	 * @param groupId String for client group id
	 * @param model   ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/deleteClientGroup")
	public @ResponseBody ResultVO deleteClientGroup(@RequestParam(value = "groupId", required = true) String groupId,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			if (groupId != null && !GPMSConstants.DEFAULT_GROUPID.equals(groupId)) {
				// query client list that include in group will delete
				ResultVO clients = clientGroupService.getOnlineClientIdsByGroupId(groupId);
				ResultVO beforeDataRe = clientGroupService.readClientGroupData(groupId);
				ClientGroupVO beforeGroupVO = null;
				if (beforeDataRe != null && beforeDataRe.getData() != null && beforeDataRe.getData().length > 0) {
					beforeGroupVO = (ClientGroupVO) beforeDataRe.getData()[0];
				}

				StatusVO status = clientGroupService.deleteClientGroup(groupId);
				resultVO.setStatus(status);

				// [JOBCREATE]
				if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
					String[] clientIds = new String[clients.getData().length];
					for (int c = 0; c < clients.getData().length; c++) {
						clientIds[c] = ((ClientVO) clients.getData()[c]).getClientId();
					}

					ResultVO re = clientGroupService.readClientGroupData(GPMSConstants.DEFAULT_GROUPID);
					if (re != null && re.getData() != null && re.getData().length > 0) {
						ClientGroupVO groupVO = (ClientGroupVO) re.getData()[0];
						jobMaker.createJobForGroup(clientIds, groupVO, beforeGroupVO);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete client group array information (multi-data delete).
	 * <p>
	 * delete group master and mapping table that connect client and group.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/deleteClientGroupList")
	public @ResponseBody ResultVO deleteClientGroupList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		String[] groupIds = req.getParameterValues("groupIds[]");
		String isDeleteClient = req.getParameter("isDeleteClient");
		ResultVO resultVO = new ResultVO();

		try {
			StatusVO status = clientGroupService.deleteClientGroupList(groupIds, isDeleteClient);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify client group information.
	 * <p>
	 * group name, group comment and client config(rule) information.
	 * <p>
	 * check group name if is duplicate.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/updateClientGroup")
	public @ResponseBody ResultVO updateClientGroup(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String groupId = req.getParameter("groupId");
		String groupName = req.getParameter("groupName");
		String groupComment = req.getParameter("groupComment");
		String regClientIp = req.getParameter("regClientIp");

		String clientConfigId = req.getParameter("clientConfigId");
		String hostNameConfigId = req.getParameter("hostNameConfigId");
		String updateServerConfigId = req.getParameter("updateServerConfigId");

		String browserRuleId = req.getParameter("browserRuleId");
		String mediaRuleId = req.getParameter("mediaRuleId");
		String securityRuleId = req.getParameter("securityRuleId");
		String filteredSoftwareRuleId = req.getParameter("filteredSoftwareRuleId");
		String ctrlCenterItemRuleId = req.getParameter("ctrlCenterItemRuleId");
		String policyKitRuleId = req.getParameter("policyKitRuleId");

		String desktopConfId = req.getParameter("desktopConfId");

		ResultVO resultVO = new ResultVO();
		try {

			// check duplicate group name
			StatusVO dupStatus = clientGroupService.isExistGroupNameByGroupId(groupId, groupName);

			if (GPMSConstants.MSG_SUCCESS.equalsIgnoreCase(dupStatus.getResult())) {

				ResultVO beforeDataRe = clientGroupService.readClientGroupData(groupId);
				ClientGroupVO beforeGroupVO = null;
				if (beforeDataRe != null && beforeDataRe.getData() != null && beforeDataRe.getData().length > 0) {
					beforeGroupVO = (ClientGroupVO) beforeDataRe.getData()[0];
				}

				ClientGroupVO groupVO = new ClientGroupVO();
				groupVO.setGrpId(groupId);
				groupVO.setGrpNm(groupName);
				groupVO.setComment(groupComment);
				groupVO.setRegClientIp(regClientIp);

				groupVO.setClientConfigId(clientConfigId);
				groupVO.setHostNameConfigId(hostNameConfigId);
				groupVO.setUpdateServerConfigId(updateServerConfigId);

				groupVO.setBrowserRuleId(browserRuleId);
				groupVO.setMediaRuleId(mediaRuleId);
				groupVO.setSecurityRuleId(securityRuleId);
				groupVO.setFilteredSoftwareRuleId(filteredSoftwareRuleId);
				groupVO.setCtrlCenterItemRuleId(ctrlCenterItemRuleId);
				groupVO.setPolicyKitRuleId(policyKitRuleId);

				groupVO.setDesktopConfigId(desktopConfId);

				groupVO.setModUserId(LoginInfoHelper.getUserId());

				StatusVO status = clientGroupService.updateClientGroup(groupVO);
				resultVO.setStatus(status);

				// [JOBCREATE]
				if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
					// register client (oinline)
					ResultVO clients = clientGroupService.getOnlineClientIdsByGroupId(groupId);
					String[] clientIds = new String[clients.getData().length];
					for (int c = 0; c < clients.getData().length; c++) {
						clientIds[c] = ((ClientVO) clients.getData()[c]).getClientId();
					}

					jobMaker.createJobForGroup(clientIds, groupVO, beforeGroupVO);
				}

			} else {
				resultVO.setStatus(dupStatus);
			}

		} catch (Exception ex) {
			logger.error("error in updateClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * insert client into group for client.
	 * <p>
	 * create job for client config(rule) change.
	 * 
	 * @param groupId string group id
	 * @param clients string clients data, comma separated
	 * @param model   ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/addClientsInGroup")
	public @ResponseBody ResultVO addClientsInGroup(@RequestParam(value = "groupId", required = true) String groupId,
			@RequestParam(value = "clients", required = true) String clients, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {
			// create array for clients
			String[] clientIds = clients.split(",");
			StatusVO status = clientGroupService.updateGroupToClient(groupId, clientIds);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				ClientGroupVO groupVO = clientGroupService.getClientGroupData(groupId);
				if (groupVO != null) {
					// create JOB
					// 1. client setup rule -> ClientConf, Hosts
					jobMaker.createJobForGroup(clientIds, groupVO, null);
				}
			}

		} catch (Exception ex) {
			logger.error("error in addClientsInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete client from group for client.
	 * <p>
	 * create job for client config(rule) change.
	 * 
	 * @param groupId string group id
	 * @param clients string clients data, comma separated
	 * @param model   ModelMap
	 * @return ResultVO
	 * 
	 */
	@PostMapping(value = "/removeClientsInGroup")
	public @ResponseBody ResultVO removeClientsInGroup(@RequestParam(value = "clients", required = true) String clients,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			String[] clientIds = clients.split(",");
			StatusVO status = clientGroupService.updateGroupToClient(GPMSConstants.DEFAULT_GROUPID, clientIds);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// create JOB for client client config(rule)
				ResultVO re = clientGroupService.readClientGroupData(GPMSConstants.DEFAULT_GROUPID);
				if (re != null && re.getData() != null && re.getData().length > 0) {
					ClientGroupVO groupVO = (ClientGroupVO) re.getData()[0];
					jobMaker.createJobForGroup(clientIds, groupVO, null);
				}
			}

		} catch (Exception ex) {
			logger.error("error in removeClientsInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * 단말그룹리스트(트리용 Json) 조회
	 */
	@PostMapping(value = "/readChildrenClientGroupList")
	public @ResponseBody Object[] readChildrenClientGroupList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		String grpId = req.getParameter("grpId");
		String hasWithRoot = req.getParameter("hasWithRoot");
		Object[] hm = null;
		try {
			ResultVO resultVO = clientGroupService.getChildrenClientGroupList(grpId, hasWithRoot);
			if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
				ClientGroupVO rootInfo = null;
				if(GPMSConstants.GUBUN_YES.equalsIgnoreCase(hasWithRoot)) {
					rootInfo = (ClientGroupVO)resultVO.getExtend()[0];
				}
				
				Object[] objs = resultVO.getData();
				hm = new Object[objs.length];
				for (int i = 0; i < objs.length; i++) {
					ClientGroupVO vo = (ClientGroupVO) objs[i];
					HashMap<String, Object> vm = new HashMap<String, Object>();
					vm.put("title", vo.getGrpNm());
					vm.put("key", vo.getGrpId());
					vm.put("hasChildren", vo.getHasChildren());
					vm.put("modDt", vo.getModDate());
					vm.put("regDt", vo.getRegDate());
					vm.put("comment", vo.getComment());
					vm.put("regClientIp", vo.getRegClientIp());
					vm.put("whleData", vo.getWhleGrpId());
					vm.put("level", vo.getGrpLevel());
					vm.put("itemCount", vo.getItemCount());
					vm.put("itemTotalCount", vo.getItemTotalCount());
					if(rootInfo != null) {
						vm.put("rootItemCount", rootInfo.getItemCount());
						vm.put("rootItemTotalCount", rootInfo.getItemTotalCount());
					}
					hm[i] = vm;
				}
			}
		} catch (Exception e) {
			hm = new Object[0];
		}
		return hm;
	}

	/**
	 * 단말그룹 설정 수정 - 하위조직 포함
	 *
	 * @param parentDeptCd String
	 * @param objId        String
	 * @param confType     String
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/updateClientGroupConfInherit")
	public @ResponseBody ResultVO updateClientGroupConfInherit(
			@RequestParam(value = "grpId", required=true) String parentGrpId,
			@RequestParam(value = "objId" ) String objId,
			@RequestParam(value = "confType" ) String confType, ModelMap model) {
		ResultVO resultVO = new ResultVO();
		try {
			// get children dept list
			ResultVO allGroupResultVo = clientGroupService.getAllChildrenGroupList(parentGrpId);
			if (allGroupResultVo != null && allGroupResultVo.getData() != null
					&& allGroupResultVo.getData().length > 0) {
				ClientGroupVO[] groups = (ClientGroupVO[]) allGroupResultVo.getData();
				for (ClientGroupVO vo : groups) {
					clientGroupService.updateClientGroupConf(vo.getGrpId(), objId, confType);
				}
			}

			StatusVO status = new StatusVO();
			status.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");

			resultVO.setStatus(status);
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * 여러그룹의 권한정보 일괄 수정
	 * 
	 * @param
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/updateRuleForGroups")
	public @ResponseBody ResultVO updateRuleForGroups(
			@RequestParam(value = "grpIds", required=true) String grpIds,

			@RequestParam(value = "clientConfigId" ) String clientConfigId,
			@RequestParam(value = "hostNameConfigId" ) String hostNameConfigId,
			@RequestParam(value = "updateServerConfigId" ) String updateServerConfigId,

			@RequestParam(value = "browserRuleId" ) String browserRuleId,
			@RequestParam(value = "mediaRuleId" ) String mediaRuleId,
			@RequestParam(value = "securityRuleId" ) String securityRuleId,
			@RequestParam(value = "filteredSoftwareRuleId" ) String filteredSoftwareRuleId,
			@RequestParam(value = "ctrlCenterItemRuleId" ) String ctrlCenterItemRuleId,
			@RequestParam(value = "policyKitRuleId" ) String policyKitRuleId,
			@RequestParam(value = "desktopConfId" ) String desktopConfId, ModelMap model) {

		String[] grpIdArray = grpIds.split(",");
		ResultVO resultVO = new ResultVO();
		try {
			StatusVO status = clientGroupService.updateRuleInfoToMultiGroup(grpIdArray, clientConfigId,
					hostNameConfigId, updateServerConfigId, browserRuleId, mediaRuleId, securityRuleId,
					filteredSoftwareRuleId, ctrlCenterItemRuleId, policyKitRuleId, desktopConfId);
			resultVO.setStatus(status);
			
			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// get clients
				ResultVO clients = clientGroupService.getOnlineClientIdsByGroupList(grpIdArray);
				String[] clientIds = new String[clients.getData().length];
				for (int c = 0; c < clients.getData().length; c++) {
					clientIds[c] = ((ClientVO) clients.getData()[c]).getClientId();
				}
				// create job
				jobMaker.createAbsoluteJobForClients(clientIds, clientConfigId);
			}
			
		} catch (Exception ex) {
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}
}
