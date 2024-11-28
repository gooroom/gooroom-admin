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
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
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

@Repository("clientGroupDAO")
public class ClientGroupDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientGroupDAO.class);

	/**
	 * response client group list.
	 * 
	 * @return ClientGroupVO List selected client group object
	 */
	public List<ClientGroupVO> selectClientGroupList() {

		List<ClientGroupVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientGroupList");
		} catch (Exception ex) {
			logger.error("error in selectClientGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response client group list for paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientGroupVO List selected client group object
	 */
	public List<ClientGroupVO> selectClientGroupListPaged(HashMap<String, Object> options) {

		List<ClientGroupVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientGroupListPaged", options);

		} catch (Exception ex) {
			logger.error("error in selectClientGroupListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response filtered count for client group list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectClientGroupListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientGroupListFilteredCount", options);
	}

	/**
	 * response total count for client group list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectClientGroupListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientGroupListTotalCount", options);
	}

	/**
	 * response client group information data.
	 * 
	 * @param grpId string target group id
	 * @return ClientGroupVO selected client group object
	 * @throws SQLException
	 */
	public ClientGroupVO selectClientGroupData(String grpId) throws SQLException {

		ClientGroupVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectClientGroupData", grpId);
		} catch (Exception ex) {
			logger.error("error in selectClientGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response client group information data list.
	 * 
	 * @param groupIds string target group id array
	 * @return ClientGroupVO selected client group object
	 */
	public List<ClientGroupVO> selectClientGroupNodeList(String[] groupIds) {

		List<ClientGroupVO> re = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("groupIds", groupIds);
			re = sqlSessionMeta.selectList("selectClientGroupNodeList", map);
		} catch (Exception ex) {
			logger.error("error in selectClientGroupNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	/**
	 * insert new client group data
	 * 
	 * @param vo ClientGroupVO client group bean
	 * @return long query result count
	 * @throws SQLException
	 */
	public long createClientGroup(ClientGroupVO vo) throws SQLException {
		return sqlSessionMeta.insert("insertClientGroup", vo);
	}

	/**
	 * insert client group history
	 * 
	 * @param chgTp string
	 * @param groupId string
	 * @param regUserId string
	 * @return long query result count
	 * @throws SQLException
	 */
	public long createClientGroupHist(String chgTp, String groupId, String regUserId) throws SQLException {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("chgTp", chgTp);
		map.put("regUserId", regUserId);
		map.put("grpId", groupId);
		
		return sqlSessionMeta.insert("insertClientGroupHist", map);
	}

	/**
	 * insert default client group data
	 * 
	 * @param vo ClientGroupVO client group bean
	 * @return long query result count
	 * @throws SQLException
	 */
	public long createDefaultClientGroup(ClientGroupVO vo) throws SQLException {

		return sqlSessionMeta.insert("insertDefaultClientGroup", vo);

	}

	/**
	 * check group name duplicate by parent id 
	 * 
	 * @param parentGrpId string parent group id
	 * @param groupName string group name
	 * @return boolean Boolean value
	 */
	public boolean isExistGroupNameByParentId(String parentGrpId, String groupName) {
		boolean re = true;
		try {
			if (parentGrpId != null && parentGrpId.length() > 0) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("parentGrpId", parentGrpId);
				map.put("groupName", groupName);
				re = sqlSessionMeta.selectOne("isExistGroupNameByParentId", map);
			}
		} catch (Exception ex) {
			logger.error("error in isExistGroupNameInParent : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * check group name duplicate by group id 
	 * 
	 * @param grpId string group id
	 * @param groupName string group name
	 * @return boolean Boolean value
	 */
	public boolean isExistGroupNameByGroupId(String grpId, String groupName) {
		boolean re = true;
		try {
			if (grpId != null && grpId.length() > 0) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("grpId", grpId);
				map.put("groupName", groupName);
				re = sqlSessionMeta.selectOne("isExistGroupNameByGroupId", map);
			}
		} catch (Exception ex) {
			logger.error("error in isExistGroupNameByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * delete client group data.
	 * 
	 * @param grpId string target group id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteClientGroup(String grpId) throws SQLException {
		return sqlSessionMeta.delete("deleteClientGroup", grpId);
	}

	/**
	 * delete client group array data.
	 * 
	 * @param groupIds string array target group id list
	 * @return long query result count
	 */
	public boolean deleteClientGroupList(String[] groupIds, String regUserId) {
	
		if(groupIds != null && groupIds.length > 0) {
			try {
				for (String groupId : groupIds) {
					HashMap<String, String> map = new HashMap<>();
					map.put("chgTp", "DELETE");
					map.put("regUserId", regUserId);
					map.put("grpId", groupId);
					// 1. insert client group history
					long re = sqlSessionMeta.insert("insertClientGroupHist", map);
					if (re > 0) {
						// 2. delete master
						long delRe = sqlSessionMeta.delete("deleteClientGroup", groupId);
						if (delRe > 0) {
							// 3. delete rule table for group : gr_group_rule_conf
							sqlSessionMeta.delete("deleteClientGroupFromRule", groupId);
							// 4. delete admin table for group : admin_clientgrp
							sqlSessionMeta.delete("deleteClientGroupForAdmin", groupId);
						}
					}
				}
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	/**
	 * delete client group mapping data with rule.
	 * 
	 * @param grpId string target group id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteClientGroupFromRule(String grpId) throws SQLException {
		return sqlSessionMeta.delete("deleteClientGroupFromRule", grpId);
	}

	/**
	 * delete client group mapping data with admin.
	 * 
	 * @param grpId string target group id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteClientGroupForAdmin(String grpId) throws SQLException {
		return sqlSessionMeta.delete("deleteClientGroupForAdmin", grpId);
	}

	/**
	 * delete client group mapping data with notify.
	 * 
	 * @param grpId string target group id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteClientGroupForNoti(String grpId) throws SQLException {
		return sqlSessionMeta.delete("deleteClientGroupForNoti", grpId);
	}

	/**
	 * delete client group array mapping data with rule.
	 * 
	 * @param groupIds string array target group id list
	 * @return long query result count
	 */
	public long deleteClientGroupListFromRule(String[] groupIds) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("groupIds", groupIds);
		return sqlSessionMeta.delete("deleteClientGroupListFromRule", map);
	}

	/**
	 * edit client group information data.
	 * 
	 * @param clientGroupVO ClientGroupVO target group data
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateClientGroup(ClientGroupVO clientGroupVO) throws SQLException {
		return sqlSessionMeta.update("updateClientGroup", clientGroupVO);
	}

	/**
	 * insert client in client group
	 * <p>
	 * create mapping data with client and group
	 * 
	 * @param grpId    string target group id
	 * @param clientIds string target client id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateGroupToClient(String grpId, String[] clientIds) throws SQLException {

		HashMap<String, Object> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("clientIds", clientIds);
		map.put("modUserId", LoginInfoHelper.getUserId());
		return sqlSessionMeta.update("updateGroupToClient", map);
	}

	/**
	 * remove clients in group. update to default group id in client
	 * 
	 * @param grpId string target group id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateGroupToClientForDelete(String grpId) throws SQLException {

		HashMap<String, Object> map = new HashMap<>();
		map.put("newGroupId", GPMSConstants.DEFAULT_GROUPID);
		map.put("grpId", grpId);
		map.put("modUserId", LoginInfoHelper.getUserId());
		return sqlSessionMeta.delete("updateGroupToClientForDelete", map);
	}

	/**
	 * remove clients in group list (multi). update to default group id in client
	 * 
	 * @param groupIds string array target group id list
	 * @return long query result count
	 */
	public long updateGroupListToClientForDelete(String[] groupIds) {

		HashMap<String, Object> map = new HashMap<>();
		map.put("newGroupId", GPMSConstants.DEFAULT_GROUPID);
		map.put("groupIds", groupIds);
		map.put("modUserId", LoginInfoHelper.getUserId());

		return sqlSessionMeta.delete("updateGroupListToClientForDelete", map);
	}

	/**
	 * assign configuration data to client group
	 * 
	 * @param grpId     string target group id
	 * @param regUserId string administrator user id
	 * @param configId  string configuration id
	 * @param configTp  string configuration type
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertOrUpdateConfigWithGroup(String grpId, String regUserId, String configId, String configTp)
			throws SQLException {

		HashMap<String, String> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("regUserId", regUserId);
		map.put("configId", configId);
		map.put("configTp", configTp);

		return sqlSessionMeta.update("insertOrUpdateConfigWithGroup", map);
	}

	/**
	 * assign admin's group relation to new client group
	 * 
	 * @param grpId     string target group id
	 * @param regUserId string administrator user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertGroupInAdminRelation(String grpId, String regUserId)
			throws SQLException {

		HashMap<String, String> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("regUserId", regUserId);

		return sqlSessionMeta.update("insertGroupInAdminRelation", map);
	}

	/**
	 * delete config data for client group
	 * 
	 * @param grpId    string target group id
	 * @param configTp string configuration type
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteConfigWithGroup(String grpId, String configTp) throws SQLException {

		HashMap<String, String> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("configTp", configTp);
		return sqlSessionMeta.update("deleteConfigWithGroup", map);
	}

	/**
	 * response client id list by group id
	 * 
	 * @param grpId string group id
	 * @return ClientVO List
	 */
	public List<ClientVO> selectOnlineClientIdsByGroupId(String grpId) {
		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("ClientGroupDAO.selectOnlineClientIdsByGroupId", grpId);
		} catch (Exception ex) {
			logger.error("error in selectOnlineClientIdsByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response client id list by group id List
	 * 
	 * @param grpId string group id List
	 * @return ClientVO List
	 */
	public List<ClientVO> selectOnlineClientIdsByGroupList(String[] grpId) {
		List<ClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("ClientGroupDAO.selectOnlineClientIdsByGroupList", grpId);
		} catch (Exception ex) {
			logger.error("error in selectOnlineClientIdsByGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * select root client group info
	 * 
	 * @return ClientGroupVO
	 */
	public ClientGroupVO selectRootChildrenClientGroupInfo() {
		return sqlSessionMeta.selectOne("selectRootChildrenClientGroupInfo");
	}

	/**
	 * select list of client group by parent group id (one depth)
	 * 
	 * @return List<ClientGroupVO>
	 */
	public List<ClientGroupVO> selectChildrenClientGroupList(HashMap<String, Object> map) {
		List<ClientGroupVO> re;
		try {
			re = sqlSessionMeta.selectList("selectChildrenClientGroupList", map);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * select list of client group by parent group id (one depth)
	 * 
	 * @return List<ClientGroupVO>
	 */
	public List<ClientGroupVO> selectChildrenClientGroupListByAdmin(HashMap<String, Object> map) {
		List<ClientGroupVO> re;
		try {
			re = sqlSessionMeta.selectList("selectChildrenClientGroupListByAdmin", map);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * child group list (all depth)
	 * 
	 * @return List<ClientGroupVO>
	 */
	public List<ClientGroupVO> selectAllChildrenGroupList(String grpId) {
		List<ClientGroupVO> re;
		try {
			re = sqlSessionMeta.selectList("selectAllChildrenGroupList", grpId);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}

	/**
	 * child group list by multi parents (all depth)
	 * 
	 * @return List<ClientGroupVO>
	 * @throws SQLException
	 */
	public List<ClientGroupVO> selectAllChildrenGroupListByParents(String[] groupIds) throws SQLException {
		List<ClientGroupVO> re;
		HashMap<String, Object> map = new HashMap<>();
		map.put("groupIds", groupIds);
		try {
			re = sqlSessionMeta.selectList("selectAllChildrenGroupListByParents", map);
		} catch (Exception ex) {
			re = null;
		}
		return re;
	}
}
