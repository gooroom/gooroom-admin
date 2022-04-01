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

package kr.gooroom.gpms.job.custom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlMstService;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import kr.gooroom.gpms.job.nodes.Job;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;

/**
 * Gooroom Job management class.
 * <p>
 * job create and target select process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Component
public class CustomJobMaker {

	private static final Logger logger = LoggerFactory.getLogger(CustomJobMaker.class);

	@Resource(name = "clientService")
	private ClientService clientService;

	@Resource(name = "jobService")
	private JobService jobService;

	@Resource(name = "ctrlMstService")
	private CtrlMstService ctrlMstService;

	/**
	 * create job when test grm stress test
	 * <p>
	 * only use test.
	 * <p>
	 * job name is 'raise_traffic'
	 * 
	 * @param stressCount string stress client amount.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForClientStressTest(String stressCount) throws Exception {

		// select target, only online clients.
		ResultVO re = clientService.getClientListInOnline();

		if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {

			ClientVO[] row = (ClientVO[]) re.getData();

			if (row.length > 0) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("traffic_size", stressCount);

				try {

					//
					Job[] jobs = new Job[1];
					jobs[0] = Job.generateJobWithMap("SERVER", "raise_traffic", map);

					String jsonStr = "";
					StringWriter outputWriter = new StringWriter();
					try {
						ObjectMapper mapper = new ObjectMapper();
						mapper.setSerializationInclusion(Include.NON_NULL);
						mapper.writeValue(outputWriter, jobs);
						jsonStr = outputWriter.toString();

					} catch (Exception jsonex) {
						logger.error("CustomJobMaker.createJobForClientStressTest (make json) Exception occurred. ",
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
					jobVO.setJobName("raise_traffic");
					jobVO.setRegUserId(LoginInfoHelper.getUserId());

					//
					String[] clientArray = new String[row.length];
					for (int i = 0; i < row.length; i++) {
						clientArray[i] = row[i].getClientId();
					}
					jobVO.setClientIds(clientArray);

					jobService.createJob(jobVO);

				} catch (Exception ex) {
					logger.error("error in createJobForClientStressTest : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				}
			}
		}
	}

	/**
	 * create job when user role changed.
	 * <p>
	 * target client is used by target user and online.
	 * 
	 * @param objId   string configuration id.
	 * @param jobName string job name.
	 * @param userIds string user id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForUserConf(String confId, String jobName, String confTp) throws Exception {

		ResultVO re = clientService.getOnlineClientIdsInClientUseConfId(confId, confTp);

		if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
			ClientVO[] row = (ClientVO[]) re.getData();
			if (row.length > 0) {
				try {
					// create job
					Job[] jobs = new Job[1];
					if (GPMSConstants.TYPE_FILTEREDSOFTWARE.equals(confTp) || GPMSConstants.TYPE_CTRLCENTERITEMRULE.equals(confTp)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("from_gpms", "yes");
						jobs[0] = Job.generateJobWithMap("config", jobName, map);
					} else {
						jobs[0] = Job.generateJob("config", jobName);
					}

					String jsonStr = "";
					StringWriter outputWriter = new StringWriter();
					try {
						ObjectMapper mapper = new ObjectMapper();
						mapper.setSerializationInclusion(Include.NON_NULL);
						mapper.writeValue(outputWriter, jobs);
						jsonStr = outputWriter.toString();

					} catch (Exception jsonex) {
						logger.error("CustomJobMaker.createJobForUserConf (make json) Exception occurred. ", jsonex);
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
					jobVO.setJobName(jobName);
					jobVO.setRegUserId(LoginInfoHelper.getUserId());

					// assign target clients
					String[] clientArray = new String[row.length];
					for (int i = 0; i < row.length; i++) {
						clientArray[i] = row[i].getClientId();
					}
					jobVO.setClientIds(clientArray);

					jobService.createJob(jobVO);
				} catch (Exception ex) {
					logger.error("error in createJobForUserConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				}
			}
		}
	}

	/**
	 * create job when client configuration changed.
	 * <p>
	 * target client is assigned client group and online.
	 * 
	 * @param objId     string configuration id.
	 * @param jobName   string job name.
	 * @param map       configuration item hierarchy data.
	 * @param clientIds string client id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForClientConf(String confId, String confTp, String jobName, HashMap<String, String> map)
			throws Exception {

		String[] clientIds = null;
		// select target clients, online client.
		ResultVO re = clientService.getOnlineClientIdsInClientConf(confId, confTp);
		if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
			ClientVO[] row = (ClientVO[]) re.getData();
			// create target client array.
			clientIds = new String[row.length];
			for (int i = 0; i < row.length; i++) {
				clientIds[i] = row[i].getClientId();
			}
		}

		if (clientIds != null && clientIds.length > 0) {
			try {

				// create job
				Job[] jobs = new Job[1];
				if (map == null) {
					map = new HashMap<String, String>();
				}
				jobs[0] = Job.generateJobWithMap("config", jobName, map);

				String jsonStr = "";
				StringWriter outputWriter = new StringWriter();
				try {
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);
					mapper.writeValue(outputWriter, jobs);
					jsonStr = outputWriter.toString();

				} catch (Exception jsonex) {
					logger.error("CustomJobMaker.createJobForClientConf (make json) Exception occurred. ", jsonex);
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
				jobVO.setJobName(jobName);
				jobVO.setRegUserId(LoginInfoHelper.getUserId());

				// assign target clients
				jobVO.setClientIds(clientIds);

				jobService.createJob(jobVO);

			} catch (Exception ex) {
				logger.error("error in createJobForClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			}
		}
	}

	/**
	 * create job when client configuration changed.
	 * <p>
	 * target client is all and online.
	 * 
	 * @param jobName   string job name.
	 * @param map       configuration item hierarchy data.
	 * @param clientIds string client id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForAllClient(String jobName, HashMap<String, String> map) throws Exception {

		String[] clientIds = null;
		// select target clients, online client.
		ResultVO re = clientService.getClientInOnline(GPMSConstants.GUBUN_ALL);
		if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
			ClientVO[] row = (ClientVO[]) re.getData();
			// create target client array.
			clientIds = new String[row.length];
			for (int i = 0; i < row.length; i++) {
				clientIds[i] = row[i].getClientId();
			}
		}

		if (clientIds != null && clientIds.length > 0) {
			try {
				// create job
				Job[] jobs = new Job[1];
				if (map == null) {
					map = new HashMap<String, String>();
				}
				jobs[0] = Job.generateJobWithMap("config", jobName, map);

				String jsonStr = "";
				StringWriter outputWriter = new StringWriter();
				try {
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);
					mapper.writeValue(outputWriter, jobs);
					jsonStr = outputWriter.toString();

				} catch (Exception jsonex) {
					logger.error("CustomJobMaker.createJobForAllClient (make json) Exception occurred. ", jsonex);
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
				jobVO.setJobName(jobName);
				jobVO.setRegUserId(LoginInfoHelper.getUserId());

				// assign target clients
				jobVO.setClientIds(clientIds);

				jobService.createJob(jobVO);

			} catch (Exception ex) {
				logger.error("error in createJobForAllClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			}
		}

	}

	/**
	 * create job when client configuration changed.
	 * <p>
	 * target client is assigned client group and online.
	 * 
	 * @param jobName   string job name.
	 * @param map       configuration item hierarchy data.
	 * @param clientIds string client id array
	 * @return void
	 * @throws Exception
	 */
	public void createJobForClientSetupWithClients(String jobName, HashMap<String, String> map, String[] clientIds)
			throws Exception {

		if (clientIds != null && clientIds.length > 0) {
			try {
				// create job
				Job[] jobs = new Job[1];
				if (map == null) {
					map = new HashMap<String, String>();
				}
				jobs[0] = Job.generateJobWithMap("config", jobName, map);

				String jsonStr = "";
				StringWriter outputWriter = new StringWriter();
				try {
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);
					mapper.writeValue(outputWriter, jobs);
					jsonStr = outputWriter.toString();

				} catch (Exception jsonex) {
					logger.error("CustomJobMaker.createJobForClientSetupWithClients (make json) Exception occurred. ",
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
				jobVO.setJobName(jobName);
				jobVO.setRegUserId(LoginInfoHelper.getUserId());

				// assign target clients
				jobVO.setClientIds(clientIds);

				jobService.createJob(jobVO);
			} catch (Exception ex) {
				logger.error("error in createJobForClientSetupWithClients : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			}
		}
	}

	/**
	 * create job for client profiling.
	 * <p>
	 * target client is used by profiling.
	 * 
	 * @param clientId string target client
	 * @return StatusVO
	 * @throws Exception
	 */
	public StatusVO createJobForProfiling(String clientId, String profileNo) throws Exception {

		StatusVO statusVO = null;

		try {
			// create job
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("profile_no", profileNo);

			Job[] jobs = new Job[1];
			jobs[0] = Job.generateJobWithMap("package", "profiling_packages", map);

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("CustomJobMaker.createJobForProfiling (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName("profiling_packages");
			jobVO.setRegUserId(LoginInfoHelper.getUserId());

			// assign target clients
			String[] clientArray = new String[1];
			clientArray[0] = clientId;
			jobVO.setClientIds(clientArray);

			statusVO = jobService.createJob(jobVO);

		} catch (Exception ex) {
			logger.error("error in createJobForProfiling : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return statusVO;
	}

	/**
	 * create job with clients list and job name
	 * 
	 * @param jobName   string job name.
	 * @param clientIds string user id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobWithClientIds(String jobName, String[] clientArray) throws Exception {

		try {
			// create job
			Job[] jobs = new Job[1];
			jobs[0] = Job.generateJob("config", jobName);

			String jsonStr = "";
			StringWriter outputWriter = new StringWriter();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, jobs);
				jsonStr = outputWriter.toString();

			} catch (Exception jsonex) {
				logger.error("CustomJobMaker.createJobWithClientIds (make json) Exception occurred. ", jsonex);
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
			jobVO.setJobName(jobName);
			jobVO.setRegUserId(LoginInfoHelper.getUserId());
			jobVO.setClientIds(clientArray);

			jobService.createJob(jobVO);

		} catch (Exception ex) {
			logger.error("error in createJobWithClientIds - customJobMaker : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
	}

	/**
	 * create job for client use rules when change dept.
	 * <p>
	 * used when, move user to dept, dept info changed.
	 * 
	 * @param userIds string user id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForUseRuleByChangeDept(String[] userIds) throws Exception {

		try {
			ResultVO re = clientService.getOnlineClientIdsInUserIds(userIds);
			if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
				OnlineClientAndUserVO[] row = (OnlineClientAndUserVO[]) re.getData();
				ArrayList<String> mediaJobTarget = new ArrayList<String>();
				ArrayList<String> browserJobTarget = new ArrayList<String>();
				ArrayList<String> securityJobTarget = new ArrayList<String>();
				ArrayList<String> softwareFilterJobTarget = new ArrayList<String>();
				ArrayList<String> ctrlCenterItemJobTarget = new ArrayList<String>();
				ArrayList<String> policyKitJobTarget = new ArrayList<String>();
				
				if (row != null && row.length > 0) {
					for (OnlineClientAndUserVO vo : row) {
						if (StringUtils.isBlank(vo.getClientMediaRuleId())
								&& StringUtils.isBlank(vo.getUserMediaRuleId())) {
							mediaJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientBrowserRuleId())
								&& StringUtils.isBlank(vo.getUserBrowserRuleId())) {
							browserJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientSecurityRuleId())
								&& StringUtils.isBlank(vo.getUserSecurityRuleId())) {
							securityJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientFilteredSoftwareRuleId())
								&& StringUtils.isBlank(vo.getUserFilteredSoftwareRuleId())) {
							softwareFilterJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientCtrlCenterItemRuleId())
								&& StringUtils.isBlank(vo.getUserCtrlCenterItemRuleId())) {
							ctrlCenterItemJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientPolicyKitRuleId())
								&& StringUtils.isBlank(vo.getUserPolicyKitRuleId())) {
							policyKitJobTarget.add(vo.getClientId());
						}
					}

					// Create Job
					if (mediaJobTarget.size() > 0) {
						createMediaJobByClientIds(mediaJobTarget.toArray(new String[mediaJobTarget.size()]));
					}
					if (browserJobTarget.size() > 0) {
						createBrowserJobByClientIds(browserJobTarget.toArray(new String[browserJobTarget.size()]));
					}
					if (securityJobTarget.size() > 0) {
						createSecurityJobByClientIds(securityJobTarget.toArray(new String[securityJobTarget.size()]));
					}
					if (softwareFilterJobTarget.size() > 0) {
						createSoftwareFilterJobByClientIds(
								softwareFilterJobTarget.toArray(new String[softwareFilterJobTarget.size()]));
					}
					if (ctrlCenterItemJobTarget.size() > 0) {
						createCtrlCenterItemJobByClientIds(
								ctrlCenterItemJobTarget.toArray(new String[ctrlCenterItemJobTarget.size()]));
					}
					if (policyKitJobTarget.size() > 0) {
						createPolicyKitJobByClientIds(
								policyKitJobTarget.toArray(new String[policyKitJobTarget.size()]));
					}
				}
			}

		} catch (Exception ex) {
			logger.error("error in createJobForUseRuleByChangeDept - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	/**
	 * create job for client use rules when change user
	 * <p>
	 * used when, move user to dept, dept info changed.
	 * 
	 * @param userIds string user id array that will null if none.
	 * @return void
	 * @throws Exception
	 */
	public void createJobForUseRuleByChangeUser(String userId) throws Exception {

		try {
			ResultVO re = clientService.getOnlineClientIdsInUserIds(new String[] { userId });
			if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
				OnlineClientAndUserVO[] row = (OnlineClientAndUserVO[]) re.getData();
				ArrayList<String> mediaJobTarget = new ArrayList<String>();
				ArrayList<String> browserJobTarget = new ArrayList<String>();
				ArrayList<String> securityJobTarget = new ArrayList<String>();
				ArrayList<String> softwareFilterJobTarget = new ArrayList<String>();
				ArrayList<String> ctrlCenterItemJobTarget = new ArrayList<String>();
				ArrayList<String> policyKitJobTarget = new ArrayList<String>();

				if (row != null && row.length > 0) {
					for (OnlineClientAndUserVO vo : row) {
						if (StringUtils.isBlank(vo.getClientMediaRuleId())) {
							mediaJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientBrowserRuleId())) {
							browserJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientSecurityRuleId())) {
							securityJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientFilteredSoftwareRuleId())) {
							softwareFilterJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientCtrlCenterItemRuleId())) {
							ctrlCenterItemJobTarget.add(vo.getClientId());
						}
						if (StringUtils.isBlank(vo.getClientPolicyKitRuleId())) {
							policyKitJobTarget.add(vo.getClientId());
						}
					}

					// Create Job
					if (mediaJobTarget.size() > 0) {
						createMediaJobByClientIds(mediaJobTarget.toArray(new String[mediaJobTarget.size()]));
					}
					if (browserJobTarget.size() > 0) {
						createBrowserJobByClientIds(browserJobTarget.toArray(new String[browserJobTarget.size()]));
					}
					if (securityJobTarget.size() > 0) {
						createSecurityJobByClientIds(securityJobTarget.toArray(new String[securityJobTarget.size()]));
					}
					if (softwareFilterJobTarget.size() > 0) {
						createSoftwareFilterJobByClientIds(
								softwareFilterJobTarget.toArray(new String[softwareFilterJobTarget.size()]));
					}
					if (ctrlCenterItemJobTarget.size() > 0) {
						createCtrlCenterItemJobByClientIds(
								ctrlCenterItemJobTarget.toArray(new String[ctrlCenterItemJobTarget.size()]));
					}
					if (policyKitJobTarget.size() > 0) {
						createPolicyKitJobByClientIds(
								policyKitJobTarget.toArray(new String[policyKitJobTarget.size()]));
					}
				}
			}
		} catch (Exception ex) {
			logger.error("error in createJobForUseRuleByChangeDept - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createMediaJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_MEDIA_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createMediaJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createBrowserJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_BROWSER_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createBrowserJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createSecurityJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE, clientIds);
			createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_PASSWORDTIME_CHANGE, clientIds);
			createJobWithClientIds(GPMSConstants.JOB_CLIENTSECU_SCREENTIME_CHANGE, clientIds);
			createJobWithClientIds(GPMSConstants.JOB_MEDIA_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createSecurityJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createSoftwareFilterJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_FILTEREDSOFTWARE_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createSoftwareFilterJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createCtrlCenterItemJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_CTRLCENTERITEMS_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createCtrlCenterItemJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	private void createPolicyKitJobByClientIds(String[] clientIds) {
		try {
			createJobWithClientIds(GPMSConstants.JOB_POLICYKIT_RULE_CHANGE, clientIds);
		} catch (Exception ex) {
			logger.error("error in createPolicyKitJobByClientIds - customJobMaker : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
	}

	public void createJobForNoticeInstantNotice(String noticePublishId) throws Exception {
		String moduleName = "noti";
		String taskName = "set_noti";

		ResultVO re = clientService.getClientListForNoticeInstantNotice(noticePublishId);
		if (GPMSConstants.MSG_SUCCESS.equals(re.getStatus().getResult())) {
			ClientVO[] row = (ClientVO[]) re.getData();
			if (row.length > 0) {
				try {
					// create Job
					Job[] jobs = new Job[1];
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("notice_publish_id", noticePublishId);
					jobs[0] = Job.generateJobWithMap(moduleName, taskName, map);

					String jsonStr = "";
					StringWriter outputWriter = new StringWriter();
					try {
						ObjectMapper mapper = new ObjectMapper();
						mapper.setSerializationInclusion(Include.NON_NULL);
						mapper.writeValue(outputWriter, jobs);
						jsonStr = outputWriter.toString();
					} catch (Exception jsonex) {
						logger.error("CustomJobMaker.createJobForNoticeInstantNotice (make json) Exception occurred. ",
								jsonex);
					}

					JobVO jobVO = new JobVO();
					jobVO.setJobData(jsonStr);
					jobVO.setJobName(taskName);
					jobVO.setRegUserId(LoginInfoHelper.getUserId());

					// assign target clients
					String[] clientArray = new String[row.length];
					for (int i = 0; i < row.length; i++) {
						clientArray[i] = row[i].getClientId();
					}
					jobVO.setClientIds(clientArray);

					jobService.createJob(jobVO);
				} catch (Exception ex) {
					logger.error("error in createJobForNoticeInstantNotice : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
				}
			}
		}
	}

	/**
	 * create client config(rule) change job.
	 * 
	 * @param clientIds     string array client id
	 * @param newGroupVO    ClientGroupVO group information current
	 * @param beforeGroupVO ClientGroupVO group information before
	 * @return void
	 * @throws Exception
	 */
	public void createJobForGroup(String[] clientIds, ClientGroupVO newGroupVO, ClientGroupVO beforeGroupVO)
			throws Exception {
	
		if (clientIds != null && clientIds.length > 0) {
	
			// 단말설정
			if (beforeGroupVO == null || beforeGroupVO.getClientConfigId() == null
					|| !(beforeGroupVO.getClientConfigId().equals(newGroupVO.getClientConfigId()))) {
	
				ResultVO re = ctrlMstService.readCtrlItem(newGroupVO.getClientConfigId());
				if (re != null && re.getData().length > 0) {
	
					ArrayList<CtrlPropVO> props = ((CtrlItemVO) re.getData()[0]).getPropList();
					String homeReset = "";
					String rootAllow = "";
					String sudoAllow = "";
					String cleanModeAllow = "";
					if (props != null && props.size() > 0) {
						for (CtrlPropVO prop : props) {
							if (GPMSConstants.CTRL_ITEM_USEHOMERESET.equalsIgnoreCase(prop.getPropNm())) {
								homeReset = prop.getPropValue();
							}
							else if (GPMSConstants.CTRL_ITEM_ROOTALLOW.equalsIgnoreCase(prop.getPropNm())) {
								rootAllow = prop.getPropValue();
							}
							else if (GPMSConstants.CTRL_ITEM_SUDOALLOW.equalsIgnoreCase(prop.getPropNm())) {
								sudoAllow = prop.getPropValue();
							}
							else if (GPMSConstants.CTRL_ITEM_CLEANMODEALLOW.equalsIgnoreCase(prop.getPropNm())) {
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
					createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOMERESET_CHANGE, map,
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
					createJobForClientSetupWithClients(GPMSConstants.JOB_ACCOUNT_RULE_CHANGE, mapAccountAllow,
							clientIds);

					// clean mode allow job
					HashMap<String, String> mapCleanModeAllow = new HashMap<>();
					if("true".equalsIgnoreCase(cleanModeAllow)) {
						mapCleanModeAllow.put("cleanmode_use", "enable");
					} else {
						mapCleanModeAllow.put("cleanmode_use", "disable");
					}
					createJobForClientSetupWithClients(GPMSConstants.JOB_CLEANMODE_RULE_CHANGE, mapCleanModeAllow,
							clientIds);
	
					// use log config change job
					createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_LOGCONFIG_CHANGE, null,
							clientIds);
				}
			}
	
			// Host change job
			if (beforeGroupVO == null || beforeGroupVO.getHostNameConfigId() == null
					|| !(beforeGroupVO.getHostNameConfigId().equals(newGroupVO.getHostNameConfigId()))) {
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOSTS_CHANGE, null, clientIds);
			}
	
			// software filtered job
			if (beforeGroupVO == null || beforeGroupVO.getFilteredSoftwareRuleId() == null
					|| !(beforeGroupVO.getFilteredSoftwareRuleId().equals(newGroupVO.getFilteredSoftwareRuleId()))) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("from_gpms", "yes");
				createJobForClientSetupWithClients(GPMSConstants.JOB_FILTEREDSOFTWARE_RULE_CHANGE, map,
						clientIds);
			}

			// control center item job
			if (beforeGroupVO == null || beforeGroupVO.getCtrlCenterItemRuleId() == null
					|| !(beforeGroupVO.getCtrlCenterItemRuleId().equals(newGroupVO.getCtrlCenterItemRuleId()))) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("from_gpms", "yes");
				createJobForClientSetupWithClients(GPMSConstants.JOB_CTRLCENTERITEMS_RULE_CHANGE, map,
						clientIds);
			}

			// policy kit job
			if (beforeGroupVO == null || beforeGroupVO.getPolicyKitRuleId() == null
					|| !(beforeGroupVO.getPolicyKitRuleId().equals(newGroupVO.getPolicyKitRuleId()))) {
				createJobForClientSetupWithClients(GPMSConstants.JOB_POLICYKIT_RULE_CHANGE, null,
						clientIds);
			}

			// browser job
			if (beforeGroupVO == null || beforeGroupVO.getBrowserRuleId() == null
					|| !(beforeGroupVO.getBrowserRuleId().equals(newGroupVO.getBrowserRuleId()))) {
				createJobForClientSetupWithClients(GPMSConstants.JOB_BROWSER_RULE_CHANGE, null, clientIds);
			}
	
			// media job
			if (beforeGroupVO == null || beforeGroupVO.getMediaRuleId() == null
					|| !(beforeGroupVO.getMediaRuleId().equals(newGroupVO.getMediaRuleId()))) {
				createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
			}
	
			// security job
			if (beforeGroupVO == null || beforeGroupVO.getSecurityRuleId() == null
					|| !(beforeGroupVO.getSecurityRuleId().equals(newGroupVO.getSecurityRuleId()))) {
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE, null,
						clientIds);
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_PASSWORDTIME_CHANGE, null,
						clientIds);
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_SCREENTIME_CHANGE, null,
						clientIds);
				createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
			}
		}
	}
	
	/**
	 * create client config(rule) change job - absolute.
	 * 
	 * @param clientIds     string array client id
	 * @param clientConfigId    clientconfig rule id
	 * @return void
	 * @throws Exception
	 */
	public void createAbsoluteJobForClients(String[] clientIds, String clientConfigId) throws Exception {

		if (clientIds != null && clientIds.length > 0) {

			// 단말설정
			ResultVO re = ctrlMstService.readCtrlItem(clientConfigId);
			if (re != null && re.getData().length > 0) {

				ArrayList<CtrlPropVO> props = ((CtrlItemVO) re.getData()[0]).getPropList();
				String homeReset = "";
				String rootAllow = "";
				String sudoAllow = "";
				String cleanModeAllow = "";
				if (props != null && props.size() > 0) {
					for (CtrlPropVO prop : props) {
						if (GPMSConstants.CTRL_ITEM_USEHOMERESET.equalsIgnoreCase(prop.getPropNm())) {
							homeReset = prop.getPropValue();
						}
						else if (GPMSConstants.CTRL_ITEM_USEHOMERESET.equalsIgnoreCase(prop.getPropNm())) {
							rootAllow = prop.getPropValue();
						}
						else if (GPMSConstants.CTRL_ITEM_USEHOMERESET.equalsIgnoreCase(prop.getPropNm())) {
							sudoAllow = prop.getPropValue();
						}
						else if (GPMSConstants.CTRL_ITEM_CLEANMODEALLOW.equalsIgnoreCase(prop.getPropNm())) {
							cleanModeAllow = prop.getPropValue();
						}
					}
				}

				// home reset job
				HashMap<String, String> mapHomeReset = new HashMap<String, String>();
				if ("true".equalsIgnoreCase(homeReset)) {
					mapHomeReset.put("operation", "enable");
				} else {
					mapHomeReset.put("operation", "disable");
				}
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOMERESET_CHANGE, mapHomeReset, clientIds);

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
				createJobForClientSetupWithClients(GPMSConstants.JOB_ACCOUNT_RULE_CHANGE, mapAccountAllow, clientIds);

				// clean mode allow job
				HashMap<String, String> mapCleanModeAllow = new HashMap<>();
				if("true".equalsIgnoreCase(cleanModeAllow)) {
					mapCleanModeAllow.put("cleanmode_use", "enable");
				} else {
					mapCleanModeAllow.put("cleanmode_use", "disable");
				}
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLEANMODE_RULE_CHANGE, mapCleanModeAllow, clientIds);

				// use log config change job
				createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_LOGCONFIG_CHANGE, null, clientIds);
			}

			// Host change job
			createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTCONF_HOSTS_CHANGE, null, clientIds);

			// software filtered job
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("from_gpms", "yes");
			createJobForClientSetupWithClients(GPMSConstants.JOB_FILTEREDSOFTWARE_RULE_CHANGE, map, clientIds);

			// control center item job
			map = new HashMap<String, String>();
			map.put("from_gpms", "yes");
			createJobForClientSetupWithClients(GPMSConstants.JOB_CTRLCENTERITEMS_RULE_CHANGE, map, clientIds);

			// policy kit job
			createJobForClientSetupWithClients(GPMSConstants.JOB_POLICYKIT_RULE_CHANGE, null, clientIds);

			// browser job
			createJobForClientSetupWithClients(GPMSConstants.JOB_BROWSER_RULE_CHANGE, null, clientIds);

			// media job
			createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);

			// security job
			createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_PACKAGEHANDLE_CHANGE, null, clientIds);
			createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_PASSWORDTIME_CHANGE, null, clientIds);
			createJobForClientSetupWithClients(GPMSConstants.JOB_CLIENTSECU_SCREENTIME_CHANGE, null, clientIds);
			createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
		}
	}

	public void createJobForUserReq(String ClientId, HashMap<String, String> map) throws Exception {

		Job[] jobs = new Job[1];
		jobs[0] = Job.generateJobWithMap("config", "server_event_usb_whitelist", map);

		String jsonStr = "";
		StringWriter outputWriter = new StringWriter();

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.writeValue(outputWriter, jobs);
			jsonStr = outputWriter.toString();

		} catch (Exception jsonex) {
			logger.error("CustomJobMaker.createJobForUserReq (make json) Exception occurred. ", jsonex);
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
		jobVO.setJobName("client_event_usb_whitelist");
		jobVO.setRegUserId(LoginInfoHelper.getUserId());

		// assign target clients
		String[] clientArray = new String[1];
		clientArray[0] = ClientId;
		jobVO.setClientIds(clientArray);

		jobService.createJob(jobVO);
	}
}