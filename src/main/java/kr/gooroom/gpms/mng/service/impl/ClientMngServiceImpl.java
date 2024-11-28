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

package kr.gooroom.gpms.mng.service.impl;

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

import kr.gooroom.gpms.client.service.ClientPackageVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.mng.service.ClientMngService;
import kr.gooroom.gpms.mng.service.ClientProfileSetVO;
import kr.gooroom.gpms.mng.service.ClientRegKeyVO;
import kr.gooroom.gpms.mng.service.ClientSoftwareVO;

/**
 * Client registration key service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientMngService")
public class ClientMngServiceImpl implements ClientMngService {

	private static final Logger logger = LoggerFactory.getLogger(ClientMngServiceImpl.class);

	@Resource(name = "clientMngDAO")
	private ClientMngDAO clientMngDAO;

	@Inject
	private CustomJobMaker jobMaker;

	/**
	 * response client registration key list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getRegKeyList(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientRegKeyVO> re = clientMngDAO.selectRegKeyList(options);
			long totalCount = clientMngDAO.selectRegKeyListTotalCount(options);
			long filteredCount = clientMngDAO.selectRegKeyListFilteredCount(options);

			if (re != null && re.size() > 0) {

				ClientRegKeyVO[] row = re.toArray(ClientRegKeyVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getRegKeyList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new client registration key data
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createRegKeyData(ClientRegKeyVO clientRegKeyVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			clientRegKeyVO.setModUserId(LoginInfoHelper.getUserId());
			clientRegKeyVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = clientMngDAO.insertRegKeyData(clientRegKeyVO);

			if (resultCnt > 0) {

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("regkey.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("regkey.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * edit client registration key data
	 * 
	 * @param clientRegKeyVO ClientRegKeyVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editRegKeyData(ClientRegKeyVO clientRegKeyVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			clientRegKeyVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = clientMngDAO.updateRegKeyData(clientRegKeyVO);

			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("regkey.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("regkey.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in editRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete client registration key data.
	 * 
	 * @param regKeyNo String registration key id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteRegKeyData(String regKeyNo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re = clientMngDAO.deleteRegKeyData(regKeyNo);

			if (re > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("regkey.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("regkey.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteRegKeyData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create profile set by reference client id, start job.
	 * 
	 * @param vo ClientProfileSetVO
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createProfileSetService(ClientProfileSetVO vo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			
			// add Register user
			vo.setRegUserId(LoginInfoHelper.getUserId());

			// add master
			long resultCnt = clientMngDAO.insertProfileSet(vo);
			if (resultCnt > 0 && vo.getClientId() != null && vo.getClientId().length() > 0) {
				// create job
				statusVO = jobMaker.createJobForProfiling(vo.getClientId(), vo.getProfileNo());
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("client.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createProfileSetService : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createProfileSetService : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response Profile Set list data with paging
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getProfileSetListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientProfileSetVO> re = clientMngDAO.selectProfileSetListPaged(options);
			long totalCount = clientMngDAO.selectProfileSetListTotalCount(options);
			long filteredCount = clientMngDAO.selectProfileSetListFilteredCount(options);

			if (re != null && re.size() > 0) {

				ClientProfileSetVO[] row = re.toArray(ClientProfileSetVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getProfileSetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response package list data in profile set.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getProfilePackageListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientPackageVO> re = clientMngDAO.selectProfilePackageListPaged(options);
			long totalCount = clientMngDAO.selectProfilePackageListTotalCount(options);
			long filteredCount = clientMngDAO.selectProfilePackageListFilteredCount(options);

			if (re != null && re.size() > 0) {

				ClientPackageVO[] row = re.toArray(ClientPackageVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getProfilePackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete profile set data
	 * 
	 * @param profileNo String profile set no
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteProfileSetData(String profileNo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re = clientMngDAO.deleteProfileSetMaster(profileNo);

			if (re > 0) {

				clientMngDAO.deleteProfileSetData(profileNo);
				// 데이타 정보는 없을수도 있으므로 결과에 검증은 하지 않는다.

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("profileset.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("profileset.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * edit profile set data
	 * 
	 * @param clientProfileSetVO ClientProfileSetVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editProfileSetData(ClientProfileSetVO clientProfileSetVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			clientProfileSetVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = clientMngDAO.updateProfileSetData(clientProfileSetVO);

			if (reCnt > 0) {

				if (clientProfileSetVO.getClientId() != null && clientProfileSetVO.getClientId().length() > 0) {
					// create job
					jobMaker.createJobForProfiling(clientProfileSetVO.getClientId(), clientProfileSetVO.getProfileNo());
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("profileset.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("profileset.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in editProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editProfileSetData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create new client software data
	 * 
	 * @param clientSoftwareVO ClientSoftwareVO
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createClientSoftware(ClientSoftwareVO clientSoftwareVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			clientSoftwareVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = clientMngDAO.insertClientSoftwareData(clientSoftwareVO);

			if (resultCnt > 0) {

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("software.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("software.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete client software data
	 * 
	 * @param swId String software id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteClientSoftware(String swId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re = clientMngDAO.deleteClientSoftwareData(swId);

			if (re > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("software.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("software.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * update client software data
	 * 
	 * @param clientSoftwareVO ClientSoftwareVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editClientSoftware(ClientSoftwareVO clientSoftwareVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			clientSoftwareVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = clientMngDAO.updateClientSoftwareData(clientSoftwareVO);

			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("software.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("software.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in editClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editClientSoftware : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response client software data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO result object
	 */
	@Override
	public ResultPagingVO getClientSoftwareList(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientSoftwareVO> re = clientMngDAO.selectClientSoftwareList(options);
			long totalCount = clientMngDAO.selectClientSoftwareListTotalCount(options);
			long filteredCount = clientMngDAO.selectClientSoftwareListFilteredCount(options);

			if (re != null && re.size() > 0) {

				ClientSoftwareVO[] row = re.toArray(ClientSoftwareVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getClientSoftwareList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
