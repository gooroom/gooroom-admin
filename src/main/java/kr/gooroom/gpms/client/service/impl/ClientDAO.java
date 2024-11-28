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

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.ClientSummaryVO;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.client.service.UpdatePackageClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.OnlineClientAndUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * data access object class for client management process.
 * <p>
 * with client group management.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("clientDAO")
public class ClientDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientDAO.class);

	/**
	 * response client list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientVO List selected client object
	 */
	public List<ClientVO> selectClientList(HashMap<String, Object> options) {
		List<ClientVO> re;

		try {
			re = sqlSessionMeta.selectList("selectClientList", options);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response total count for client list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectClientTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientTotalCount", options);
	}

	/**
	 * response filtered total count for client list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectClientFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientFilteredCount", options);
	}

	/**
	 * response client list in group
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientVO List selected client object
	 */
	public List<ClientVO> selectClientListForGroups(HashMap<String, Object> options) {

		List<ClientVO> re;

		try {
			re = sqlSessionMeta.selectList("selectClientListForGroups", options);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientListForGroups : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response client list for insert client into group
	 * <p>
	 * select client not in a group.
	 * 
	 * @param groupId string target group id
	 * @return ClientVO List selected client object
	 */
	public List<ClientVO> selectClientListForAddingGroup(String groupId) {

		List<ClientVO> re;
		try {
			re = sqlSessionMeta.selectList("selectClientListForAddingGroup", groupId);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientListForAddingGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response client information data.
	 * 
	 * @param clientId string target client id
	 * @return ClientVO selected client object
	 */
	public ClientVO selectClientInfo(String clientId) {

		ClientVO re;
		try {
			re = sqlSessionMeta.selectOne("selectClientInfo", clientId);
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response result to check client id is duplicated.
	 * 
	 * @param clientId string target client id
	 * @return boolean
	 */
	public boolean isExistClientId(String clientId) {

		boolean re;

		try {

			re = sqlSessionMeta.selectOne("isExistClientId", clientId);

		} catch (Exception ex) {

			re = true;
			logger.error("error in isExistClientId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * update client data.
	 * <p>
	 * basic data.
	 * 
	 * @param vo ClientVO client information bean.
	 * @return long query result count
	 */
	public long editClient(ClientVO vo) throws SQLException {
		return sqlSessionMeta.insert("updateClient", vo);

	}

	/**
	 * update client data.
	 * <p>
	 * external data.
	 * 
	 * @param vo ClientVO client information bean.
	 * @return long query result count
	 */
	public long editClientExt(ClientVO vo) {
		return sqlSessionMeta.insert("updateClientExt", vo);

	}

	/**
	 * update client status for revoke client
	 * <p>
	 * update client revoke information and insert client revoke history.
	 * 
	 * @param clientId string target client id
	 * @param adminId  string administrator user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateRevokeInfo(String clientId, String adminId) throws SQLException {

		HashMap<String, String> map = new HashMap<>();
		map.put("statusCd", GPMSConstants.STS_REVOKED);
		map.put("grpId", GPMSConstants.DEFAULT_GROUPID);
		map.put("clientId", clientId);
		map.put("adminId", adminId);

		long re1 = sqlSessionMeta.update("updateClientRevoke", map);
		if (re1 > 0) {
			// 1. delete client extension
			sqlSessionMeta.delete("deleteClientExtension", clientId);
			// 2. delete noti target
			sqlSessionMeta.delete("deleteClientForNoti", clientId);
			// 3. delete client_security_state
			sqlSessionMeta.delete("deleteClientForSecurity", clientId);
			
			// 4. delete client_profile_mstr
			sqlSessionMeta.delete("deleteClientForProfile", clientId);
			// 5. delete client_package_state
			sqlSessionMeta.delete("deleteClientForPackge", clientId);
			// 6. delete client_package
			sqlSessionMeta.delete("deleteClientForPackageState", clientId);

			// 7. delete CLIENT_ACCESS
			sqlSessionMeta.delete("deleteClientForAccess", clientId);
			// 8. delete CLIENT_CFG
			// don't delete passphrase
			
			// 9. update job target
			sqlSessionMeta.insert("updateJobTargetToRevoke", map);
			// 10. insert history
			map.put("chgTp", "REVOKE");
			sqlSessionMeta.insert("insertRevokeHistory", map);
		}

		return re1;
	}

	/**
	 * select client list in group.
	 * 
	 * @param groupIds string array target group id list
	 * @return ClientVO list selected client data
	 * @throws SQLException
	 */
	public List<ClientVO> selectClientListInGroup(String[] groupIds) throws SQLException {

		List<ClientVO> re;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("groupIds", groupIds);
			re = sqlSessionMeta.selectList("selectClientListInGroup", map);

		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientListInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select online client list
	 * 
	 * @return ClientVO list
	 */
	public List<ClientVO> selectClientListInOnline() {

		List<ClientVO> re;
		try {
			re = sqlSessionMeta.selectList("selectClientListInOnline");
		} catch (Exception ex) {
			re = null;
			logger.error("error in selectClientListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select online client
	 * 
	 * @return ClientVO list
	 */
	public List<ClientVO> selectClientInOnline(String gubun) {

		List<ClientVO> re = null;
		try {
			if (GPMSConstants.GUBUN_ALL.equalsIgnoreCase(gubun)) {
				re = sqlSessionMeta.selectList("selectAllClientInOnline");
			} else if (GPMSConstants.GUBUN_ONE.equalsIgnoreCase(gubun)) {
				re = sqlSessionMeta.selectList("selectClientInOnline");
			}
		} catch (Exception ex) {
			logger.error("error in selectClientInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select online client list by configuration data
	 * 
	 * @param confId    string array configuration id
	 * @param hasDefault boolean has default configuration id in first parameter
	 * @param confTp string
	 * @return ClientVO list
	 */
	public List<ClientVO> selectOnlineClientIdsInUserConf(String confId, boolean hasDefault, String confTp) {

		List<ClientVO> re;
		try {

			if (hasDefault) {
				HashMap<String, String> param = new HashMap<>();
				param.put("confId", confId);
				param.put("confTp", confTp);
				re = sqlSessionMeta.selectList("onlineClientIdsInUserConfDefault", param);
			} else {
				re = sqlSessionMeta.selectList("onlineClientIdsInClientUseConfId", confId);
			}

		} catch (Exception ex) {
			re = null;
			logger.error("error in selectOnlineClientIdsInUserConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select online client list by user data
	 * 
	 * @param userIds string array user id list
	 * @return ClientVO list
	 */
	public List<OnlineClientAndUserVO> selectOnlineClientIdsInUserIds(String[] userIds) {

		List<OnlineClientAndUserVO> re = null;
		try {

			HashMap<String, Object> map = new HashMap<>();
			map.put("userIds", userIds);

			re = sqlSessionMeta.selectList("onlineClientIdsInUserIds", map);

		} catch (Exception ex) {
			logger.error("error in selectOnlineClientIdsInUserIds : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * select online client list by client configuration data
	 * 
	 * @param confId string configuration id
	 * @param confTp string configuration type
	 * @return ClientVO list
	 */
	public List<ClientVO> selectOnlineClientIdsInClientConf(String confId, String confTp) {
		List<ClientGroupVO> groups;
		List<ClientVO> resultRe = new ArrayList<>();
		try {
			if (confId.endsWith(GPMSConstants.RULE_GRADE_DEFAULT)) {
				HashMap<String, String> map = new HashMap<>();
				map.put("confId", confId);
				map.put("confTp", confTp);
				groups = sqlSessionMeta.selectList("selectClientGroupIdByDefaultConf", map);
			} else {
				groups = sqlSessionMeta.selectList("selectClientGroupIdByConf", confId);
			}

			if (groups != null && groups.size() > 0) {
				List<ClientVO> tempRe;
				for (ClientGroupVO vo : groups) {
					String groupId = vo.getGrpId();
					tempRe = sqlSessionMeta.selectList("onlineClientIdsInClientGroup", groupId);
					if (tempRe != null && tempRe.size() > 0) {
						resultRe.addAll(tempRe);
					}
				}
			}
		} catch (Exception ex) {
			resultRe = null;
			logger.error("error in selectOnlineClientIdsInClientConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return resultRe;
	}

	/**
	 * select online client list by client configuration data
	 * 
	 * @param confIds string array configuration id list
	 * @return ClientVO list
	 */
	public List<ClientVO> selectOnlineClientIdsInClientConfArray(String[] confIds) {

		List<ClientGroupVO> groups;
		List<ClientVO> resultRe = new ArrayList<>();

		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("confIds", confIds);
			groups = sqlSessionMeta.selectList("clientGroupsInClientConfArray", map);
			if (groups != null && groups.size() > 0) {
				List<ClientVO> tempRe;
				for (ClientGroupVO vo : groups) {
					String groupId = vo.getGrpId();
					tempRe = sqlSessionMeta.selectList("onlineClientIdsInClientGroup", groupId);
					if (tempRe != null && tempRe.size() > 0) {
						resultRe.addAll(tempRe);
					}
				}
			}
		} catch (Exception ex) {
			resultRe = null;
			logger.error("error in selectOnlineClientIdsInClientConfArray : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return resultRe;
	}

	/**
	 * select attacked client list
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientVO list
	 */
	public List<ClientVO> selectViolatedClientList(HashMap<String, Object> options) {
		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectViolatedClientList", options);
		} catch (Exception ex) {
			logger.error("error in selectViolatedClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response total count for attacked client list
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectViolatedClientListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectViolatedClientListTotalCount", options);
	}

	/**
	 * response filtered total count for attacked client list options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectViolatedClientListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectViolatedClientListFilteredCount", options);
	}

	/**
	 * select attacked client count
	 * 
	 * @return ClientVO list
	 */
	public long selectViolatedClientCount(HashMap<String, Object> map) {
		return sqlSessionMeta.selectOne("selectViolatedClientCount", map);
	}

	/**
	 * select client list
	 * 
	 * @return ClientVO list
	 */
	public List<ClientVO> selectClientAllList() {

		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("clientListAll");
		} catch (Exception ex) {
			logger.error("error in selectClientAllList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * generate client count by status
	 * 
	 * @return ClientSummaryVO summary object
	 */
	public ClientSummaryVO selectClientStatusSummary(HashMap<String, Object> map) {

		ClientSummaryVO totalRe = new ClientSummaryVO();
		try {

			ClientSummaryVO re1 = sqlSessionMeta.selectOne("selectClientStatusSummary", map);
			if (re1 != null) {
				totalRe.setOnCount(re1.getOnCount());
				totalRe.setOffCount(re1.getOffCount());
				totalRe.setTotalCount(re1.getTotalCount());
				totalRe.setRevokeCount(re1.getRevokeCount());
			}

			ClientSummaryVO re2 = sqlSessionMeta.selectOne("selectLoginStatusSummary", map);
			if (re2 != null) {
				totalRe.setUserCount(re2.getUserCount());
				totalRe.setLoginCount(re2.getLoginCount());
			}

			ClientSummaryVO re3 = sqlSessionMeta.selectOne("selectUpdatePackageSummary", map);
			if (re1 != null) {
				totalRe.setNoUpdateCount(re3.getNoUpdateCount());
				totalRe.setMainUpdateCount(re3.getMainUpdateCount());
				totalRe.setUpdateCount(re3.getUpdateCount());
			}

		} catch (Exception ex) {
			totalRe = null;
			logger.error("error in selectClientStatusSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return totalRe;
	}

	/**
	 * generate user count by login status
	 * 
	 * @return ClientSummaryVO summary object
	 */
	public ClientSummaryVO selectLoginStatusSummary() {

		ClientSummaryVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectLoginStatusSummary");
		} catch (Exception ex) {
			logger.error("error in selectLoginStatusSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * generate client count by need update package
	 * 
	 * @return ClientSummaryVO summary object
	 */
	public ClientSummaryVO selectUpdatePackageSummary() {

		ClientSummaryVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectUpdatePackageSummary");
		} catch (Exception ex) {
			logger.error("error in selectUpdatePackageSummary : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * generate package count for client by need update
	 * 
	 * @return ClientSummaryVO summary object
	 */
	public List<UpdatePackageClientVO> selectUpdatePackageClientList() {

		List<UpdatePackageClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectUpdatePackageClientList");
		} catch (Exception ex) {
			logger.error("error in selectUpdatePackageClientList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response online client data list by noticePublishId.
	 * 
	 * @param noticePublishId string
	 */
	public List<ClientVO> selectOnlineClientIdsForNoticeInstantNotice(String noticePublishId) {
		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectOnlineClientIdsForNoticeInstantNotice", noticePublishId);
		} catch (Exception ex) {
			logger.error("error in selectOnlineClientIdsForNoticeInstantNotice : {}, {}, {}",
					GPMSConstants.CODE_SYSERROR, MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR),
					ex.toString());
		}
		return re;
	}

	/**
	 *
	 * @param ClientId string
	 * @return UserReqVO list
	 */
	public List<ClientVO> selectOnlineClientIdInClientId(String ClientId) {
		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectOnlineClientIdInClientId", ClientId);
		} catch (Exception ex) {
			logger.error("error in selectOnlineClientIdInClientId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

}
