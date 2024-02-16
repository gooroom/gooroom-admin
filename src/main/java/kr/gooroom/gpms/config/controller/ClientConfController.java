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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.client.service.ClientGroupService;
import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.ClientConfService;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import kr.gooroom.gpms.config.service.DesktopConfService;
import kr.gooroom.gpms.config.service.MgServerConfVO;
import kr.gooroom.gpms.config.service.RuleIdsVO;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.user.service.UserRoleVO;
import kr.gooroom.gpms.user.service.UserService;

/**
 * Handles requests for the client configuration management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientConfController {

	private static final Logger logger = LoggerFactory.getLogger(ClientConfController.class);

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "clientConfService")
	private ClientConfService clientConfService;

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;

	@Resource(name = "jobService")
	private JobService jobService;

	@Resource(name = "clientGroupService")
	private ClientGroupService clientGroupService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "desktopConfService")
	private DesktopConfService desktopConfService;

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

	private ArrayList<CtrlPropVO> createCtrlPropList(HttpServletRequest req, String modUserId) {

		ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
		int propSeq = 1;

		String homeReset = req.getParameter(GPMSConstants.CTRL_ITEM_USEHOMERESET);
		String rootAllow = req.getParameter(GPMSConstants.CTRL_ITEM_ROOTALLOW);
		String sudoAllow = req.getParameter(GPMSConstants.CTRL_ITEM_SUDOALLOW);
		String whiteIpAll = req.getParameter(GPMSConstants.CTRL_ITEM_WHITEIPALL);
		String[] whiteIps = req.getParameterValues(GPMSConstants.CTRL_ITEM_WHITEIPS + "[]");
		String policykitUser = req.getParameter("policykit_user");

		String cleanModeAllow = req.getParameter(GPMSConstants.CTRL_ITEM_CLEANMODEALLOW);

		String isDeleteLog = req.getParameter("isDeleteLog");
		String logMaxSize = req.getParameter("logMaxSize");
		String logMaxCount = req.getParameter("logMaxCount");
		String logRemainDate = req.getParameter("logRemainDate");
		String systemKeepFree = req.getParameter("systemKeepFree");

		String transmit_boot = req.getParameter("transmit_boot");
		String transmit_exe = req.getParameter("transmit_exe");
		String transmit_os = req.getParameter("transmit_os");
		String transmit_media = req.getParameter("transmit_media");
		String transmit_agent = req.getParameter("transmit_agent");

		String notify_boot = req.getParameter("notify_boot");
		String notify_exe = req.getParameter("notify_exe");
		String notify_os = req.getParameter("notify_os");
		String notify_media = req.getParameter("notify_media");
		String notify_agent = req.getParameter("notify_agent");

		String show_boot = req.getParameter("show_boot");
		String show_exe = req.getParameter("show_exe");
		String show_os = req.getParameter("show_os");
		String show_media = req.getParameter("show_media");
		String show_agent = req.getParameter("show_agent");

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_USEHOMERESET, homeReset,
				"", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_ROOTALLOW, rootAllow,
				"", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_SUDOALLOW, sudoAllow,
				"", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_CLEANMODEALLOW, cleanModeAllow,
				"", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_WHITEIPALL, whiteIpAll,
				"", modUserId, ""));

		if (whiteIps != null && whiteIps.length > 0) {
			for (String whiteIp : whiteIps) {
				propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_WHITEIPS,
						whiteIp, "", modUserId, ""));
			}
		}

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "policykitUser", policykitUser, "", modUserId, ""));

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "isDeleteLog", isDeleteLog, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "logMaxSize", logMaxSize, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "logMaxCount", logMaxCount, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "logRemainDate", logRemainDate, "", modUserId, ""));
		propList.add(
				new CtrlPropVO("", String.valueOf(propSeq++), "systemKeepFree", systemKeepFree, "", modUserId, ""));

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "transmit_boot", transmit_boot, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "transmit_exe", transmit_exe, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "transmit_os", transmit_os, "", modUserId, ""));
		propList.add(
				new CtrlPropVO("", String.valueOf(propSeq++), "transmit_media", transmit_media, "", modUserId, ""));
		propList.add(
				new CtrlPropVO("", String.valueOf(propSeq++), "transmit_agent", transmit_agent, "", modUserId, ""));

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "notify_boot", notify_boot, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "notify_exe", notify_exe, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "notify_os", notify_os, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "notify_media", notify_media, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "notify_agent", notify_agent, "", modUserId, ""));

		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "show_boot", show_boot, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "show_exe", show_exe, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "show_os", show_os, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "show_media", show_media, "", modUserId, ""));
		propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "show_agent", show_agent, "", modUserId, ""));

		return propList;
	}

		/**
         * get common configuration list data with paging for client conf, hosts, update
         * server conf.
         *
         * @param req HttpServletRequest
         * @return ResultPagingVO result data bean
         *
         */
	private ResultPagingVO readCommonCtrlItemAndPropList(HttpServletRequest req, String objectType) {

		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_")
				: "");

		// search keyword
		options.put("searchKey", searchKey);

		// << paging >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");

		// << Order >>
		if ("chConfName".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "M.OBJ_NM");
		} else if ("chModUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "M.MOD_USER_ID");
		} else if ("chRegUser".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "M.REG_USER_ID");
		} else if ("chConfId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "M.OBJ_ID");
		} else if ("chModDate".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "M.MOD_DT");
		} else {
			options.put("paramOrderColumn", "M.OBJ_ID");
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

		options.put("mngObjTp", objectType);

		try {

			resultVO = ctrlMstService.readCtrlItemAndPropListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(paramLength);

		} catch (Exception ex) {
			logger.error("error in readClientConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get gooroom server address(configuration) history data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMgServerConfHistory")
	public @ResponseBody ResultVO readMgServerConfHistory() {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = clientConfService.getMgServerConfList();
		} catch (Exception ex) {
			logger.error("error in readMgServerConfHistory : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * insert or update gooroom server address(configuration) data.
	 * 
	 * @param paramVO MgServerConfVO configuration data bean
	 * @param model   ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createMgServerConf")
	public @ResponseBody ResultVO createMgServerConf(@ModelAttribute("paramVO") MgServerConfVO paramVO,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = clientConfService.createMgServerConf(paramVO);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get current gooroom server address(configuration) data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCurrentMgServerConf")
	public @ResponseBody ResultVO readCurrentMgServerConf() {

		ResultVO resultVO = new ResultVO();
		try {

			resultVO = clientConfService.readCurrentMgServerConf();

		} catch (Exception ex) {
			logger.error("error in readCurrentMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get client configuration list data
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientConfList")
	public @ResponseBody ResultVO readClientConfList(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_CLIENT_SETUP_CONF);

		} catch (Exception ex) {
			logger.error("error in readClientConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get client configuration list data with paging
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientConfListPaged")
	public @ResponseBody ResultPagingVO readClientConfListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_CLIENT_SETUP_CONF);
	}

	/**
	 * set client configuration data
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createClientConf")
	public @ResponseBody ResultVO createClientConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		ResultVO resultVO = new ResultVO();

		try {
			String modUserId = LoginInfoHelper.getUserId();

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(modUserId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_CLIENT_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = createCtrlPropList(req, modUserId);
			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned update-server configuration data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneClientConf")
	public @ResponseBody ResultVO cloneClientConf(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_CLIENT_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get property value by property name
	 * 
	 * @param vo       CtrlItemVO property item
	 * @param propName string property name
	 * @return string property value
	 *
	 */
	private String getPropertyValue(CtrlItemVO vo, String propName) {

		if (vo != null && propName != null) {
			ArrayList<CtrlPropVO> props = vo.getPropList();
			if (props != null && props.size() > 0) {
				for (CtrlPropVO prop : props) {
					if (propName.equals(prop.getPropNm())) {
						return prop.getPropValue();
					}
				}
			}
		}

		return "";
	}

	/**
	 * get client configuration data with item and value
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientConf")
	public @ResponseBody ResultVO readClientConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");

		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * delete client configuration data and create job
	 * 
	 * @param objId string target configuration id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteClientConf")
	public @ResponseBody ResultVO deleteClientConf(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();

		try {

			ResultVO clients = clientService.getOnlineClientIdsInClientConf(objId, GPMSConstants.TYPE_CLIENTCONF);

			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_CLIENT,
					GPMSConstants.TYPE_CLIENTCONF);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (clients != null && clients.getData().length > 0) {

					String[] clientIds = new String[clients.getData().length];
					for (int c = 0; c < clients.getData().length; c++) {
						clientIds[c] = ((ClientVO) clients.getData()[c]).getClientId();
					}

					// create job for client assign default rule, as delete rule.
					// select default rule.
					ResultVO defaultConfObj = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
					if (defaultConfObj != null && defaultConfObj.getData().length > 0) {

						ArrayList<CtrlPropVO> props = ((CtrlItemVO) defaultConfObj.getData()[0]).getPropList();

						String homeReset = "";
						String rootAllow = "";
						String sudoAllow = "";
						String cleanModeAllow = "";
						if (props != null && props.size() > 0) {
							for (CtrlPropVO prop : props) {
								if (GPMSConstants.CTRL_ITEM_USEHOMERESET.equalsIgnoreCase(prop.getPropNm())) {
									homeReset = prop.getPropValue();
								}
								if (GPMSConstants.CTRL_ITEM_ROOTALLOW.equalsIgnoreCase(prop.getPropNm())) {
									rootAllow = prop.getPropValue();
								}
								if (GPMSConstants.CTRL_ITEM_SUDOALLOW.equalsIgnoreCase(prop.getPropNm())) {
									sudoAllow = prop.getPropValue();
								}
								if (GPMSConstants.CTRL_ITEM_CLEANMODEALLOW.equalsIgnoreCase(prop.getPropNm())) {
									cleanModeAllow = prop.getPropValue();
								}
							}
						}

						// home reset job
						HashMap<String, String> map = new HashMap<String, String>();
						if ("true".equalsIgnoreCase(homeReset)) {
							map.put("operation", "enable");
						} else {
							map.put("operation", "disable");
						}
						jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOMERESET_CHANGE, map,
								clientIds);
						
						// root / sudo allow job
						HashMap<String, String> mapAccountAllow = new HashMap<String, String>();
						if ("true".equalsIgnoreCase(rootAllow)) {
							mapAccountAllow.put("root_use", "allow");
						} else {
							mapAccountAllow.put("root_use", "disallow");
						}
						if ("true".equalsIgnoreCase(sudoAllow)) {
							mapAccountAllow.put("sudo_use", "allow");
						} else {
							mapAccountAllow.put("sudo_use", "disallow");
						}
						jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_ACCOUNT_RULE_CHANGE, mapAccountAllow,
								clientIds);

						// clean mode allow job
						HashMap<String, String> mapCleanModeAllow = new HashMap<>();
						if("true".equalsIgnoreCase(cleanModeAllow)) {
							mapCleanModeAllow.put("cleanmode_use", "enable");
						} else {
							mapCleanModeAllow.put("cleanmode_use", "disable");
						}
						jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_CLEANMODE_RULE_CHANGE, mapCleanModeAllow,
								clientIds);

						// use log config change job
						jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_LOGCONFIG_CHANGE, null,
								clientIds);

					}
				}
			}
		} catch (Exception ex) {
			logger.error("error in deleteClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify client configuration data with new values.
	 * <p>
	 * create job for client.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateClientConf")
	public @ResponseBody ResultVO updateClientConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String objId = req.getParameter("objId");
		String confName = req.getParameter("objName");
		String confComment = req.getParameter("objComment");


		ResultVO resultVO = new ResultVO();

		try {
			String modUserId = LoginInfoHelper.getUserId();
			// select current configuration
			ResultVO currentConfObj = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(confName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_CLIENT_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR);
			itemVo.setComment(confComment);
			itemVo.setModUserId(modUserId);

			ArrayList<CtrlPropVO> propList = createCtrlPropList(req, modUserId);
			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				String homeReset = req.getParameter(GPMSConstants.CTRL_ITEM_USEHOMERESET);
				String rootAllow = req.getParameter(GPMSConstants.CTRL_ITEM_ROOTALLOW);
				String sudoAllow = req.getParameter(GPMSConstants.CTRL_ITEM_SUDOALLOW);
				String policykitUser = req.getParameter("policykit_user");
				String cleanModeAllow = req.getParameter(GPMSConstants.CTRL_ITEM_CLEANMODEALLOW);

				// use home reset job
				if (homeReset != null && !(homeReset.equals(getPropertyValue((CtrlItemVO) currentConfObj.getData()[0],
						GPMSConstants.CTRL_ITEM_USEHOMERESET)))) {
					HashMap<String, String> map = new HashMap<String, String>();
					if ("true".equalsIgnoreCase(homeReset)) {
						map.put("operation", "enable");
					} else {
						map.put("operation", "disable");
					}
					jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_CLIENTCONF,
							GPMSConstants.JOB_CLIENTCONF_HOMERESET_CHANGE, map);
				}
				
				// root / sudo allow job
				HashMap<String, String> mapAccountAllow = new HashMap<String, String>();
				if ("true".equalsIgnoreCase(rootAllow)) {
					mapAccountAllow.put("root_use", "allow");
				} else {
					mapAccountAllow.put("root_use", "disallow");
				}
				
				if ("true".equalsIgnoreCase(sudoAllow)) {
					mapAccountAllow.put("sudo_use", "allow");
				} else {
					mapAccountAllow.put("sudo_use", "disallow");
				}
				jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_CLIENTCONF,
						GPMSConstants.JOB_ACCOUNT_RULE_CHANGE, mapAccountAllow);
				
				// policykit user job
				if (policykitUser != null && !(policykitUser.equals(getPropertyValue((CtrlItemVO) currentConfObj.getData()[0], "policykitUser")))) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("polkit_admin", policykitUser);
					jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_CLIENTCONF,
							GPMSConstants.JOB_CLIENTCONF_POLICYKITUSER_CHANGE, map);
				}

				// clean mode allow job
				HashMap<String, String> mapCleanModeAllow = new HashMap<>();
				if("true".equalsIgnoreCase(cleanModeAllow)) {
					mapCleanModeAllow.put("cleanmode_use", "enable");
				} else {
					mapCleanModeAllow.put("cleanmode_use", "disable");
				}
				jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_CLIENTCONF,
						GPMSConstants.JOB_CLEANMODE_RULE_CHANGE, mapCleanModeAllow);

				// use log config change job
				jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_CLIENTCONF,
						GPMSConstants.JOB_CLIENTCONF_LOGCONFIG_CHANGE, null);

			}

		} catch (Exception ex) {
			logger.error("error in updateClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get client configuration data by client group information.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readClientConfByGroupId")
	public @ResponseBody ResultVO readClientConfByGroupId(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO groupConfInfo = clientConfService.getClientConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(groupConfInfo.getStatus().getResult())) {
					ClientGroupVO vo = (ClientGroupVO) groupConfInfo.getData()[0];
					if (vo.getClientConfigId() != null && vo.getClientConfigId().length() > 0) {
						resultVO = ctrlMstService.readCtrlItem(vo.getClientConfigId());
						if (GPMSConstants.MSG_SUCCESS.equals(resultVO.getStatus().getResult())) {
							resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_GROUP });
							return resultVO;
						}
					}
				}

				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_CLIENT_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
			}
		} catch (Exception ex) {
			logger.error("error in readClientConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get update-server configuration data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUpdateServerConfList")
	public @ResponseBody ResultVO readUpdateServerConfList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_UPDATE_SERVER_CONF);

		} catch (Exception ex) {
			logger.error("error in readUpdateServerConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get update-server configuration data list paged.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readUpdateServerConfListPaged")
	public @ResponseBody ResultPagingVO readUpdateServerConfListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_UPDATE_SERVER_CONF);
	}

	/**
	 * insert update-server configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createUpdateServerConf")
	public @ResponseBody ResultVO createUpdateServerConf(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String mainOs = req.getParameter(GPMSConstants.CTRL_ITEM_MAINOS);
		String extOs = req.getParameter(GPMSConstants.CTRL_ITEM_EXTOS);
		String priorities = req.getParameter(GPMSConstants.CTRL_ITEM_PRIORITIES);

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			itemVo.setMngObjTp(GPMSConstants.CTRL_UPDATE_SERVER_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_MAINOS, mainOs, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_EXTOS, extOs, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PRIORITIES, priorities,
					"", LoginInfoHelper.getUserId(), ""));

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createUpdateServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned update-server configuration data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneUpdateServerConf")
	public @ResponseBody ResultVO cloneUpdateServerConf(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_UPDATE_SERVER_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneUpdateServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get current update-server configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUpdateServerConf")
	public @ResponseBody ResultVO readUpdateServerConf(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");

		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readUpdateServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get current update-server configuration data by client group id.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readUpdateServerConfByGroupId")
	public @ResponseBody ResultVO readUpdateServerConfByGroupId(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO groupConfInfo = clientConfService.getClientConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(groupConfInfo.getStatus().getResult())) {
					ClientGroupVO vo = (ClientGroupVO) groupConfInfo.getData()[0];
					if (vo.getUpdateServerConfigId() != null && vo.getUpdateServerConfigId().length() > 0) {
						resultVO = ctrlMstService.readCtrlItem(vo.getUpdateServerConfigId());
						if (GPMSConstants.MSG_SUCCESS.equals(resultVO.getStatus().getResult())) {
							resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_GROUP });
							return resultVO;
						}
					}
				}

				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
			}
		} catch (Exception ex) {
			logger.error("error in readClientConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * update update-server configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateUpdateServerConf")
	public @ResponseBody ResultVO updateUpdateServerConf(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		String objId = req.getParameter("objId");
		String confName = req.getParameter("objName");
		String confComment = req.getParameter("objComment");

		String mainos = req.getParameter(GPMSConstants.CTRL_ITEM_MAINOS);
		String extos = req.getParameter(GPMSConstants.CTRL_ITEM_EXTOS);
		String priorities = req.getParameter(GPMSConstants.CTRL_ITEM_PRIORITIES);

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(confName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_UPDATE_SERVER_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_UPDATE_SERVER_CONF_ABBR);
			itemVo.setComment(confComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_MAINOS, mainos, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_EXTOS, extos, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PRIORITIES, priorities,
					"", LoginInfoHelper.getUserId(), ""));

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateUpdateServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete update-server configuration data.
	 * 
	 * @param objId String update-server configuration id.
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/deleteUpdateServerConf")
	public @ResponseBody ResultVO deleteUpdateServerConf(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();

		try {

			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_CLIENT,
					GPMSConstants.TYPE_UPDATESERVERCONF);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in deleteUpdateServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get hosts configuration data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readHostNameConfList")
	public @ResponseBody ResultVO readHostNameConfList() {

		ResultVO resultVO = new ResultVO();

		try {

			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_HOSTS_SETUP_CONF);

		} catch (Exception ex) {
			logger.error("error in readHostNameConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get hosts configuration data list paged.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readHostNameConfListPaged")
	public @ResponseBody ResultPagingVO readHostNameConfListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_HOSTS_SETUP_CONF);
	}

	/**
	 * insert Hosts configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createHostNameConf")
	public @ResponseBody ResultVO createHostNameConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String hosts = req.getParameter(GPMSConstants.CTRL_ITEM_HOSTNAME);

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			itemVo.setMngObjTp(GPMSConstants.CTRL_HOSTS_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_HOSTNAME, hosts, "",
					LoginInfoHelper.getUserId(), ""));

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createHostNameConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned Hosts configuration data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneHostNameConf")
	public @ResponseBody ResultVO cloneHostNameConf(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_HOSTS_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneHostNameConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get current Hosts configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readHostNameConf")
	public @ResponseBody ResultVO readHostNameConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");

		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readHostNameConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * get current Hosts configuration data by client group id.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readHostNameConfByGroupId")
	public @ResponseBody ResultVO readHostNameConfByGroupId(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO groupConfInfo = clientConfService.getClientConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(groupConfInfo.getStatus().getResult())) {
					ClientGroupVO vo = (ClientGroupVO) groupConfInfo.getData()[0];
					if (vo.getHostNameConfigId() != null && vo.getHostNameConfigId().length() > 0) {
						resultVO = ctrlMstService.readCtrlItem(vo.getHostNameConfigId());
						if (GPMSConstants.MSG_SUCCESS.equals(resultVO.getStatus().getResult())) {
							resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_GROUP });
							return resultVO;
						}
					}
				}

				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
			}
		} catch (Exception ex) {
			logger.error("error in readHostNameConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return resultVO;
	}

	/**
	 * update Hosts configuration data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateHostNameConf")
	public @ResponseBody ResultVO updateHostNameConf(HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		String objId = req.getParameter("objId");
		String confName = req.getParameter("objName");
		String confComment = req.getParameter("objComment");

		String hosts = req.getParameter(GPMSConstants.CTRL_ITEM_HOSTNAME);

		ResultVO resultVO = new ResultVO();

		try {

			// select current configuration
			ResultVO currentConfObj = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(confName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_HOSTS_SETUP_CONF);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_HOSTS_SETUP_CONF_ABBR);
			itemVo.setComment(confComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_HOSTNAME, hosts, "",
					LoginInfoHelper.getUserId(), ""));

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// hosts
				if (hosts != null && !(hosts.equals(getPropertyValue((CtrlItemVO) currentConfObj.getData()[0],
						GPMSConstants.CTRL_ITEM_HOSTNAME)))) {
					jobMaker.createJobForClientConf(objId, GPMSConstants.TYPE_HOSTNAMECONF,
							GPMSConstants.JOB_CLIENTCONF_HOSTS_CHANGE, null);
				}
			}
		} catch (Exception ex) {
			logger.error("error in updateHostNameConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete Hosts configuration data.
	 * 
	 * @param objId String update-server configuration id.
	 * @return ResultVO
	 * @throws Exception
	 */
	@PostMapping(value = "/deleteHostNameConf")
	public @ResponseBody ResultVO deleteHostNameConf(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();

		try {

			// get target client for create job
			ResultVO clients = clientService.getOnlineClientIdsInClientConf(objId, GPMSConstants.TYPE_HOSTNAMECONF);

			// delete configuration
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_CLIENT,
					GPMSConstants.TYPE_HOSTNAMECONF);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (clients != null && clients.getData().length > 0) {

					String[] clientIds = new String[clients.getData().length];
					for (int c = 0; c < clients.getData().length; c++) {
						clientIds[c] = ((ClientVO) clients.getData()[c]).getClientId();
					}

					// create job for changing configuration data
					// hosts job
					jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOSTS_CHANGE, null,
							clientIds);
				}
			}

		} catch (Exception ex) {
			logger.error("error in deleteHostNameConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * get browser rule (websocket, webworker, whitelist, trust-rule, untrust-rule)
	 * list data paged.
	 * 
	 * @param req                    HttpServletRequest
	 * @param resHttpServletResponse
	 * @param modelModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readBrowserRuleListPaged")
	public @ResponseBody ResultPagingVO readBrowserRuleListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE);
	}

	/**
	 * response browser rule list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readBrowserRuleList")
	public @ResponseBody ResultVO readBrowserRuleList() {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE);
		} catch (Exception ex) {
			logger.error("error in readBrowserRuleList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response browser rule configuration data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readBrowserRule")
	public @ResponseBody HashMap<String, Object> readBrowserRule(HttpServletRequest req) {

		HashMap<String, Object> hm = new HashMap<String, Object>();
		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");

		try {

			resultVO = ctrlMstService.readCtrlItem(objId);

			hm.put("status", resultVO.getStatus());
			hm.put("data", resultVO.getData());

//	    CtrlItemVO[] objs = (CtrlItemVO[]) resultVO.getData();
//	    if (objs != null && objs.length > 0) {
//		ArrayList<CtrlPropVO> props = objs[0].getPropList();
//		if (props != null && props.size() > 0) {
//		    ArrayList<Object> trustGroupList = new ArrayList<Object>();
//		    for (CtrlPropVO prop : props) {
//			if ("trustgroup".equalsIgnoreCase(prop.getPropNm())) {
//			    ResultVO groupResultVO = ctrlMstService.readCtrlItem(prop.getPropValue());
//			    trustGroupList.add(groupResultVO.getData()[0]);
//			} else if ("trustSetup".equalsIgnoreCase(prop.getPropNm())) {
//			    ResultVO setupResultVO = ctrlMstService.readCtrlItem(prop.getPropValue());
//			    if (setupResultVO != null && setupResultVO.getData().length > 0) {
//				ArrayList<CtrlPropVO> setupProps = ((CtrlItemVO) setupResultVO.getData()[0])
//					.getPropList();
//				for (CtrlPropVO setupProp : setupProps) {
//				    if ("CONTENT".equals(setupProp.getPropNm())) {
//					hm.put("TRUSTSITESETUP", ((CtrlItemVO) setupResultVO.getData()[0]).getObjNm());
//					hm.put("TRUSTSITESETUPSCRIPT", setupProp.getPropValue());
//				    }
//				}
//			    }
//			} else if ("untrustSetup".equalsIgnoreCase(prop.getPropNm())) {
//			    ResultVO setupResultVO = ctrlMstService.readCtrlItem(prop.getPropValue());
//			    if (setupResultVO != null && setupResultVO.getData().length > 0) {
//				ArrayList<CtrlPropVO> setupProps = ((CtrlItemVO) setupResultVO.getData()[0])
//					.getPropList();
//				for (CtrlPropVO setupProp : setupProps) {
//				    if ("CONTENT".equals(setupProp.getPropNm())) {
//					hm.put("UNTRUSTSITESETUP",
//						((CtrlItemVO) setupResultVO.getData()[0]).getObjNm());
//					hm.put("UNTRUSTSITESETUPSCRIPT", setupProp.getPropValue());
//				    }
//				}
//			    }
//			}
//		    }
//
//		    if (trustGroupList != null && trustGroupList.size() > 0) {
//			Object[] trustGroupArray = new Object[trustGroupList.size()];
//			trustGroupArray = trustGroupList.toArray(trustGroupArray);
//			hm.put("TRUSTGROUP", trustGroupArray);
//		    }
//		}
//	    }

		} catch (Exception ex) {
			logger.error("error in readBrowserRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return hm;
	}

	/**
	 * response browser rule configuration data by user id
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readBrowserRuleByUserId")
	public @ResponseBody HashMap<String, Object> readBrowserRuleByUserId(HttpServletRequest req) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String userId = req.getParameter("userId");
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getBrowserRuleId() != null && vo.getBrowserRuleId().length() > 0) {
						hm = getBrowserRuleByRoleId(vo.getBrowserRuleId(), GPMSConstants.RULE_GRADE_USER);
						hm.put("extend", new String[] { "USER" });
						return hm;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getBrowserRuleId() != null && vo.getBrowserRuleId().length() > 0) {
						hm = getBrowserRuleByRoleId(vo.getBrowserRuleId(), GPMSConstants.RULE_GRADE_DEPT);
						hm.put("extend", new String[] { "DEPT" });
						return hm;
					}
				}

				hm = getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readBrowserRuleByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return hm;
	}

	/**
	 * response real browser rule configuration data by dept code and if no exist,
	 * return default
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readBrowserRuleByDeptCd")
	public @ResponseBody HashMap<String, Object> readBrowserRuleByDeptCd(HttpServletRequest req) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getBrowserRuleId() != null && vo.getBrowserRuleId().length() > 0) {
						hm = getBrowserRuleByRoleId(vo.getBrowserRuleId(), GPMSConstants.RULE_GRADE_DEPT);
					} else {
						hm = getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					hm = getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readBrowserRuleByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return hm;
	}

	private HashMap<String, Object> getBrowserRuleByRoleId(String roleId, String ruleGrade) {

		HashMap<String, Object> hm = new HashMap<String, Object>();
		ResultVO resultVO = new ResultVO();

		try {

			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// select default rule
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

			//
			hm.put("status", resultVO.getStatus());
			hm.put("data", resultVO.getData());
			hm.put("extend", resultVO.getExtend());

		} catch (Exception ex) {
			logger.error("error in getBrowserRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO = null;
		}

		return hm;
	}

	/**
	 * response real browser rule configuration data by group code and if no exist,
	 * return default
	 * 
	 * @param req HttpServletRequest
	 * @return HashMap json format
	 *
	 */
	@PostMapping(value = "/readBrowserRuleByGroupId")
	public @ResponseBody HashMap<String, Object> readBrowserRuleByGroupId(HttpServletRequest req) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getBrowserRuleId() != null && vo.getBrowserRuleId().length() > 0) {
						hm = getBrowserRuleByRoleId(vo.getBrowserRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						hm = getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					hm = getBrowserRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readBrowserRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return hm;
	}

	/**
	 * create browser rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createBrowserRuleConf")
	public @ResponseBody ResultVO createBrowserRuleConf(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String webSocket = req.getParameter("webSocket");
		String webWorker = req.getParameter("webWorker");

		String devToolRule__trust = req.getParameter("devToolRule__trust");
		String downloadRule__trust = req.getParameter("downloadRule__trust");
		String printRule__trust = req.getParameter("printRule__trust");
		String viewSourceRule__trust = req.getParameter("viewSourceRule__trust");

		String devToolRule__untrust = req.getParameter("devToolRule__untrust");
		String downloadRule__untrust = req.getParameter("downloadRule__untrust");
		String printRule__untrust = req.getParameter("printRule__untrust");
		String viewSourceRule__untrust = req.getParameter("viewSourceRule__untrust");

		String trustSetup = req.getParameter("trustSetup");
		String untrustSetup = req.getParameter("untrustSetup");
		String[] trustUrlList = req.getParameterValues("trustUrlList[]");

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "websocket", webSocket, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "webworker", webWorker, "",
					LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "devToolRule__trust", devToolRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "downloadRule__trust", downloadRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "printRule__trust", printRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "viewSourceRule__trust", viewSourceRule__trust,
					"", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "devToolRule__untrust", devToolRule__untrust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "downloadRule__untrust", downloadRule__untrust,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "printRule__untrust", printRule__untrust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "viewSourceRule__untrust",
					viewSourceRule__untrust, "", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "trustSetup", trustSetup, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "untrustSetup", untrustSetup, "",
					LoginInfoHelper.getUserId(), ""));

			if (trustUrlList != null && trustUrlList.length > 0) {
				for (String brTrust : trustUrlList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "trust", brTrust, "",
							LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createClientSecuRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned browser rule data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneBrowserRule")
	public @ResponseBody ResultVO cloneBrowserRule(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneBrowserRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete browser rule configuration data
	 * 
	 * @param objId string id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteBrowserRuleConf")
	public @ResponseBody ResultVO deleteBrowserRuleConf(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_BROWSERRULE);
			// delete configuration
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_BROWSERRULE);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_BROWSER_RULE_CHANGE, clientArray);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("error in deleteBrowserRuleConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * modify browser rule configuration data
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateBrowserRuleConf")
	public @ResponseBody ResultVO updateBrowserRuleConf(HttpServletRequest req) {

		String objId = req.getParameter("objId");
		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");

		String webSocket = req.getParameter("webSocket");
		String webWorker = req.getParameter("webWorker");

		String devToolRule__trust = req.getParameter("devToolRule__trust");
		String downloadRule__trust = req.getParameter("downloadRule__trust");
		String printRule__trust = req.getParameter("printRule__trust");
		String viewSourceRule__trust = req.getParameter("viewSourceRule__trust");

		String devToolRule__untrust = req.getParameter("devToolRule__untrust");
		String downloadRule__untrust = req.getParameter("downloadRule__untrust");
		String printRule__untrust = req.getParameter("printRule__untrust");
		String viewSourceRule__untrust = req.getParameter("viewSourceRule__untrust");

		String trustSetup = req.getParameter("trustSetup");
		String untrustSetup = req.getParameter("untrustSetup");
		String[] trustUrlList = req.getParameterValues("trustUrlList[]");
		String[] trustGroupList = req.getParameterValues("trustGroupList[]");

		ResultVO resultVO = new ResultVO();

		try {

			// read before data.
			ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE_ABBR);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "websocket", webSocket, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "webworker", webWorker, "",
					LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "devToolRule__trust", devToolRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "downloadRule__trust", downloadRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "printRule__trust", printRule__trust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "viewSourceRule__trust", viewSourceRule__trust,
					"", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "devToolRule__untrust", devToolRule__untrust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "downloadRule__untrust", downloadRule__untrust,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "printRule__untrust", printRule__untrust, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "viewSourceRule__untrust",
					viewSourceRule__untrust, "", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "trustSetup", trustSetup, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "untrustSetup", untrustSetup, "",
					LoginInfoHelper.getUserId(), ""));

			if (trustUrlList != null && trustUrlList.length > 0) {
				for (String paramTrustUrl : trustUrlList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "trust", paramTrustUrl, "",
							LoginInfoHelper.getUserId(), ""));
				}
			}

			if (trustGroupList != null && trustGroupList.length > 0) {
				for (String paramTrustGroup : trustGroupList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), "trustgroup", paramTrustGroup, "T",
							LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// Create Job if value changed (compare oldData
				if (oldData != null && oldData.getData() != null && oldData.getData().length > 0) {

					boolean needJob = false;
					if (!needJob && webSocket != null
							&& !webSocket.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "websocket"))) {
						needJob = true;
					} else if (!needJob && webWorker != null
							&& !webWorker.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "webWorker"))) {
						needJob = true;
					} else if (!needJob && devToolRule__trust != null && !devToolRule__trust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "devToolRule__trust"))) {
						needJob = true;
					} else if (!needJob && downloadRule__trust != null && !downloadRule__trust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "downloadRule__trust"))) {
						needJob = true;
					} else if (!needJob && printRule__trust != null && !printRule__trust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "printRule__trust"))) {
						needJob = true;
					} else if (!needJob && viewSourceRule__trust != null && !viewSourceRule__trust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "viewSourceRule__trust"))) {
						needJob = true;
					} else if (!needJob && devToolRule__untrust != null && !devToolRule__untrust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "devToolRule__untrust"))) {
						needJob = true;
					} else if (!needJob && downloadRule__untrust != null && !downloadRule__untrust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "downloadRule__untrust"))) {
						needJob = true;
					} else if (!needJob && printRule__untrust != null && !printRule__untrust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "printRule__untrust"))) {
						needJob = true;
					} else if (!needJob && viewSourceRule__untrust != null && !viewSourceRule__untrust
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "viewSourceRule__untrust"))) {
						needJob = true;
					} else if (!needJob && trustSetup != null
							&& !trustSetup.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "trustSetup"))) {
						needJob = true;
					} else if (!needJob && untrustSetup != null && !untrustSetup
							.equals(getPropertyValue((CtrlItemVO) oldData.getData()[0], "untrustSetup"))) {
						needJob = true;

					} else if (!needJob && trustUrlList != null && (!CommonUtils.isEqualArrayProperties(trustUrlList,
							(CtrlItemVO) oldData.getData()[0], "trust"))) {
						needJob = true;
					} else if (!needJob && trustGroupList != null && (!CommonUtils
							.isEqualArrayProperties(trustGroupList, (CtrlItemVO) oldData.getData()[0], "trustgroup"))) {
						needJob = true;
					}

					if (needJob) {
						jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_BROWSER_RULE_CHANGE,
								GPMSConstants.TYPE_BROWSERRULE);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in updateBrowserRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * get client media rule list data paged.
	 * 
	 * @param req                    HttpServletRequest
	 * @param resHttpServletResponse
	 * @param modelModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRuleListPaged")
	public @ResponseBody ResultPagingVO readMediaRuleListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE);
	}

	/**
	 * response client media rule list data.
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRuleList")
	public @ResponseBody ResultVO readMediaRuleList() {

		ResultVO resultVO = new ResultVO();

		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE);
		} catch (Exception ex) {
			logger.error("error in readMediaRuleList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client media rule data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRule")
	public @ResponseBody ResultVO readMediaRule(HttpServletRequest req) {

		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");
		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readMediaRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client media rule data by user id.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRuleByUserId")
	public @ResponseBody ResultVO readMediaRuleByUserId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String userId = req.getParameter("userId");
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getMediaRuleId() != null && vo.getMediaRuleId().length() > 0) {
						resultVO = getMediaRuleByRoleId(vo.getMediaRuleId(), GPMSConstants.RULE_GRADE_USER);
						return resultVO;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getMediaRuleId() != null && vo.getMediaRuleId().length() > 0) {
						resultVO = getMediaRuleByRoleId(vo.getMediaRuleId(), GPMSConstants.RULE_GRADE_DEPT);
						return resultVO;
					}
				}

				resultVO = getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readMediaRuleByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client media rule data by dept code and if no exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRuleByDeptCd")
	public @ResponseBody ResultVO readMediaRuleByDeptCd(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getMediaRuleId() != null && vo.getMediaRuleId().length() > 0) {
						resultVO = getMediaRuleByRoleId(vo.getMediaRuleId(), GPMSConstants.RULE_GRADE_DEPT);
					} else {
						resultVO = getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readMediaRuleByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response client media rule data by group id and if no exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readMediaRuleByGroupId")
	public @ResponseBody ResultVO readMediaRuleByGroupId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getMediaRuleId() != null && vo.getMediaRuleId().length() > 0) {
						resultVO = getMediaRuleByRoleId(vo.getMediaRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						resultVO = getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getMediaRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readMediaRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	private ResultVO getMediaRuleByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
		} catch (Exception ex) {
			logger.error("error in getMediaRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new client media rule data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createMediaRuleConf")
	public @ResponseBody ResultVO createMediaRuleConf(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String paramUsbMemory = req.getParameter(GPMSConstants.MEDIA_ITEM_USB_MEMORY);
		String paramCdDvd = req.getParameter(GPMSConstants.MEDIA_ITEM_CD_DVD);
		String paramPrinter = req.getParameter(GPMSConstants.MEDIA_ITEM_PRINTER);

		String paramCamera = req.getParameter(GPMSConstants.MEDIA_ITEM_CAMERA);
		String paramMicrophone = req.getParameter(GPMSConstants.MEDIA_ITEM_MICROPHONE);
		String paramSound = req.getParameter(GPMSConstants.MEDIA_ITEM_SOUND);
		String paramScreenCapture = req.getParameter(GPMSConstants.MEDIA_ITEM_SCREEN_CAPTURE);
		String paramClipboard = req.getParameter(GPMSConstants.MEDIA_ITEM_CLIPBOARD);
		String paramKeyboard = req.getParameter(GPMSConstants.MEDIA_ITEM_KEYBOARD);
		String paramMouse = req.getParameter(GPMSConstants.MEDIA_ITEM_MOUSE);

		String paramWireless = req.getParameter(GPMSConstants.MEDIA_ITEM_WIRELESS);
		String paramBluetoothState = req.getParameter(GPMSConstants.MEDIA_ITEM_BLUETOOTH_STATE);
		String[] paramMacAddressList = req.getParameterValues("macAddressList[]");
		String[] paramUsbSerialNoList = req.getParameterValues("usbSerialNoList[]");

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_USB_MEMORY,
					paramUsbMemory, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CD_DVD, paramCdDvd, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_PRINTER, paramPrinter,
					"", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CAMERA, paramCamera, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MICROPHONE,
					paramMicrophone, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_SOUND, paramSound, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_SCREEN_CAPTURE,
					paramScreenCapture, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CLIPBOARD,
					paramClipboard, "", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_KEYBOARD, paramKeyboard,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MOUSE, paramMouse, "",
					LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_WIRELESS, paramWireless,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_BLUETOOTH_STATE,
					paramBluetoothState, "", LoginInfoHelper.getUserId(), ""));

			if (paramMacAddressList != null && paramMacAddressList.length > 0) {
				for (String paramMacAddress : paramMacAddressList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MAC_ADDRESS,
							paramMacAddress, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			if (paramUsbSerialNoList != null && paramUsbSerialNoList.length > 0) {
				for (String paramUsbSerialNo : paramUsbSerialNoList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_USB_SERIALNO,
							paramUsbSerialNo, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createMediaRuleConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned media rule data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneMediaRule")
	public @ResponseBody ResultVO cloneMediaRule(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneMediaRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * delete client media rule data.
	 * 
	 * @param objId String object id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteMediaRuleConf")
	public @ResponseBody ResultVO deleteMediaRuleConf(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_MEDIARULE);
			// delete configuration
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_MEDIARULE);
			resultVO.setStatus(status);

			// [JOBCREATE]
			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_MEDIA_RULE_CHANGE, clientArray);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("error in deleteMediaRuleConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * update client media rule data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateMediaRuleConf")
	public @ResponseBody ResultVO updateMediaRuleConf(HttpServletRequest req) {

		String objId = req.getParameter("objId");
		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");

		String paramUsbMemory = req.getParameter(GPMSConstants.MEDIA_ITEM_USB_MEMORY);
		String paramCdDvd = req.getParameter(GPMSConstants.MEDIA_ITEM_CD_DVD);
		String paramPrinter = req.getParameter(GPMSConstants.MEDIA_ITEM_PRINTER);

		String paramCamera = req.getParameter(GPMSConstants.MEDIA_ITEM_CAMERA);
		// save sound option in microphone for while. hmkim. RESET :
		String paramMicrophone = req.getParameter(GPMSConstants.MEDIA_ITEM_MICROPHONE);
		String paramSound = req.getParameter(GPMSConstants.MEDIA_ITEM_SOUND);
		String paramScreenCapture = req.getParameter(GPMSConstants.MEDIA_ITEM_SCREEN_CAPTURE);
		String paramClipboard = req.getParameter(GPMSConstants.MEDIA_ITEM_CLIPBOARD);

		String paramKeyboard = req.getParameter(GPMSConstants.MEDIA_ITEM_KEYBOARD);
		String paramMouse = req.getParameter(GPMSConstants.MEDIA_ITEM_MOUSE);

		String paramWireless = req.getParameter(GPMSConstants.MEDIA_ITEM_WIRELESS);
		String paramBluetoothState = req.getParameter(GPMSConstants.MEDIA_ITEM_BLUETOOTH_STATE);
		String[] paramMacAddressList = req.getParameterValues("macAddressList[]");
		String[] paramUsbSerialNoList = req.getParameterValues("usbSerialNoList[]");

		ResultVO resultVO = new ResultVO();

		try {

			// read before data.
			ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE_ABBR);

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_USB_MEMORY,
					paramUsbMemory, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CD_DVD, paramCdDvd, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_PRINTER, paramPrinter,
					"", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CAMERA, paramCamera, "",
					LoginInfoHelper.getUserId(), ""));
			// microphone == sound
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MICROPHONE,
					paramMicrophone, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_SOUND, paramSound, "",
					LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_SCREEN_CAPTURE,
					paramScreenCapture, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_CLIPBOARD,
					paramClipboard, "", LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_KEYBOARD, paramKeyboard,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MOUSE, paramMouse, "",
					LoginInfoHelper.getUserId(), ""));

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_WIRELESS, paramWireless,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_BLUETOOTH_STATE,
					paramBluetoothState, "", LoginInfoHelper.getUserId(), ""));

			if (paramMacAddressList != null && paramMacAddressList.length > 0) {
				for (String paramMacAddress : paramMacAddressList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_MAC_ADDRESS,
							paramMacAddress, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			if (paramUsbSerialNoList != null && paramUsbSerialNoList.length > 0) {
				for (String paramUsbSerialNo : paramUsbSerialNoList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.MEDIA_ITEM_USB_SERIALNO,
							paramUsbSerialNo, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// Create Job if value changed (compare oldData
				if (oldData != null && oldData.getData() != null && oldData.getData().length > 0) {
					boolean needJob = false;
					if (!needJob && paramUsbMemory != null
							&& !paramUsbMemory.equals(CommonUtils.getPropertyValue((CtrlItemVO) oldData.getData()[0],
									GPMSConstants.MEDIA_ITEM_USB_MEMORY))) {
						needJob = true;
					} else if (!needJob && paramCdDvd != null && !paramCdDvd.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_CD_DVD))) {
						needJob = true;
					} else if (!needJob && paramPrinter != null && !paramPrinter.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_PRINTER))) {
						needJob = true;
					} else if (!needJob && paramCamera != null && !paramCamera.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_CAMERA))) {
						needJob = true;
					} else if (!needJob && paramMicrophone != null
							&& !paramMicrophone.equals(CommonUtils.getPropertyValue((CtrlItemVO) oldData.getData()[0],
									GPMSConstants.MEDIA_ITEM_MICROPHONE))) {
						needJob = true;
					} else if (!needJob && paramSound != null && !paramSound.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_SOUND))) {
						needJob = true;
					} else if (!needJob && paramScreenCapture != null
							&& !paramScreenCapture.equals(CommonUtils.getPropertyValue(
									(CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_SCREEN_CAPTURE))) {
						needJob = true;
					} else if (!needJob && paramClipboard != null && !paramClipboard.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_CLIPBOARD))) {
						needJob = true;
					} else if (!needJob && paramKeyboard != null && !paramKeyboard.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_KEYBOARD))) {
						needJob = true;
					} else if (!needJob && paramMouse != null && !paramMouse.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_MOUSE))) {
						needJob = true;

					} else if (!needJob && paramWireless != null && !paramWireless.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_WIRELESS))) {
						needJob = true;
					} else if (!needJob && paramBluetoothState != null
							&& !paramBluetoothState.equals(CommonUtils.getPropertyValue(
									(CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_BLUETOOTH_STATE))) {
						needJob = true;
					}

					// macAddress
					if (paramMacAddressList != null) {
						if (!needJob && (!CommonUtils.isEqualArrayProperties(paramMacAddressList,
								(CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_MAC_ADDRESS))) {
							needJob = true;
						}
					} else {
						ArrayList<String> re = CommonUtils.getPropertyValues((CtrlItemVO) oldData.getData()[0],
								GPMSConstants.MEDIA_ITEM_MAC_ADDRESS);
						if (re != null && re.size() > 0) {
							needJob = true;
						}
					}

					// UsbSerialNo
					if (paramUsbSerialNoList != null) {
						if (!needJob && (!CommonUtils.isEqualArrayProperties(paramUsbSerialNoList,
								(CtrlItemVO) oldData.getData()[0], GPMSConstants.MEDIA_ITEM_USB_SERIALNO))) {
							needJob = true;
						}
					} else {
						ArrayList<String> re = CommonUtils.getPropertyValues((CtrlItemVO) oldData.getData()[0],
								GPMSConstants.MEDIA_ITEM_USB_SERIALNO);
						if (re != null && re.size() > 0) {
							needJob = true;
						}
					}

					if (needJob) {
						jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_MEDIA_RULE_CHANGE,
								GPMSConstants.TYPE_MEDIARULE);
					}
				}
			}

			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in updateMediaRuleConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * create client security rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createSecurityRule")
	public @ResponseBody ResultVO createSecurityRule(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String screen_time = req.getParameter(GPMSConstants.CTRL_ITEM_SCREENTIME);
		String password_time = req.getParameter(GPMSConstants.CTRL_ITEM_PASSWORDTIME);
		String package_handle = req.getParameter(GPMSConstants.CTRL_ITEM_PACKAGEHANDLE);
		String global_network = req.getParameter(GPMSConstants.CTRL_ITEM_GLOBALNETWORK);
		String[] firewall_network = req.getParameterValues(GPMSConstants.CTRL_ITEM_FIREWAllNETWORK + "[]");

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_GRSECU_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_SCREENTIME, screen_time,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PASSWORDTIME,
					password_time, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PACKAGEHANDLE,
					package_handle, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_GLOBALNETWORK,
					global_network, "", LoginInfoHelper.getUserId(), ""));
			if (firewall_network != null && firewall_network.length > 0) {
				for (String item : firewall_network) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_FIREWAllNETWORK,
							item, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned security rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneSecurityRule")
	public @ResponseBody ResultVO cloneSecurityRule(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_GRSECU_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom security rule list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRuleList")
	public @ResponseBody ResultVO readSecurityRuleList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_GRSECU_RULE);
		} catch (Exception ex) {
			logger.error("error in readSecurityRuleList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get client security rule (new) list data paged.
	 * 
	 * @param req                    HttpServletRequest
	 * @param resHttpServletResponse
	 * @param modelModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRuleListPaged")
	public @ResponseBody ResultPagingVO readSecurityRuleListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_GRSECU_RULE);
	}

	/**
	 * response gooroom security rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRule")
	public @ResponseBody ResultVO readSecurityRule(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");
		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom security rule information data by user id.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRuleByUserId")
	public @ResponseBody ResultVO readSecurityRuleByUserId(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		ResultVO resultVO = new ResultVO();
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getSecurityRuleId() != null && vo.getSecurityRuleId().length() > 0) {
						resultVO = getSecurityRuleByRoleId(vo.getSecurityRuleId(), GPMSConstants.RULE_GRADE_USER);
						return resultVO;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getSecurityRuleId() != null && vo.getSecurityRuleId().length() > 0) {
						resultVO = getSecurityRuleByRoleId(vo.getSecurityRuleId(), GPMSConstants.RULE_GRADE_DEPT);
						return resultVO;
					}
				}

				resultVO = getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readSecurityRuleByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom security rule information data by dept code and if no exist,
	 * return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRuleByDeptCd")
	public @ResponseBody ResultVO readSecurityRuleByDeptCd(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getSecurityRuleId() != null && vo.getSecurityRuleId().length() > 0) {
						resultVO = getSecurityRuleByRoleId(vo.getSecurityRuleId(), GPMSConstants.RULE_GRADE_DEPT);
					} else {
						resultVO = getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readSecurityRuleByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom security rule information data by group id and if no exist,
	 * return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSecurityRuleByGroupId")
	public @ResponseBody ResultVO readSecurityRuleByGroupId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getSecurityRuleId() != null && vo.getSecurityRuleId().length() > 0) {
						resultVO = getSecurityRuleByRoleId(vo.getSecurityRuleId(), GPMSConstants.RULE_GRADE_GROUP);
					} else {
						resultVO = getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getSecurityRuleByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readSecurityRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	private ResultVO getSecurityRuleByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_GRSECU_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}
		} catch (Exception ex) {
			logger.error("error in getSecurityRuleByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * edit gooroom security rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateSecurityRule")
	public @ResponseBody ResultVO updateSecurityRule(HttpServletRequest req) {

		String objId = StringUtils.defaultString(req.getParameter("objId"));
		String objName = StringUtils.defaultString(req.getParameter("objName"));
		String objComment = StringUtils.defaultString(req.getParameter("objComment"));

		String screen_time = StringUtils.defaultString(req.getParameter(GPMSConstants.CTRL_ITEM_SCREENTIME));
		String password_time = StringUtils.defaultString(req.getParameter(GPMSConstants.CTRL_ITEM_PASSWORDTIME));
		String package_handle = StringUtils.defaultString(req.getParameter(GPMSConstants.CTRL_ITEM_PACKAGEHANDLE));
		String global_network = StringUtils.defaultString(req.getParameter(GPMSConstants.CTRL_ITEM_GLOBALNETWORK));
		String[] firewall_network = req.getParameterValues(GPMSConstants.CTRL_ITEM_FIREWAllNETWORK + "[]");

		ResultVO resultVO = new ResultVO();

		try {

			// read before data.
			ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_GRSECU_RULE);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_SCREENTIME, screen_time,
					"", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PASSWORDTIME,
					password_time, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_PACKAGEHANDLE,
					package_handle, "", LoginInfoHelper.getUserId(), ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_GLOBALNETWORK,
					global_network, "", LoginInfoHelper.getUserId(), ""));
			if (firewall_network != null && firewall_network.length > 0) {
				for (String item : firewall_network) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_FIREWAllNETWORK,
							item, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {

				if (oldData != null && oldData.getData() != null && oldData.getData().length > 0) {

					if (screen_time != null && !screen_time.equals(CommonUtils
							.getPropertyValue((CtrlItemVO) oldData.getData()[0], GPMSConstants.CTRL_ITEM_SCREENTIME))) {
						jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_CLIENTSECU_SCREENTIME_CHANGE,
								GPMSConstants.TYPE_SECURITYRULE);
					}

					if (password_time != null
							&& !password_time.equals(CommonUtils.getPropertyValue((CtrlItemVO) oldData.getData()[0],
									GPMSConstants.CTRL_ITEM_PASSWORDTIME))) {
						jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_CLIENTSECU_PASSWORDTIME_CHANGE,
								GPMSConstants.TYPE_SECURITYRULE);
					}

					if (package_handle != null
							&& !package_handle.equals(CommonUtils.getPropertyValue((CtrlItemVO) oldData.getData()[0],
									GPMSConstants.CTRL_ITEM_PACKAGEHANDLE))) {
						jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE,
								GPMSConstants.TYPE_SECURITYRULE);
					}
				}

				jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_MEDIA_RULE_CHANGE,
						GPMSConstants.TYPE_SECURITYRULE);
			}

		} catch (Exception ex) {
			logger.error("error in updateSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete browser rule configuration data
	 * 
	 * @param objId string id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteSecurityRule")
	public @ResponseBody ResultVO deleteSecurityRule(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_SECURITYRULE);
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_SECURITYRULE);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_SCREENTIME_CHANGE, clientArray);
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_PASSWORDTIME_CHANGE, clientArray);
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE, clientArray);
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_MEDIA_RULE_CHANGE, clientArray);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("error in deleteSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * 
	 * #######################################################################################################
	 * response gooroom software filter information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilter")
	public @ResponseBody ResultVO readSoftwareFilter(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");
		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readSoftwareFilter : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom software filter information data by dept code and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilterByDeptCd")
	public @ResponseBody ResultVO readSoftwareFilterByDeptCd(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getFilteredSoftwareRuleId() != null && vo.getFilteredSoftwareRuleId().length() > 0) {
						resultVO = getSoftwareFilterByRoleId(vo.getFilteredSoftwareRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
					} else {
						resultVO = getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readSoftwareFilterByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom software filter information data by group id and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilterByGroupId")
	public @ResponseBody ResultVO readSoftwareFilterByGroupId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getFilteredSoftwareRuleId() != null && vo.getFilteredSoftwareRuleId().length() > 0) {
						resultVO = getSoftwareFilterByRoleId(vo.getFilteredSoftwareRuleId(),
								GPMSConstants.RULE_GRADE_GROUP);
					} else {
						resultVO = getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readSoftwareFilterByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom software filter information data by user id.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilterByUserId")
	public @ResponseBody ResultVO readSoftwareFilterByUserId(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		ResultVO resultVO = new ResultVO();
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getFilteredSoftwareRuleId() != null && vo.getFilteredSoftwareRuleId().length() > 0) {
						resultVO = getSoftwareFilterByRoleId(vo.getFilteredSoftwareRuleId(),
								GPMSConstants.RULE_GRADE_USER);
						return resultVO;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getFilteredSoftwareRuleId() != null && vo.getFilteredSoftwareRuleId().length() > 0) {
						resultVO = getSoftwareFilterByRoleId(vo.getFilteredSoftwareRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
						return resultVO;
					}
				}

				resultVO = getSoftwareFilterByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readSoftwareFilterByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom software filter list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilterList")
	public @ResponseBody ResultVO readSoftwareFilterList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_SWFILTER_RULE);
		} catch (Exception ex) {
			logger.error("error in readSoftwareFilterList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get client software filter (new) list data paged.
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readSoftwareFilterListPaged")
	public @ResponseBody ResultPagingVO readSoftwareFilterListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_SWFILTER_RULE);
	}

	private ResultVO getSoftwareFilterByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getSoftwareFilterByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create client software filter data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createSoftwareFilter")
	public @ResponseBody ResultVO createSoftwareFilter(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String[] swList = req.getParameterValues("swList[]");

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_SWFILTER_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			if (swList != null && swList.length > 0) {
				for (String sw : swList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_FILTEREDSOFTWARE,
							sw, "", LoginInfoHelper.getUserId(), CommonUtils.getCtrlItemType(sw)));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * edit gooroom filtered software information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateSoftwareFilter")
	public @ResponseBody ResultVO updateSoftwareFilter(HttpServletRequest req) {

		String objId = StringUtils.defaultString(req.getParameter("objId"));
		String objName = StringUtils.defaultString(req.getParameter("objName"));
		String objComment = StringUtils.defaultString(req.getParameter("objComment"));

		String[] swList = req.getParameterValues("swList[]");

		ResultVO resultVO = new ResultVO();

		try {

			// read before data.
			// ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_SWFILTER_RULE);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			if (swList != null && swList.length > 0) {
				for (String sw : swList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_FILTEREDSOFTWARE,
							sw, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// 이전값과 비교 할지????
				jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_FILTEREDSOFTWARE_RULE_CHANGE,
						GPMSConstants.TYPE_FILTEREDSOFTWARE);
			}

		} catch (Exception ex) {
			logger.error("error in updateSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete filtered software rule data
	 * 
	 * @param objId string id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteSoftwareFilter")
	public @ResponseBody ResultVO deleteSoftwareFilter(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_FILTEREDSOFTWARE);
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_FILTEREDSOFTWARE);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_FILTEREDSOFTWARE_RULE_CHANGE, clientArray);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in deleteSecurityRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned security rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneSoftwareFilter")
	public @ResponseBody ResultVO cloneSoftwareFilter(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_SWFILTER_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_SWFILTER_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneSoftwareFilter : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}
	
	/**
	 * 
	 * #######################################################################################################
	 * response gooroom control center item information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItem")
	public @ResponseBody ResultVO readCtrlCenterItem(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");
		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readCtrlCenterItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom control center item data by dept code and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItemByDeptCd")
	public @ResponseBody ResultVO readCtrlCenterItemByDeptCd(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getCtrlCenterItemRuleId() != null && vo.getCtrlCenterItemRuleId().length() > 0) {
						resultVO = getCtrlCenterItemByRoleId(vo.getCtrlCenterItemRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
					} else {
						resultVO = getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readCtrlCenterItemByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom control center item data by group id and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItemByGroupId")
	public @ResponseBody ResultVO readCtrlCenterItemByGroupId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getCtrlCenterItemRuleId() != null && vo.getCtrlCenterItemRuleId().length() > 0) {
						resultVO = getCtrlCenterItemByRoleId(vo.getCtrlCenterItemRuleId(),
								GPMSConstants.RULE_GRADE_GROUP);
					} else {
						resultVO = getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readCtrlCenterItemByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom control center item data by user id.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItemByUserId")
	public @ResponseBody ResultVO readCtrlCenterItemByUserId(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		ResultVO resultVO = new ResultVO();
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getCtrlCenterItemRuleId() != null && vo.getCtrlCenterItemRuleId().length() > 0) {
						resultVO = getCtrlCenterItemByRoleId(vo.getCtrlCenterItemRuleId(),
								GPMSConstants.RULE_GRADE_USER);
						return resultVO;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getCtrlCenterItemRuleId() != null && vo.getCtrlCenterItemRuleId().length() > 0) {
						resultVO = getCtrlCenterItemByRoleId(vo.getCtrlCenterItemRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
						return resultVO;
					}
				}

				resultVO = getCtrlCenterItemByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readCtrlCenterItemByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
	
	/**
	 * response control center item list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItemList")
	public @ResponseBody ResultVO readCtrlCenterItemList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE);
		} catch (Exception ex) {
			logger.error("error in readCtrlCenterItemList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get control center item list data paged.
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readCtrlCenterItemListPaged")
	public @ResponseBody ResultPagingVO readCtrlCenterItemListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE);
	}

	private ResultVO getCtrlCenterItemByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getCtrlCenterItemByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create control center item data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createCtrlCenterItem")
	public @ResponseBody ResultVO createCtrlCenterItem(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");

		String[] itemList = req.getParameterValues("itemList[]");

		ResultVO resultVO = new ResultVO();

		try {

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;

			if (itemList != null && itemList.length > 0) {
				for (String item : itemList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_CTRLCENTERITEM,
							item, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createCtrlCenterItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * edit gooroom control center item data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateCtrlCenterItem")
	public @ResponseBody ResultVO updateCtrlCenterItem(HttpServletRequest req) {

		String objId = StringUtils.defaultString(req.getParameter("objId"));
		String objName = StringUtils.defaultString(req.getParameter("objName"));
		String objComment = StringUtils.defaultString(req.getParameter("objComment"));

		String[] itemList = req.getParameterValues("itemList[]");

		ResultVO resultVO = new ResultVO();

		try {

			// read before data.
			// ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE);
			itemVo.setComment(objComment);
			itemVo.setModUserId(LoginInfoHelper.getUserId());

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			if (itemList != null && itemList.length > 0) {
				for (String item : itemList) {
					propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.CTRL_ITEM_CTRLCENTERITEM,
							item, "", LoginInfoHelper.getUserId(), ""));
				}
			}

			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// 이전값과 비교 할지????
				jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_CTRLCENTERITEMS_RULE_CHANGE,
						GPMSConstants.TYPE_CTRLCENTERITEMRULE);
			}

		} catch (Exception ex) {
			logger.error("error in updateCtrlCenterItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete control center item data
	 * 
	 * @param objId string id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deleteCtrlCenterItem")
	public @ResponseBody ResultVO deleteCtrlCenterItem(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_CTRLCENTERITEMRULE);
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_CTRLCENTERITEMRULE);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_CTRLCENTERITEMS_RULE_CHANGE, clientArray);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in deleteCtrlCenterItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned control center item rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/cloneCtrlCenterItem")
	public @ResponseBody ResultVO cloneCtrlCenterItem(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in cloneCtrlCenterItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}


	
	/**
	 * 
	 * #######################################################################################################
	 * response gooroom policy kit information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRule")
	public @ResponseBody ResultVO readPolicyKitRule(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String objId = req.getParameter("objId");
		try {
			resultVO = ctrlMstService.readCtrlItem(objId);
		} catch (Exception ex) {
			logger.error("error in readPolicyKitRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response gooroom policy kit data by dept code and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRuleByDeptCd")
	public @ResponseBody ResultVO readPolicyKitRuleByDeptCd(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String deptCd = req.getParameter("deptCd");
		try {
			if (deptCd != null && deptCd.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByDeptCd(deptCd);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getPolicyKitRuleId() != null && vo.getPolicyKitRuleId().length() > 0) {
						resultVO = getPolicyKitByRoleId(vo.getPolicyKitRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
					} else {
						resultVO = getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readPolicyKitRuleByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom policy kit data by group id and if no
	 * exist, return default
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRuleByGroupId")
	public @ResponseBody ResultVO readPolicyKitRuleByGroupId(HttpServletRequest req) {
		ResultVO resultVO = new ResultVO();
		String groupId = req.getParameter("groupId");
		try {
			if (groupId != null && groupId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByGroupId(groupId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getPolicyKitRuleId() != null && vo.getPolicyKitRuleId().length() > 0) {
						resultVO = getPolicyKitByRoleId(vo.getPolicyKitRuleId(),
								GPMSConstants.RULE_GRADE_GROUP);
					} else {
						resultVO = getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
					}
				} else {
					resultVO = getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
				}
			}
		} catch (Exception ex) {
			logger.error("error in readPolicyKitRuleByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response gooroom policy kit data by user id.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRuleByUserId")
	public @ResponseBody ResultVO readPolicyKitRuleByUserId(HttpServletRequest req) {
		String userId = req.getParameter("userId");
		ResultVO resultVO = new ResultVO();
		try {
			if (userId != null && userId.trim().length() > 0) {
				ResultVO userRoleInfo = userService.getUserConfIdByUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getPolicyKitRuleId() != null && vo.getPolicyKitRuleId().length() > 0) {
						resultVO = getPolicyKitByRoleId(vo.getPolicyKitRuleId(),
								GPMSConstants.RULE_GRADE_USER);
						return resultVO;
					}
				}

				// get role by deptCd in user
				userRoleInfo = userService.getUserConfIdByDeptCdFromUserId(userId);
				if (GPMSConstants.MSG_SUCCESS.equals(userRoleInfo.getStatus().getResult())) {
					UserRoleVO vo = (UserRoleVO) userRoleInfo.getData()[0];
					if (vo.getPolicyKitRuleId() != null && vo.getPolicyKitRuleId().length() > 0) {
						resultVO = getPolicyKitByRoleId(vo.getPolicyKitRuleId(),
								GPMSConstants.RULE_GRADE_DEPT);
						return resultVO;
					}
				}

				resultVO = getPolicyKitByRoleId(null, GPMSConstants.RULE_GRADE_DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("error in readPolicyKitRuleByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}
	
	/**
	 * response policy kit list data
	 * 
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRuleList")
	public @ResponseBody ResultVO readPolicyKitRuleList() {
		ResultVO resultVO = new ResultVO();
		try {
			resultVO = ctrlMstService.readCtrlItemAndPropList(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE);
		} catch (Exception ex) {
			logger.error("error in readPolicyKitRuleList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get policy kit list data paged.
	 * 
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param model ModelMap
	 * @return ResultPagingVO result data bean
	 *
	 */
	@PostMapping(value = "/readPolicyKitRuleListPaged")
	public @ResponseBody ResultPagingVO readPolicyKitRuleListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		return readCommonCtrlItemAndPropList(req, GPMSConstants.CTRL_ITEM_POLICYKIT_RULE);
	}

	private ResultVO getPolicyKitByRoleId(String roleId, String ruleGrade) {
		ResultVO resultVO = new ResultVO();
		try {
			if (roleId != null && roleId.trim().length() > 0) {
				resultVO = ctrlMstService.readCtrlItem(roleId);
				if (resultVO != null && resultVO.getData() != null && resultVO.getData().length > 0) {
					resultVO.setExtend(new String[] { ruleGrade });
				} else {
					resultVO = ctrlMstService
							.readCtrlItem(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
					resultVO.setExtend(new String[] { GPMSConstants.RULE_GRADE_DEFAULT });
				}
			} else {
				// default media rule.
				resultVO = ctrlMstService
						.readCtrlItem(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR + GPMSConstants.MSG_DEFAULT);
				resultVO.setExtend(new String[] { ruleGrade });
			}

		} catch (Exception ex) {
			logger.error("error in getPolicyKitByRoleId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create policy kit data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createPolicyKitRule")
	public @ResponseBody ResultVO createPolicyKitRule(HttpServletRequest req) {

		String objName = req.getParameter("objName");
		String objComment = req.getParameter("objComment");
		String adminType = req.getParameter("adminType");
		
		String paramGooroomUpdate = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMUPDATE);
		String paramGooroomAgent = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMAGENT);
		String paramGooroomRegister = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMREGISTER);
		String paramGracEditor = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GRACEDITOR);
		String paramWireWireless = req.getParameter(GPMSConstants.POLICYKIT_ITEM_WIREORWIRELESS);
		String paramNetworkConfig = req.getParameter(GPMSConstants.POLICYKIT_ITEM_NETWORK);
		String paramPrinter = req.getParameter(GPMSConstants.POLICYKIT_ITEM_PRINTER);
		String paramDiskMount = req.getParameter(GPMSConstants.POLICYKIT_ITEM_DISKMOUNT);
		String paramPkexec = req.getParameter(GPMSConstants.POLICYKIT_ITEM_ADMINEXEC);
		String paramPackageMng = req.getParameter(GPMSConstants.POLICYKIT_ITEM_PACKAGEMNG);

		ResultVO resultVO = new ResultVO();

		try {
			
			String regUserId = LoginInfoHelper.getUserId();

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(regUserId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				itemVo.setStandardObj(true);
			}

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMUPDATE, paramGooroomUpdate, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMAGENT, paramGooroomAgent, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMREGISTER, paramGooroomRegister, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GRACEDITOR, paramGracEditor, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_WIREORWIRELESS, paramWireWireless, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_NETWORK, paramNetworkConfig, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_PRINTER, paramPrinter, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_DISKMOUNT, paramDiskMount, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_ADMINEXEC, paramPkexec, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_PACKAGEMNG, paramPackageMng, "",
					regUserId, ""));

			
			
			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.createCtrlItem(itemVo, props);
			resultVO.setStatus(status);

		} catch (Exception ex) {
			logger.error("error in createPolicyKitRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * edit gooroom policy kit data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updatePolicyKitRule")
	public @ResponseBody ResultVO updatePolicyKitRule(HttpServletRequest req) {

		String objId = StringUtils.defaultString(req.getParameter("objId"));
		String objName = StringUtils.defaultString(req.getParameter("objName"));
		String objComment = StringUtils.defaultString(req.getParameter("objComment"));
		
		String paramGooroomUpdate = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMUPDATE);
		String paramGooroomAgent = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMAGENT);
		String paramGooroomRegister = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GOOROOMREGISTER);
		String paramGracEditor = req.getParameter(GPMSConstants.POLICYKIT_ITEM_GRACEDITOR);
		String paramWireWireless = req.getParameter(GPMSConstants.POLICYKIT_ITEM_WIREORWIRELESS);
		String paramNetworkConfig = req.getParameter(GPMSConstants.POLICYKIT_ITEM_NETWORK);
		String paramPrinter = req.getParameter(GPMSConstants.POLICYKIT_ITEM_PRINTER);
		String paramDiskMount = req.getParameter(GPMSConstants.POLICYKIT_ITEM_DISKMOUNT);
		String paramPkexec = req.getParameter(GPMSConstants.POLICYKIT_ITEM_ADMINEXEC);
		String paramPackageMng = req.getParameter(GPMSConstants.POLICYKIT_ITEM_PACKAGEMNG);

		ResultVO resultVO = new ResultVO();

		try {
			
			String regUserId = LoginInfoHelper.getUserId();

			// read before data.
			// ResultVO oldData = ctrlMstService.readCtrlItem(objId);

			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setObjNm(objName);
			itemVo.setComment(objComment);
			itemVo.setModUserId(regUserId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE);

			ArrayList<CtrlPropVO> propList = new ArrayList<CtrlPropVO>();
			int propSeq = 1;
			
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMUPDATE, paramGooroomUpdate, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMAGENT, paramGooroomAgent, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GOOROOMREGISTER, paramGooroomRegister, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_GRACEDITOR, paramGracEditor, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_WIREORWIRELESS, paramWireWireless, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_NETWORK, paramNetworkConfig, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_PRINTER, paramPrinter, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_DISKMOUNT, paramDiskMount, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_ADMINEXEC, paramPkexec, "",
					regUserId, ""));
			propList.add(new CtrlPropVO("", String.valueOf(propSeq++), GPMSConstants.POLICYKIT_ITEM_PACKAGEMNG, paramPackageMng, "",
					regUserId, ""));
			
			CtrlPropVO[] props = new CtrlPropVO[propList.size()];
			props = propList.toArray(props);

			StatusVO status = ctrlMstService.updateCtrlItem(itemVo, props);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				// 이전값과 비교 할지????
				jobMaker.createJobForUserConf(objId, GPMSConstants.JOB_POLICYKIT_RULE_CHANGE,
						GPMSConstants.TYPE_POLICYKITRULE);
			}

		} catch (Exception ex) {
			logger.error("error in updatePolicyKitRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete policy kit
	 * 
	 * @param objId string id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deletePolicyKitRule")
	public @ResponseBody ResultVO deletePolicyKitRule(@RequestParam(value = "objId", required = true) String objId) {

		ResultVO resultVO = new ResultVO();
		try {
			// get client id for job target (online client)
			ResultVO currentClients = clientService.getOnlineClientIdsInClientUseConfId(objId,
					GPMSConstants.TYPE_POLICYKITRULE);
			StatusVO status = ctrlMstService.deleteCtrlItem(objId, GPMSConstants.TYPE_RULE_USER,
					GPMSConstants.TYPE_POLICYKITRULE);
			resultVO.setStatus(status);

			if (GPMSConstants.MSG_SUCCESS.equals(status.getResult())) {
				if (GPMSConstants.MSG_SUCCESS.equals(currentClients.getStatus().getResult())) {
					String[] clientArray = null;
					ClientVO[] row = (ClientVO[]) currentClients.getData();
					if (row.length > 0) {
						clientArray = new String[row.length];
						for (int i = 0; i < row.length; i++) {
							clientArray[i] = row[i].getClientId();
						}
						jobMaker.createJobWithClientIds(GPMSConstants.JOB_POLICYKIT_RULE_CHANGE, clientArray);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in deletePolicyKitRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create cloned policy kit rule information data.
	 * 
	 * @param req HttpServletRequest
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/clonePolicyKitRule")
	public @ResponseBody ResultVO clonePolicyKitRule(HttpServletRequest req) {
		String objId = req.getParameter("objId");
		ResultVO resultVO = new ResultVO();
		try {
			CtrlItemVO itemVo = new CtrlItemVO();
			itemVo.setObjId(objId);
			itemVo.setMngObjTp(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE);
			itemVo.setMngObjTpAbbr(GPMSConstants.CTRL_ITEM_POLICYKIT_RULE_ABBR);
			itemVo.setModUserId(LoginInfoHelper.getUserId());
			StatusVO status = ctrlMstService.cloneCtrlItem(itemVo);
			resultVO.setStatus(status);
		} catch (Exception ex) {
			logger.error("error in clonePolicyKitRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	
	@PostMapping(value = "/readTotalRule")
	public @ResponseBody HashMap<String, Object> readTotalRule(
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "clientId", required = true) String clientId, ModelMap model) {

		HashMap<String, Object> hm = new HashMap<String, Object>();

		try {
			// get rule ids
			ResultVO result = ctrlMstService.getRuleIdsByClientAndUser(userId, clientId);
			if (GPMSConstants.MSG_SUCCESS.equals(result.getStatus().getResult())) {
				RuleIdsVO reVO = (RuleIdsVO) result.getData()[0];

				// 1.
				if (reVO.getBrowserRuleId() != null && !"".equals(reVO.getBrowserRuleId())) {
					HashMap<String, Object> browserRuleMap = getBrowserRuleByRoleId(reVO.getBrowserRuleId(), "");
					if (browserRuleMap != null && browserRuleMap.get("data") != null
							&& ((Object[]) browserRuleMap.get("data")).length > 0) {
						hm.put("browserRuleMap", ((Object[]) browserRuleMap.get("data"))[0]);
					}
				}

				// 2.
				if (reVO.getMediaRuleId() != null && !"".equals(reVO.getMediaRuleId())) {
					ResultVO mediaRuleResult = getMediaRuleByRoleId(reVO.getMediaRuleId(), "");
					if (GPMSConstants.MSG_SUCCESS.equals(mediaRuleResult.getStatus().getResult())) {
						hm.put("mediaRuleMap", mediaRuleResult.getData()[0]);
					}
				}

				// 3.
				if (reVO.getSecurityRuleId() != null && !"".equals(reVO.getSecurityRuleId())) {
					ResultVO securityRuleResult = getSecurityRuleByRoleId(reVO.getSecurityRuleId(), "");
					if (GPMSConstants.MSG_SUCCESS.equals(securityRuleResult.getStatus().getResult())) {
						hm.put("securityRuleMap", securityRuleResult.getData()[0]);
					}
				}

				// 4.
				if (reVO.getFilteredSwRuleId() != null && !"".equals(reVO.getFilteredSwRuleId())) {
					ResultVO softwareFilterRuleResult = getSoftwareFilterByRoleId(reVO.getFilteredSwRuleId(), "");
					if (GPMSConstants.MSG_SUCCESS.equals(softwareFilterRuleResult.getStatus().getResult())) {
						hm.put("filteredSwRuleMap", softwareFilterRuleResult.getData()[0]);
					}
				}

				// 9.
				if (reVO.getCtrlCenterItemRuleId() != null && !"".equals(reVO.getCtrlCenterItemRuleId())) {
					ResultVO ctrlCenterItemRuleResult = getCtrlCenterItemByRoleId(reVO.getCtrlCenterItemRuleId(), "");
					if (GPMSConstants.MSG_SUCCESS.equals(ctrlCenterItemRuleResult.getStatus().getResult())) {
						hm.put("ctrlCenterItemRuleMap", ctrlCenterItemRuleResult.getData()[0]);
					}
				}

				// 10.
				if (reVO.getPolicyKitRuleId() != null && !"".equals(reVO.getPolicyKitRuleId())) {
					ResultVO policyKitRuleResult = getPolicyKitByRoleId(reVO.getPolicyKitRuleId(), "");
					if (GPMSConstants.MSG_SUCCESS.equals(policyKitRuleResult.getStatus().getResult())) {
						hm.put("policyKitRuleMap", policyKitRuleResult.getData()[0]);
					}
				}

				// 5.
				if (reVO.getHostNameConfId() != null && !"".equals(reVO.getHostNameConfId())) {
					ResultVO hostNameConfResult = ctrlMstService.readCtrlItem(reVO.getHostNameConfId());
					if (GPMSConstants.MSG_SUCCESS.equals(hostNameConfResult.getStatus().getResult())) {
						hm.put("hostNameConfMap", hostNameConfResult.getData()[0]);
					}
				}

				// 6.
				if (reVO.getUpdateServerConfId() != null && !"".equals(reVO.getUpdateServerConfId())) {
					ResultVO updateServerConfResult = ctrlMstService.readCtrlItem(reVO.getUpdateServerConfId());
					if (GPMSConstants.MSG_SUCCESS.equals(updateServerConfResult.getStatus().getResult())) {
						hm.put("updateServerConfMap", updateServerConfResult.getData()[0]);
					}
				}

				// 7.
				if (reVO.getClientConfId() != null && !"".equals(reVO.getClientConfId())) {
					ResultVO clientConfResult = ctrlMstService.readCtrlItem(reVO.getClientConfId());
					if (GPMSConstants.MSG_SUCCESS.equals(clientConfResult.getStatus().getResult())) {
						hm.put("clientConfMap", clientConfResult.getData()[0]);
					}
				}

				// 8.
				if (reVO.getDesktopConfId() != null && !"".equals(reVO.getDesktopConfId())) {
					ResultVO desktopConfResult = desktopConfService.getDesktopConfData(reVO.getDesktopConfId());
					if (GPMSConstants.MSG_SUCCESS.equals(desktopConfResult.getStatus().getResult())) {
						hm.put("desktopConfMap", desktopConfResult.getData()[0]);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in readTotalRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return hm;
	}

	/**
	 * 정책 - 적용 그룹 리스트
	 */
	@PostMapping(value = "/readActivateGroupList")
	public @ResponseBody ResultPagingVO readActivateGroupList(HttpServletRequest req, HttpServletResponse res,
																	ModelMap model) {
		ResultPagingVO resultVO = new ResultPagingVO();
		HashMap<String, Object> options = new HashMap<String, Object>();

		//gubun
		String gubun = req.getParameter("gubun"); //user, group, date
		// object id
		String confId = req.getParameter("objectId");
		//search keyword
		String searchKey = ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "");
		//date
		String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
		String toDate = StringUtils.defaultString(req.getParameter("toDate"));

		options.put("gubun", gubun);
		options.put("confId", confId);
		options.put("searchKey", searchKey);
		options.put("fromDate", fromDate);
		options.put("toDate", toDate);

		//paging
		String paramStart = StringUtils.defaultString(req.getParameter("start"), "0");
		String paramLength = StringUtils.defaultString(req.getParameter("length"), "10");
		String paramDraw = req.getParameter("draw");
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {
			resultVO = ctrlMstService.readActivateGroupListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setRowLength(paramLength);
			resultVO.setDraw(paramDraw);

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
}
