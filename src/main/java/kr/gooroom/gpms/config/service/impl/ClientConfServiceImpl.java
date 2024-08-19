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

package kr.gooroom.gpms.config.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.ClientConfService;
import kr.gooroom.gpms.config.service.MgServerConfVO;
import kr.gooroom.gpms.config.service.SiteConfVO;
import kr.gooroom.gpms.job.custom.CustomJobMaker;

/**
 * Client configuration management service implements class.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientConfService")
public class ClientConfServiceImpl implements ClientConfService {

	private static final Logger logger = LoggerFactory.getLogger(ClientConfServiceImpl.class);

	@Resource(name = "clientConfDAO")
	private ClientConfDAO clientConfDao;

	@Inject
	private CustomJobMaker jobMaker;

	/**
	 * get gooroom managements server information(address) history data.
	 * 
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getMgServerConfList() {

		ResultVO resultVO = new ResultVO();

		try {

			List<MgServerConfVO> re = clientConfDao.getMgServerConfList();

			if (re != null && re.size() > 0) {

				MgServerConfVO[] row = re.toArray(MgServerConfVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getMgServerConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new gooroom managements server information(address) data.
	 * 
	 * @param vo MgServerConfVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createMgServerConf(MgServerConfVO vo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			vo.setModUserId(LoginInfoHelper.getUserId());

			// add history data
			long cnt = clientConfDao.createMgServerConfHist(vo);
			if (cnt > 0) {
				if (clientConfDao.isExistMgServerConf()) {
					// update
					long upcnt = clientConfDao.editMgServerConf(vo);
					if (upcnt > 0) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
								MessageSourceHelper.getMessage("serverconf.result.update"));
					} else {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
								MessageSourceHelper.getMessage("serverconf.result.noupdate"));
					}
				} else {
					// insert
					long inCnt = clientConfDao.createMgServerConf(vo);
					if (inCnt > 0) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
								MessageSourceHelper.getMessage("serverconf.result.insert"));
					} else {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
								MessageSourceHelper.getMessage("serverconf.result.noinsert"));
					}
				}
			}

			long oldPollingTime = clientConfDao.selectSitePollingTime("");
			long oldTrialCount = clientConfDao.selectSiteLoginTrialCount("");
			long oldMaxMediaCnt = clientConfDao.selectSiteMaxMediaCnt("");

			// update polling time and trial count and passwordRule
			SiteConfVO confVo = new SiteConfVO();
			confVo.setPollingCycle(vo.getPollingTime());
			confVo.setTrialCount(vo.getTrialCount());
			confVo.setLockTime(vo.getLockTime());
			confVo.setPasswordRule(vo.getPasswordRule());
			confVo.setEnableDuplicateLogin(vo.getEnableDuplicateLogin());
			confVo.setMaxMediaCnt(vo.getMaxMediaCnt());
			confVo.setRegisterReq(vo.getRegisterReq());
			confVo.setDeleteReq(vo.getDeleteReq());
			
			long updateCnt = clientConfDao.updateSiteConf(confVo);
			if (updateCnt > 0) {
				long insertCnt = clientConfDao.createSiteConfHist(confVo);
				if (insertCnt > 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							MessageSourceHelper.getMessage("serverconf.result.insert"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
							MessageSourceHelper.getMessage("serverconf.result.noinsert"));
				}

				if (Long.parseLong(vo.getPollingTime()) != oldPollingTime) {
					// create client job for change polling time
					HashMap<String, String> map = new HashMap<>();
					map.put("dispatch_time", vo.getPollingTime());
					jobMaker.createJobForAllClient(GPMSConstants.JOB_CLIENTCONF_AGENTPOLLING_CHANGE, map);
				}
				
				if (Long.parseLong(vo.getTrialCount()) != oldTrialCount) {
					// update user login trial count
					HashMap<String, Object> map = new HashMap<>();
					map.put("trialCount", vo.getTrialCount());
					clientConfDao.updateLoginTrialInUser(map);
				}

				if (Long.parseLong(vo.getMaxMediaCnt()) != oldMaxMediaCnt) {
					// create client job for change max media count
					HashMap<String, String> map = new HashMap<>();
					jobMaker.createJobForAllClient(GPMSConstants.JOB_CLIENTCONF_MAXMEDIACNT_CHANGE, map);
				}
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * get current gooroom managements server information(address) data.
	 * 
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO readCurrentMgServerConf() {

		ResultVO resultVO = new ResultVO();

		try {

			MgServerConfVO vo = clientConfDao.getMgServerConf();
			long pollingTime = clientConfDao.selectSitePollingTime("");
			long trialCount = clientConfDao.selectSiteLoginTrialCount("");
			long lockTime = clientConfDao.selectSiteLoginLockTime("");
			long enableDuplicateLogin = clientConfDao.selectSiteLoginDuplicateEnable("");
			String passwordRule = clientConfDao.selectSitePasswordRule("");
			long maxMediaCnt =clientConfDao.selectSiteMaxMediaCnt("");
			long registerReq =clientConfDao.selectSiteRegisterReq("");
			long deleteReq =clientConfDao.selectSiteDeleteReq("");

			if (vo != null) {

				vo.setPollingTime(String.valueOf(pollingTime));
				vo.setTrialCount(String.valueOf(trialCount));
				vo.setLockTime(String.valueOf(lockTime));
				vo.setPasswordRule(passwordRule);
				vo.setEnableDuplicateLogin(String.valueOf(enableDuplicateLogin));
				vo.setMaxMediaCnt(String.valueOf(maxMediaCnt));
				vo.setRegisterReq(String.valueOf(registerReq));
				vo.setDeleteReq(String.valueOf(deleteReq));

				MgServerConfVO[] row = new MgServerConfVO[1];
				row[0] = vo;
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readCurrentMgServerConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get client configuration information data by group id.
	 * 
	 * @param groupId String group id
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getClientConfIdByGroupId(String groupId) {

		ResultVO resultVO = new ResultVO();
		try {
			ClientGroupVO re = clientConfDao.selectClientConfIdByGroupId(groupId);
			if (re != null) {
				ClientGroupVO[] o = new ClientGroupVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				ClientGroupVO[] o = new ClientGroupVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getUserConfIdByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
