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

package kr.gooroom.gpms.client.service.impl;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientSummaryVO;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.client.service.UpdatePackageClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.OnlineClientAndUserVO;
import kr.gooroom.gpms.user.service.AdminUserVO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client management service implement class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientService")
public class ClientServiceImpl implements ClientService {

	private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

	@Resource(name = "clientDAO")
	private ClientDAO clientDao;

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	/**
	 * modify client information
	 * 
	 * @param clientVO ClientVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateClientInfo(ClientVO clientVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// update main data
			long reCnt = clientDao.editClient(clientVO);
			// update external data
			// long extReCnt = clientDao.editClientExt(clientVO);

			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("client.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("client.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * generate client list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getClientList(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}

			List<ClientVO> re = clientDao.selectClientList(options);
			long totalCount = clientDao.selectClientTotalCount(options);
			long filteredCount = clientDao.selectClientFilteredCount(options);

			if (re != null && re.size() > 0) {

				re.stream().map(temp -> {
					ClientVO obj = temp;
					obj.setViewStatus(CommonUtils.getViewStatusForClient(temp.getClientStatus(), temp.getIsOn(),
							temp.getIsProtector()));
					return obj;
				}).collect(Collectors.toList());

				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data in group
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO selectClientListForGroups(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientVO> re = clientDao.selectClientListForGroups(options);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in selectClientListForGroups : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data for insert client group
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientListForAddingGroup(String groupId) {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientVO> re = clientDao.selectClientListForAddingGroup(groupId);

			if (re != null && re.size() > 0) {

				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientListForAddingGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client information data
	 * 
	 * @param clientId string target client id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO selectClientInfo(String clientId) {

		ResultVO resultVO = new ResultVO();

		try {

			ClientVO re = clientDao.selectClientInfo(clientId);

			if (re != null) {

				// select certificate information
				CertificateFactory fact = CertificateFactory.getInstance("X.509");
				InputStream input = new ByteArrayInputStream(re.getKeyInfo().getBytes(StandardCharsets.UTF_8));
				X509Certificate clientCert = (X509Certificate) fact.generateCertificate(input);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				re.setKeySerialNO(clientCert.getSerialNumber().toString());
				re.setKeyDateFrom(sdf.format(clientCert.getNotBefore()));
				re.setKeyDateTo(sdf.format(clientCert.getNotAfter()));

				ClientVO[] row = new ClientVO[1];
				row[0] = re;
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
			logger.error("error in selectClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * check duplicate client id
	 * 
	 * @param clientId string target client id
	 * @return StatusVO result status object
	 */
	@Override
	public StatusVO isExistClientId(String clientId) {

		StatusVO statusVO = new StatusVO();

		try {

			boolean re = clientDao.isExistClientId(clientId);

			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("client.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("client.result.noduplicate"));
			}

		} catch (Exception ex) {
			logger.error("error in isExistClientId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * revoke client certificate for delete client
	 * 
	 * @param clientId string target client id
	 * @return StatusVO result status object
	 */
	@Override
	public StatusVO revokeClientCertificate(String clientId) {
		StatusVO statusVO = new StatusVO();
		try {
			// get client information for revoke
			ClientVO clientVO = clientDao.selectClientInfo(clientId);
			if (clientVO != null) {
				// update
				long re = clientDao.updateRevokeInfo(clientId, LoginInfoHelper.getUserId());
				if (re <= 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
							MessageSourceHelper.getMessage("client.result.norevoked"));
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
							MessageSourceHelper.getMessage("client.result.revoked"));
				}
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		} catch (Exception ex) {
			logger.error("error in revokeClientCertificate : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * generate client group data list
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientListInGroup(String groupId) {

		ResultVO resultVO = new ResultVO();

		try {

			String[] groupArray = groupId.split(",");

			List<ClientVO> re = clientDao.selectClientListInGroup(groupArray);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientListInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientListInOnline() {

		ResultVO resultVO = new ResultVO();

		try {
			List<ClientVO> re = clientDao.selectClientListInOnline();

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientInOnline(String gubun) {
		ResultVO resultVO = new ResultVO();
		try {
			List<ClientVO> re = clientDao.selectClientInOnline(gubun);
			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientIdInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list include client group configuration
	 * 
	 * @param confId string rule configuration id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getOnlineClientIdsInClientUseConfId(String confId, String confTp) {

		ResultVO resultVO = new ResultVO();
		try {
			boolean hasDefault = confId != null && confId.endsWith(GPMSConstants.MSG_DEFAULT);
			List<ClientVO> re = clientDao.selectOnlineClientIdsInUserConf(confId, hasDefault, confTp);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getOnlineClientIdsInClientUseConfId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list include user id
	 * 
	 * @param userIds string array user id list
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getOnlineClientIdsInUserIds(String[] userIds) {

		ResultVO resultVO = new ResultVO();

		try {

			List<OnlineClientAndUserVO> re = clientDao.selectOnlineClientIdsInUserIds(userIds);

			if (re != null && re.size() > 0) {
				OnlineClientAndUserVO[] row = re.toArray(OnlineClientAndUserVO[]::new);
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
			logger.error("error in getOnlineClientIdsInUserIds : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list include client configuration id
	 * 
	 * @param confId string configuration id
	 * @param confTp string configuration type
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getOnlineClientIdsInClientConf(String confId, String confTp) {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientVO> re = clientDao.selectOnlineClientIdsInClientConf(confId, confTp);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getOnlineClientIdsInClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate online client data list include client id
	 *
	 * @param clientId string client id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getOnlineClientIdByClientId(String clientId) {

		ResultVO resultVO = new ResultVO();

		try {
			List<ClientVO> re = clientDao.selectOnlineClientIdInClientId(clientId);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getOnlineClientIdByClientId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list that attacked protector rule
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getViolatedClientList(HashMap<String, Object> options) {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}
			
			List<ClientVO> re = clientDao.selectViolatedClientList(options);
			long totalCount = clientDao.selectViolatedClientListTotalCount(options);
			long filteredCount = clientDao.selectViolatedClientListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getViolatedClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate client count that attacked protector rule
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getViolatedClientCount() {
		ResultVO resultVO = new ResultVO();
		try {
			HashMap<String, Object> map = new HashMap<>();
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			}
			
			long reCnt = clientDao.selectViolatedClientCount(map);
			AdminUserVO reVo = adminUserDao.selectDuplicateReqLoginData(LoginInfoHelper.getUserId());
			
			if (reCnt > -1 || reVo != null) {
				HashMap<String, Object> resultMap = new HashMap<>();
				resultMap.put("violatedCount", String.valueOf(reCnt));
				resultMap.put("dupLoginIp", reVo.getDupLoginTryIp());
				resultMap.put("dupLoginDate", reVo.getDupLoginTryDate());
				
				Object[] o = new Object[1];
				o[0] = resultMap;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getViolatedClientCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate client list data
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientAllList() {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientVO> re = clientDao.selectClientAllList();

			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientAllList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client count data by status
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getClientStatusSummary() {

		ResultVO resultVO = new ResultVO();

		try {
			HashMap<String, Object> map = new HashMap<>();
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			}

			ClientSummaryVO re = clientDao.selectClientStatusSummary(map);

			if (re != null) {
				ClientSummaryVO[] row = new ClientSummaryVO[1];
				row[0] = re;
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
			logger.error("error in getClientStatusSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate user login count data by status
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getLoginStatusSummary() {

		ResultVO resultVO = new ResultVO();

		try {

			ClientSummaryVO re = clientDao.selectLoginStatusSummary();

			if (re != null) {
				ClientSummaryVO[] row = new ClientSummaryVO[1];
				row[0] = re;
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
			logger.error("error in getLoginStatusSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate package count by client and need update
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getUpdatePackageSummary() {

		ResultVO resultVO = new ResultVO();
		try {
			ClientSummaryVO re = clientDao.selectUpdatePackageSummary();
			if (re != null) {
				ClientSummaryVO[] row = new ClientSummaryVO[1];
				row[0] = re;
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
			logger.error("error in getUpdatePackageSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client list data that need update package.
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getUpdatePackageClientList() {

		ResultVO resultVO = new ResultVO();
		try {
			List<UpdatePackageClientVO> re = clientDao.selectUpdatePackageClientList();

			if (re != null) {
				UpdatePackageClientVO[] row = re.toArray(UpdatePackageClientVO[]::new);
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
			logger.error("error in getUpdatePackageClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	@Override
	public ResultVO getClientListForNoticeInstantNotice(String noticePublishId) {
		ResultVO resultVO = new ResultVO();
		try {
			List<ClientVO> re = clientDao.selectOnlineClientIdsForNoticeInstantNotice(noticePublishId);
			if (re != null && re.size() > 0) {
				ClientVO[] row = re.toArray(ClientVO[]::new);
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
			logger.error("error in getClientListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}
}
