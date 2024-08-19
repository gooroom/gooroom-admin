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
import jakarta.inject.Inject;
import kr.gooroom.gpms.client.service.ClientGroupService;
import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Client management service implement class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientGroupService")
public class ClientGroupServiceImpl implements ClientGroupService {

	private static final Logger logger = LoggerFactory.getLogger(ClientGroupServiceImpl.class);

	@Resource(name = "clientDAO")
	private ClientDAO clientDao;

	@Resource(name = "clientGroupDAO")
	private ClientGroupDAO clientGroupDao;

	@Inject
	private CustomJobMaker jobMaker;

	/**
	 * generate client group list data
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO selectClientGroupList() {

		ResultVO resultVO = new ResultVO();
		try {

			List<ClientGroupVO> re = clientGroupDao.selectClientGroupList();

			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.toArray(ClientGroupVO[]::new);
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
			logger.error("error in selectClientGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client group list data for paging.
	 * 
	 * @param options HashMap<String, Object>.
	 * @return ResultPagingVO result object
	 */
	@Override
	public ResultPagingVO getClientGroupListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientGroupVO> re = clientGroupDao.selectClientGroupListPaged(options);
			long totalCount = clientGroupDao.selectClientGroupListTotalCount(options);
			long filteredCount = clientGroupDao.selectClientGroupListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.toArray(ClientGroupVO[]::new);
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
			logger.error("error in getClientGroupListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client group information data
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO readClientGroupData(String groupId) {
		ResultVO resultVO = new ResultVO();
		try {
			ClientGroupVO re = clientGroupDao.selectClientGroupData(groupId);
			if (re != null) {
				ClientGroupVO[] row = new ClientGroupVO[1];
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
			logger.error("error in readClientGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate client group information data list
	 * 
	 * @param groupIds string target group id array
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO readClientGroupNodeList(String[] groupIds) {
		ResultVO resultVO = new ResultVO();
		try {
			List<ClientGroupVO> re = clientGroupDao.selectClientGroupNodeList(groupIds);
			
			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.toArray(ClientGroupVO[]::new);
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
			logger.error("error in readClientGroupNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * get client group information data
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ClientGroupVO getClientGroupData(String groupId) throws Exception {
		return clientGroupDao.selectClientGroupData(groupId);
	}

	/**
	 * create new client group data
	 * 
	 * @param clientGroupVO ClientGroupVO group data bean
	 * @param isDefault     boolean which default group
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createClientGroup(ClientGroupVO clientGroupVO, boolean isDefault) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			// insert main information
			long reCnt;
			if (isDefault) {
				reCnt = clientGroupDao.createDefaultClientGroup(clientGroupVO);
			} else {
				reCnt = clientGroupDao.createClientGroup(clientGroupVO);
			}

			if (reCnt > 0) {
				// save client configuration information
				long cnt;
				String cfgId = clientGroupVO.getClientConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_CLIENTCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getHostNameConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_HOSTNAMECONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getUpdateServerConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_UPDATESERVERCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getBrowserRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_BROWSERRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getMediaRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_MEDIARULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getSecurityRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_SECURITYRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getFilteredSoftwareRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}
				
				cfgId = clientGroupVO.getCtrlCenterItemRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getPolicyKitRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_POLICYKITRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cfgId = clientGroupVO.getDesktopConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					cnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId(), cfgId, GPMSConstants.TYPE_DESKTOPCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				}
				
				// insert ADMIN_CLIENTGRP : if group's upper group id is 'CGRPDEFAULT'
				if(GPMSConstants.DEFAULT_GROUPID.equals(clientGroupVO.getUprGrpId())) {
					cnt = clientGroupDao.insertGroupInAdminRelation(clientGroupVO.getGrpId(),
							clientGroupVO.getRegUserId());
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("clientgroup.result.insert"));

			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("clientgroup.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {

			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * check duplicate client group name in group id
	 * 
	 * @param parentGrpId string parent group id
	 * @param groupName string target group name
	 * @return StatusVO result status object
	 */
	@Override
	public StatusVO isExistGroupNameByParentId(String parentGrpId, String groupName) {
		StatusVO statusVO = new StatusVO();
		try {
			boolean re = clientGroupDao.isExistGroupNameByParentId(parentGrpId, groupName);
			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("clientgroup.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("clientgroup.result.noduplicate"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistGroupNameByParentId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * check duplicate client group name in group id
	 * 
	 * @param grpId   string target group id
	 * @param groupName string target group name
	 * @return StatusVO result status object
	 */
	@Override
	public StatusVO isExistGroupNameByGroupId(String grpId, String groupName) {
		StatusVO statusVO = new StatusVO();
		try {
			boolean re = clientGroupDao.isExistGroupNameByGroupId(grpId, groupName);
			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("clientgroup.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("clientgroup.result.noduplicate"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistGroupNameByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * delete client group
	 * 
	 * @param groupId string target group id
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteClientGroup(String groupId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long reCnt1 = clientGroupDao.deleteClientGroup(groupId);
			clientGroupDao.deleteClientGroupFromRule(groupId);
			clientGroupDao.updateGroupToClientForDelete(groupId);

			if (reCnt1 > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("clientgroup.result.delete"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("clientgroup.result.nodelete"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * delete client group array
	 * 
	 * @param groupIds string[] target group id array
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteClientGroupList(String[] groupIds, String isDeleteClient) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			// 1. get all group id - child
			String[] newGroupIds = groupIds;
			List<ClientGroupVO> re = clientGroupDao.selectAllChildrenGroupListByParents(groupIds);
			if (re != null && re.size() > 0) {
				newGroupIds = new String[re.size()];
				for(int i = 0; i < re.size(); i++) {
					newGroupIds[i] = re.get(i).getGrpId();
				}
			}

			// 1-1. get client list from job and revoke
			List<ClientVO> clientRe = clientDao.selectClientListInGroup(newGroupIds);
			String[] newClientIds = new String[clientRe.size()];
			for(int i = 0; i < clientRe.size(); i++) {
				newClientIds[i] = clientRe.get(i).getClientId();
			}

			// DELETE CLIENT GROUP
			String regUserId = LoginInfoHelper.getUserId();
			for (String newGroupId : newGroupIds) {
				// 2. insert history
				long histRe = clientGroupDao.createClientGroupHist("DELETE", newGroupId, regUserId);
				if (histRe > 0) {
					// 3. delete master
					long mstrRe = clientGroupDao.deleteClientGroup(newGroupId);
					if (mstrRe > 0) {
						// 4. delete rule config
						clientGroupDao.deleteClientGroupFromRule(newGroupId);
						// 5. delete admin config
						clientGroupDao.deleteClientGroupForAdmin(newGroupId);
						// 6. delete notify config
						clientGroupDao.deleteClientGroupForNoti(newGroupId);
					} else {
						throw new SQLException();
					}
				} else {
					throw new SQLException();
				}
			}
			
			if(newClientIds.length > 0) {
				clientGroupDao.updateGroupToClient(GPMSConstants.DEFAULT_GROUPID, newClientIds);
				if ("Y".equalsIgnoreCase(isDeleteClient)) {
					for (String newClientId : newClientIds) {
						long revokeRe = clientDao.updateRevokeInfo(newClientId, regUserId);
						if (revokeRe < 1) {
							throw new SQLException();
						}
					}
				} else {
					// create JOB for client config(rule)
					ClientGroupVO defaultGroupVO = clientGroupDao.selectClientGroupData(GPMSConstants.DEFAULT_GROUPID);
					jobMaker.createJobForGroup(newClientIds, defaultGroupVO, null);
				}
			}
						
			// RESULT
			statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
					MessageSourceHelper.getMessage("clientgroup.result.delete"));
			
		} catch (SQLException sqlEx) {
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * change(update) client group
	 * 
	 * @param clientGroupVO ClientGroupVO group data
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateClientGroup(ClientGroupVO clientGroupVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			// update main(basic) information
			long updCnt = clientGroupDao.updateClientGroup(clientGroupVO);

			if (updCnt > 0) {
				long chgCnt;
				String cfgId = clientGroupVO.getClientConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_CLIENTCONF);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_CLIENTCONF);
				}

				cfgId = clientGroupVO.getHostNameConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_HOSTNAMECONF);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_HOSTNAMECONF);
				}

				cfgId = clientGroupVO.getUpdateServerConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_UPDATESERVERCONF);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_UPDATESERVERCONF);
				}

				cfgId = clientGroupVO.getBrowserRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_BROWSERRULE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_BROWSERRULE);
				}

				cfgId = clientGroupVO.getMediaRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_MEDIARULE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_MEDIARULE);
				}

				cfgId = clientGroupVO.getSecurityRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_SECURITYRULE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_SECURITYRULE);
				}

				cfgId = clientGroupVO.getFilteredSoftwareRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_FILTEREDSOFTWARE);
				}

				cfgId = clientGroupVO.getCtrlCenterItemRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_CTRLCENTERITEMRULE);
				}

				cfgId = clientGroupVO.getPolicyKitRuleId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_POLICYKITRULE);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_POLICYKITRULE);
				}

				cfgId = clientGroupVO.getDesktopConfigId();
				if (cfgId != null && cfgId.length() > 0) {
					chgCnt = clientGroupDao.insertOrUpdateConfigWithGroup(clientGroupVO.getGrpId(),
							clientGroupVO.getModUserId(), cfgId, GPMSConstants.TYPE_DESKTOPCONF);
					if (chgCnt < 1) {
						throw new SQLException();
					}
				} else {
					clientGroupDao.deleteConfigWithGroup(clientGroupVO.getGrpId(),
							GPMSConstants.TYPE_DESKTOPCONF);
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("clientgroup.result.update"));
			} else {
				// rollback
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("clientgroup.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * insert client data in group
	 * 
	 * @param groupId     string target group id
	 * @param client_list string array that client id list
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateGroupToClient(String groupId, String[] client_list) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			boolean isOk = true;
			long cnt = clientGroupDao.updateGroupToClient(groupId, client_list);
			if (cnt < 1) {
				isOk = false;
			}
			if (isOk) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("clientgroup.result.insertclient"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("clientgroup.result.noinsertclient"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateGroupToClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateGroupToClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * check exist default client group
	 * 
	 * @return StatusVO result status object
	 */
	@Override
	public StatusVO isExistDefaultClientGroup() {

		StatusVO statusVO = new StatusVO();

		try {
			ClientGroupVO re = clientGroupDao.selectClientGroupData(GPMSConstants.DEFAULT_GROUPID);
			if (re != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistDefaultClientGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * response client id array in group
	 * 
	 * @param groupId string client group id
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getOnlineClientIdsByGroupId(String groupId) {
		ResultVO resultVO = new ResultVO();
		try {
			List<ClientVO> re = clientGroupDao.selectOnlineClientIdsByGroupId(groupId);
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
			logger.error("error in getOnlineClientIdsByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * response client id array in group List
	 * 
	 * @param groupId string client group id
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getOnlineClientIdsByGroupList(String[] groupId) {
		ResultVO resultVO = new ResultVO();
		try {
			List<ClientVO> re = clientGroupDao.selectOnlineClientIdsByGroupList(groupId);
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
			logger.error("error in getOnlineClientIdsByGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * response client id array in group array
	 * 
	 * @param groupIds string[] client group id array
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO readClientIdsByGroupIdList(String[] groupIds) {

		ResultVO resultVO = new ResultVO();

		try {
			List<ClientVO> re = clientDao.selectClientListInGroup(groupIds);

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
			logger.error("error in readClientIdsByGroupIdList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * response client group children id array
	 * 
	 * @return ResultVO
	 */
	@Override
	public ResultVO getChildrenClientGroupList(String grpId, String hasWithRoot) {

		ResultVO resultVO = new ResultVO();

		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("grpId", grpId);
			
			if(GPMSConstants.DEFAULT_GROUPID.equals(grpId) || "0".equals(grpId)) {
				String grRole = LoginInfoHelper.getUserGRRole();
				if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
					map.put("adminId", LoginInfoHelper.getUserId());
				}
			}

			List<ClientGroupVO> re = clientGroupDao.selectChildrenClientGroupListByAdmin(map);
			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.toArray(ClientGroupVO[]::new);
				resultVO.setData(row);
				
				if(GPMSConstants.GUBUN_YES.equalsIgnoreCase(hasWithRoot)) {
					ClientGroupVO rootInfo = clientGroupDao.selectRootChildrenClientGroupInfo();
					resultVO.setExtend(new Object[] {rootInfo});
				}
				
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getChildrenClientGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * update group rule and conf
	 * 
	 * @param grpId string
	 * @param cfgId string
	 * @param confType string
	 * @return StatusVO
	 */
	@Override
	public StatusVO updateClientGroupConf(String grpId, String cfgId, String confType) {
		StatusVO statusVO = new StatusVO();
		try {
			if (cfgId != null && cfgId.length() > 0) {
				long cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, LoginInfoHelper.getUserId(), cfgId, confType);
				if (cnt < 0) {
					throw new SQLException();
				}
			} else {
				clientGroupDao.deleteConfigWithGroup(grpId, confType);
			}
			statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
					MessageSourceHelper.getMessage("clientgroup.result.update"));

		} catch (Exception ex) {
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * get child group list
	 * 
	 * @return ResultVO
	 */
	@Override
	public ResultVO getAllChildrenGroupList(String grpId) {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientGroupVO> re = clientGroupDao.selectAllChildrenGroupList(grpId);

			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.toArray(ClientGroupVO[]::new);
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
			logger.error("error in getAllChildrenGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 수정
	 * 
	 * @return StatusVO
	 * @throws Exception
	 */
	@Override
	// ROLLBACK, need transaction.
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateRuleInfoToMultiGroup(String[] grpIds, String clientConfigId, String hostNameConfigId,
			String updateServerConfigId, String browserRuleId, String mediaRuleId, String securityRuleId,
			String filteredSoftwareRuleId, String ctrlCenterItemRuleId, String policyKitRuleId, String desktopConfId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			if (grpIds != null && grpIds.length > 0) {

				String modUserId = LoginInfoHelper.getUserId();
				for (String grpId : grpIds) {

					long cnt;
					// 단말정책
					if (clientConfigId != null && !"".equals(clientConfigId)) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, clientConfigId,
								GPMSConstants.TYPE_CLIENTCONF);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_CLIENTCONF);
					}

					// 호스트정보
					if (hostNameConfigId != null && !"".equals(hostNameConfigId)) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, hostNameConfigId,
								GPMSConstants.TYPE_HOSTNAMECONF);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_HOSTNAMECONF);
					}

					// 업데이트서버정보
					if (updateServerConfigId != null && !"".equals(updateServerConfigId)) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, updateServerConfigId,
								GPMSConstants.TYPE_UPDATESERVERCONF);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_UPDATESERVERCONF);
					}

					// 브라우져설정
					if (browserRuleId != null && !"".equals(browserRuleId)) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, browserRuleId,
								GPMSConstants.TYPE_BROWSERRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_BROWSERRULE);
					}

					// 매체제어설정
					if (mediaRuleId != null && mediaRuleId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, mediaRuleId,
								GPMSConstants.TYPE_MEDIARULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_MEDIARULE);
					}

					// 단말보안설정
					if (securityRuleId != null && securityRuleId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, securityRuleId,
								GPMSConstants.TYPE_SECURITYRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_SECURITYRULE);
					}

					// filtered software rule
					if (filteredSoftwareRuleId != null && filteredSoftwareRuleId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, filteredSoftwareRuleId,
								GPMSConstants.TYPE_FILTEREDSOFTWARE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_FILTEREDSOFTWARE);
					}

					// control center item rule
					if (ctrlCenterItemRuleId != null && ctrlCenterItemRuleId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, ctrlCenterItemRuleId,
								GPMSConstants.TYPE_CTRLCENTERITEMRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					}

					// policy kit rule
					if (policyKitRuleId != null && policyKitRuleId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, policyKitRuleId,
								GPMSConstants.TYPE_POLICYKITRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_POLICYKITRULE);
					}

					// 데스크톱설정
					if (desktopConfId != null && desktopConfId.length() > 0) {
						cnt = clientGroupDao.insertOrUpdateConfigWithGroup(grpId, modUserId, desktopConfId,
								GPMSConstants.TYPE_DESKTOPCONF);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						clientGroupDao.deleteConfigWithGroup(grpId, GPMSConstants.TYPE_DESKTOPCONF);
					}
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");
				}

			} else {
				
				// error, no exist deptCd
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 수정되지 않았습니다.");
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateRuleInfoToMultiGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateRuleInfoToMultiGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

}
