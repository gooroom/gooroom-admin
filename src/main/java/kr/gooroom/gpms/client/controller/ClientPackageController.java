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

import kr.gooroom.gpms.client.service.ClientPackageService;
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
 * Handles requests for the client package management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Controller
public class ClientPackageController {

	private static final Logger logger = LoggerFactory.getLogger(ClientPackageController.class);

	@Resource(name = "clientPackageService")
	private ClientPackageService clientPackageService;

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "jobService")
	private JobService jobService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * generate total package list data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readTotalPackageListPaged")
	public @ResponseBody ResultPagingVO readTotalPackageListPaged(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));

		// << paging >>
		options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
		options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");

		if ("chPackageId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_ID");
		} else if ("chPackageArch".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_ARCH");
		} else if ("chPackageLastVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_VER");
		} else {
			options.put("paramOrderColumn", "PACKAGE_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = clientPackageService.readTotalPackageListPaged(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(String.valueOf(options.get("paramLength")));
		} catch (Exception ex) {
			logger.error("error in readTotalPackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate package list data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readPackageListPagedInClient")
	public @ResponseBody ResultPagingVO readPackageListPagedInClient(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;
		HashMap<String, Object> options = new HashMap<String, Object>();

		// << options >>
		options.put("searchKey", ((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : ""));
		options.put("clientId", req.getParameter("clientId"));
		options.put("isFiltered", StringUtils.defaultString(req.getParameter("isFiltered"), "false"));

		// << paging >>
		options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
		options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

		// << ordering >>
		String paramOrderColumn = req.getParameter("orderColumn");
		String paramOrderDir = req.getParameter("orderDir");
		if ("chClientId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CP.CLIENT_ID");
		} else if ("chPackageId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CP.PACKAGE_ID");
		} else if ("chPackageArch".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CP.PACKAGE_ARCH");
		} else if ("chInstallVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CP.INSTALL_VER");
		} else if ("chPackageLastVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "CP.PACKAGE_LAST_VER");
		} else {
			options.put("paramOrderColumn", "CP.PACKAGE_ID");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}

		try {
			resultVO = clientPackageService.readPackageListPagedInClient(options);
			resultVO.setDraw(String.valueOf(req.getParameter("page")));
			resultVO.setOrderColumn(paramOrderColumn);
			resultVO.setOrderDir(paramOrderDir);
			resultVO.setRowLength(String.valueOf(options.get("paramLength")));
		} catch (Exception ex) {
			logger.error("error in readPackageListPagedInClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for update packages
	 * 
	 * @param groupId    string target group id
	 * @param clientId   string target client id
	 * @param packageIds string array package id array.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updatePackageInGroup")
	public @ResponseBody ResultVO updatePackageInGroup(
			@RequestParam(value = "groupIds", required = false) String groupIds,
			@RequestParam(value = "packageIds", required = true) String packageIds) {

		ResultVO resultVO = new ResultVO();

		try {
			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJobWithPackage("package", "install_or_upgrade_package", packageIds);
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientPackageController.updatePackageInGroup (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName("install_or_upgrade_package");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (groupIds != null && groupIds.length() > 0) {
				String[] groupIdArray = groupIds.split(",");
				for (int m = 0; m < groupIdArray.length; m++) {
					ResultVO clientResultVO = clientService.getClientListInGroup(groupIdArray[m]);
					ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
					for (int i = 0; i < clients.length; i++) {
						clientList.add(clients[i].getClientId());
					}
				}
			}

			String[] clientArray = new String[clientList.size()];
			clientArray = clientList.toArray(clientArray);

			jobVO.setClientIds(clientArray);

			resultVO.setStatus(jobService.createJob(jobVO));

		} catch (Exception ex) {
			logger.error("error in updatePackageInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for delete packages
	 * 
	 * @param groupId    string target group id
	 * @param clientId   string target client id
	 * @param packageIds string array package id array.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/deletePackageInClient")
	public @ResponseBody ResultVO deletePackageInClient(
			@RequestParam(value = "groupId", required = false) String groupId,
			@RequestParam(value = "clientId", required = true) String clientId,
			@RequestParam(value = "packageIds", required = true) String packageIds) {

		ResultVO resultVO = new ResultVO();

		try {

			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJobWithPackage("package", "remove_package", packageIds);
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientPackageController.deletePackageInClient (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName("remove_package");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			if (clientId != null && clientId.length() > 0) {

				jobVO.setClientIds(clientId.split(","));

			} else if (groupId != null && groupId.length() > 0) {

				ResultVO clientResultVO = clientService.getClientListInGroup(groupId);
				ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
				String[] clientArray = new String[clients.length];
				for (int i = 0; i < clients.length; i++) {
					clientArray[i] = clients[i].getClientId();
				}
				jobVO.setClientIds(clientArray);
			}

			resultVO.setStatus(jobService.createJob(jobVO));

		} catch (Exception ex) {
			logger.error("error in deletePackageInClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for all upgrade package
	 * 
	 * @param clientId string target client id
	 * @param groupId  string target group id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createPackageAllUpgrade")
	public @ResponseBody ResultVO createPackageAllUpgrade(
			@RequestParam(value = "clientId", required = false) String clientId,
			@RequestParam(value = "groupId", required = false) String groupId) {

		ResultVO resultVO = new ResultVO();

		try {

			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJob("package", "upgrade_all");
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientPackageController.createPackageAllUpgrade (make json) Exception occurred. ",
						jsonex);
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
			jobVO.setJobName("upgrade_all");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (groupId != null && groupId.length() > 0) {
				String[] groupIdArray = groupId.split(",");
				for (int m = 0; m < groupIdArray.length; m++) {
					ResultVO clientResultVO = clientService.getClientListInGroup(groupIdArray[m]);
					ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
					for (int i = 0; i < clients.length; i++) {
						clientList.add(clients[i].getClientId());
					}
				}
			}
			if (clientId != null && clientId.length() > 0) {
				String[] clientIdArray = clientId.split(",");
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
			logger.error("error in createPackageAllUpgrade : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for main OS upgrade package
	 * 
	 * @param clientId string target client id
	 * @param groupId  string target group id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createMainPackageUpgrade")
	public @ResponseBody ResultVO createMainPackageUpgrade(
			@RequestParam(value = "clientId", required = true) String clientId,
			@RequestParam(value = "groupId", required = true) String groupId) {

		ResultVO resultVO = new ResultVO();

		try {

			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJobWithLabel("package", "upgrade_package_with_label", "gooroom");
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientPackageController.createMainPackageUpgrade (make json) Exception occurred. ",
						jsonex);
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
			jobVO.setJobName("upgrade_package_with_label");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			if (clientId != null && clientId.length() > 0) {

				jobVO.setClientIds(clientId.split(","));

			} else if (groupId != null && groupId.length() > 0) {

				ResultVO clientResultVO = clientService.getClientListInGroup(groupId);
				ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
				String[] clientArray = new String[clients.length];
				for (int i = 0; i < clients.length; i++) {
					clientArray[i] = clients[i].getClientId();
				}
				jobVO.setClientIds(clientArray);
			}

			resultVO.setStatus(jobService.createJob(jobVO));

		} catch (Exception ex) {
			logger.error("error in createMainPackageUpgrade : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for update total package list
	 * 
	 * @param clientId string target client id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updateTotalPackage")
	public @ResponseBody ResultVO updateTotalPackage(
			@RequestParam(value = "clientId", required = false) String clientId) {

		ResultVO resultVO = new ResultVO();

		try {

			// make job
			Job[] jobs = new Job[1];
			jobs[0] = Job.generateJob("package", "insert_all_packages_to_server");

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("ClientPackageController.updateTotalPackage (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName("insert_all_packages_to_server");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// setup target data
			String[] clientArray = null;
			clientArray = new String[1];
			if (clientId == null || clientId.length() < 1) {
				// select one client id in online clients randomly.
				ResultVO clients = clientService.getClientListInOnline();
				if (clients != null && clients.getData() != null && clients.getData().length > 0) {
					clientArray[0] = ((ClientVO) clients.getData()[0]).getClientId();

				} else {
					throw new Exception("error");
				}

			} else {
				// setup client id that selected by administrator.
				clientArray[0] = clientId;
			}

			jobVO.setClientIds(clientArray);

			resultVO.setStatus(jobService.createJob(jobVO));

		} catch (Exception ex) {
			logger.error("error in updateTotalPackage : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate total package list data.
	 * 
	 * @param req   HttpServletRequest
	 * @param res   HttpServletResponse
	 * @param model ModelMap
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/readTotalPackageList")
	public @ResponseBody ResultPagingVO readTotalPackageList(HttpServletRequest req, HttpServletResponse res,
			ModelMap model) {

		ResultPagingVO resultVO = null;

		String searchKey = req.getParameter("searchKey");
		String paramOrderColumn = req.getParameter("columns[" + req.getParameter("order[0][column]") + "][data]");
		String paramOrderDir = req.getParameter("order[0][dir]");
		String paramStart = req.getParameter("start");
		String paramLength = req.getParameter("length");
		String paramDraw = req.getParameter("draw");

		HashMap<String, Object> options = new HashMap<String, Object>();

		options.put("searchKey", searchKey);

		if ("packageId".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_ID");
		} else if ("packageArch".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_ARCH");
		} else if ("packageLastVer".equalsIgnoreCase(paramOrderColumn)) {
			options.put("paramOrderColumn", "PACKAGE_VER");
		}

		if ("DESC".equalsIgnoreCase(paramOrderDir)) {
			options.put("paramOrderDir", "DESC");
		} else {
			options.put("paramOrderDir", "ASC");
		}
		options.put("paramStart", Integer.parseInt(paramStart));
		options.put("paramLength", Integer.parseInt(paramLength));

		try {

			resultVO = clientPackageService.readTotalPackageList(options);
			resultVO.setDraw(paramDraw);

		} catch (Exception ex) {
			logger.error("error in readTotalPackageList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create job for all upgrade package for client
	 * 
	 * @param clientId string target client id
	 * @param groupId  string target group id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createTotalPackageUpgradeForClient")
	public @ResponseBody ResultVO createTotalPackageUpgradeForClient(
			@RequestParam(value = "clientIds", required = false) String clientIds) {
	
		ResultVO resultVO = new ResultVO();
	
		try {
	
			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJob("package", "upgrade_all");
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");
	
			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();
	
			} catch (Exception jsonex) {
				logger.error("ClientPackageController.createTotalPackageUpgradeForClient (make json) Exception occurred. ",
						jsonex);
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
			jobVO.setJobName("upgrade_all");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());
	
			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (clientIds != null && clientIds.length() > 0) {
				String[] clientIdArray = clientIds.split(",");
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
			logger.error("error in createTotalPackageUpgradeForClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
	
		return resultVO;
	}

	/**
	 * create job for all upgrade package for group
	 * 
	 * @param clientId string target client id
	 * @param groupId  string target group id
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/createTotalPackageUpgradeForGroup")
	public @ResponseBody ResultVO createTotalPackageUpgradeForGroup(
			@RequestParam(value = "groupIds", required = false) String groupIds) {
	
		ResultVO resultVO = new ResultVO();
	
		try {
	
			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJob("package", "upgrade_all");
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");
	
			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();
	
			} catch (Exception jsonex) {
				logger.error("ClientPackageController.createTotalPackageUpgradeForGroup (make json) Exception occurred. ",
						jsonex);
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
			jobVO.setJobName("upgrade_all");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());
	
			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (groupIds != null && groupIds.length() > 0) {
				String[] groupIdArray = groupIds.split(",");
				for (int m = 0; m < groupIdArray.length; m++) {
					ResultVO clientResultVO = clientService.getClientListInGroup(groupIdArray[m]);
					if(GPMSConstants.MSG_SUCCESS.equals(clientResultVO.getStatus().getResult())) {
						ClientVO[] clients = (ClientVO[]) clientResultVO.getData();
						for (int i = 0; i < clients.length; i++) {
							clientList.add(clients[i].getClientId());
						}
					}
				}
			}
			
			if(clientList.size() > 0) {
				String[] clientArray = new String[clientList.size()];
				clientArray = clientList.toArray(clientArray);
		
				jobVO.setClientIds(clientArray);
		
				resultVO.setStatus(jobService.createJob(jobVO));
			} else {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage("job.result.noinsert") + " (no client)"));
			}
		} catch (Exception ex) {
			logger.error("error in createTotalPackageUpgradeForGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
	
		return resultVO;
	}

	/**
	 * create job for update packages
	 * 
	 * @param groupId    string target group id
	 * @param clientId   string target client id
	 * @param packageIds string array package id array.
	 * @return ResultVO result data bean
	 *
	 */
	@PostMapping(value = "/updatePackageInClient")
	public @ResponseBody ResultVO updatePackageInClient(
			@RequestParam(value = "clientIds", required = false) String clientIds,
			@RequestParam(value = "packageIds", required = true) String packageIds) {
	
		ResultVO resultVO = new ResultVO();
	
		try {
			// make job
			Job[] jobs = new Job[2];
			jobs[0] = Job.generateJobWithPackage("package", "install_or_upgrade_package", packageIds);
			jobs[1] = Job.generateJob("package", "update_package_version_to_server");
	
			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();
	
			} catch (Exception jsonex) {
				logger.error("ClientPackageController.updatePackageInClient (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName("install_or_upgrade_package");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());
	
			// setup target data
			ArrayList<String> clientList = new ArrayList<String>();
			if (clientIds != null && clientIds.length() > 0) {
				String[] clientIdArray = clientIds.split(",");
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
			logger.error("error in updatePackageInClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
	
		return resultVO;
	}
}
