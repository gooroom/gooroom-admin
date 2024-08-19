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

import kr.gooroom.gpms.config.service.ActivateGroupViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.CtrlItemVO;
import kr.gooroom.gpms.config.service.CtrlPropVO;
import kr.gooroom.gpms.config.service.RuleIdsVO;

/**
 * data access object class for configuration management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("ctrlMstDAO")
public class CtrlMstDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(CtrlMstDAO.class);

	/**
	 * response control item list data
	 * 
	 * @param mngObjTp string control item type
	 * @return CtrlItemVO List
	 */
	public List<CtrlItemVO> selectCtrlItemList(String mngObjTp) {
		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemList", mngObjTp);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response control item and property list data
	 * 
	 * @param  map HashMap<String, Object>
	 * @return CtrlItemVO List
	 */
	public List<CtrlItemVO> selectCtrlItemAndPropList(HashMap<String, Object> map) {
		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemAndPropList", map);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemAndPropList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response control item and property list data with paging.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return CtrlItemVO List
	 */
	public List<CtrlItemVO> selectCtrlItemAndPropListPaged(HashMap<String, Object> options) {
		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemAndPropListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemAndPropList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response filtered control item and property list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectCtrlItemAndPropListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("CtrlMstDAO.selectCtrlItemAndPropListFilteredCount", options);
	}

	/**
	 * response Total control item and property list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectCtrlItemAndPropListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("CtrlMstDAO.selectCtrlItemAndPropListTotalCount", options);
	}

	/**
	 * response control item data by item id
	 * 
	 * @param objId string control item id
	 * @return CtrlItemVO
	 */
	public CtrlItemVO selectCtrlItem(String objId) {

		CtrlItemVO re = null;
		try {
			re = sqlSessionMeta.selectOne("CtrlMstDAO.selectCtrlItem", objId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response control item datas by item ids
	 * 
	 * @param objIds string array control item id array
	 * @return CtrlItemVO List
	 */
	public List<CtrlItemVO> selectParentCtrlItem(String[] objIds) {
		List<CtrlItemVO> re = null;

		HashMap<String, Object> map = new HashMap<>();
		map.put("objIds", objIds);

		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectParentCtrlItems", map);
		} catch (Exception ex) {
			logger.error("error in selectParentCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response control property list by item id
	 * 
	 * @param objId string control item id
	 * @return CtrlPropVO List
	 */
	public List<CtrlPropVO> selectCtrlPropList(String objId) {
		List<CtrlPropVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlPropList", objId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlPropList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * create new control item and property data
	 * 
	 * @param vo CtrlItemVO control item object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertCtrlItem(CtrlItemVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.insertCtrlItem", vo);
	}

	/**
	 * create default control item and property data
	 * 
	 * @param vo CtrlItemVO control item object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertCtrlDefaultItem(CtrlItemVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.insertCtrlDefaultItem", vo);
	}

	/**
	 * modify control item and property data
	 * 
	 * @param vo CtrlItemVO control item object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateCtrlItem(CtrlItemVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.updateCtrlItem", vo);
	}

	/**
	 * delete control item and property data
	 * 
	 * @param objId string item id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteCtrlItem(String objId) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.deleteCtrlItem", objId);
	}

	/**
	 * create control property data
	 * 
	 * @param vo CtrlPropVO control property object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertCtrlProp(CtrlPropVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.insertCtrlProp", vo);
	}

	/**
	 * update control property data
	 * 
	 * @param vo CtrlPropVO control property object bean
	 * @return long data insert result count.
	 */
	public long updateCtrlProp(CtrlPropVO vo) {
		return sqlSessionMeta.insert("CtrlMstDAO.updateCtrlProp", vo);
	}

	/**
	 * delete control property data
	 * 
	 * @param objId string property id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteCtrlProp(String objId) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.deleteCtrlProp", objId);
	}

	/**
	 * delete control property data with link information
	 * 
	 * @param objId string property id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteCtrlPropWithLink(String objId) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.deleteCtrlPropWithLink", objId);
	}

	/**
	 * delete control item data in group mapper
	 * 
	 * @param confId string configuration id
	 * @return long data insert result count.
	 */
	public long deleteCtrlItemInGroupMapper(String confId) {
		return sqlSessionMeta.insert("CtrlMstDAO.deleteCtrlItemInGroupMapper", confId);
	}

	/**
	 * response client group id by configuration id
	 * 
	 * @param confId string configuration id
	 * @return ClientGroupVO List
	 */
	public List<ClientGroupVO> selectClientGroupIdByConfId(String confId) {

		List<ClientGroupVO> re = null;

		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectClientGroupIdByConfId", confId);
		} catch (Exception ex) {
			logger.error("error in selectClientGroupIdByConfId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response configuration id in client group id
	 * 
	 * @param groupId  string group id
	 * @param confType string configuration type
	 * @return String List
	 */
	public List<String> selectConfIdInClientGroupId(String groupId, String confType) {

		List<String> re = null;

		try {

			HashMap<String, String> map = new HashMap<>();
			map.put("groupId", groupId);
			map.put("confType", confType);

			re = sqlSessionMeta.selectList("CtrlMstDAO.selectConfIdInClientGroupId", map);

		} catch (Exception ex) {
			logger.error("error in selectConfIdInClientGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * create cloned control item
	 * 
	 * @param vo CtrlItemVO control item object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertClonedCtrlItem(CtrlItemVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.insertClonedCtrlItem", vo);
	}

	/**
	 * create cloned control property data
	 * 
	 * @param vo CtrlPropVO control property object bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertClonedCtrlProp(CtrlPropVO vo) throws SQLException {
		return sqlSessionMeta.insert("CtrlMstDAO.insertClonedCtrlProp", vo);
	}

	/**
	 * delete group rule configuration relation data
	 * 
	 * @param confId string configuration id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteGroupRuleConf(String confId, String confType) throws SQLException {
		HashMap<String, String> hm = new HashMap<>();
		hm.put("confId", confId);
		hm.put("confTp", confType);
		return sqlSessionMeta.insert("CtrlMstDAO.deleteGroupRuleConf", hm);
	}

	/**
	 * delete department rule configuration relation data
	 * 
	 * @param confId string configuration id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteDeptRuleConf(String confId, String confType) throws SQLException {
		HashMap<String, String> hm = new HashMap<>();
		hm.put("confId", confId);
		hm.put("confTp", confType);
		return sqlSessionMeta.insert("CtrlMstDAO.deleteDeptRuleConf", hm);
	}

	/**
	 * delete user rule configuration relation data
	 * 
	 * @param confId string configuration id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long deleteUserRuleConf(String confId, String confType) throws SQLException {
		HashMap<String, String> hm = new HashMap<>();
		hm.put("confId", confId);
		hm.put("confTp", confType);
		return sqlSessionMeta.insert("CtrlMstDAO.deleteUserRuleConf", hm);
	}

	/**
	 * response total rule id.
	 * 
	 * @param userId   string user id
	 * @param clientId string client id
	 * @return String List
	 */
	public List<RuleIdsVO> selectRuleIdsByClientAndUser(String userId, String clientId) {

		List<RuleIdsVO> re = null;

		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("userId", userId);
			map.put("clientId", clientId);

			if (userId != null && !("".equals(userId))) {
				re = sqlSessionMeta.selectList("CtrlMstDAO.selectRuleIdsByClientAndUser", map);
			} else {
				re = sqlSessionMeta.selectList("CtrlMstDAO.selectRuleIdsByClientAndEmptyUser", map);
			}
		} catch (Exception ex) {
			logger.error("error in selectRuleIdsByClientAndUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response rule ids by Group id.
	 * 
	 * @param groupId   client group id
	 * @return RuleIdsVO vo
	 */
	public RuleIdsVO selectRuleIdsByGroupId(String groupId) {

		RuleIdsVO re = null;
		try {
			re = sqlSessionMeta.selectOne("CtrlMstDAO.selectRuleIdByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectRuleIdsByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	
	/**
	 * response control item data by group id
	 * 
	 * @param groupId string control group id
	 * @return CtrlItemVO
	 */
	public List<CtrlItemVO> selectCtrlItemByGroupId(String groupId) {

		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	/**
	 * response control property list by group id
	 * 
	 * @param groupId string control group id
	 * @return CtrlPropVO List
	 */
	public List<CtrlPropVO> selectCtrlPropListByGroupId(String groupId) {

		List<CtrlPropVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlPropListByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlPropListByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}
	
	/**
	 * response control item data by dept cd
	 * 
	 * @param deptCd string
	 * @return CtrlItemVO
	 */
	public List<CtrlItemVO> selectCtrlItemByDeptCd(String deptCd) {

		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemByDeptCd", deptCd);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	/**
	 * response control property list by dept cd
	 * 
	 * @param deptCd string control dept cd
	 * @return CtrlPropVO List
	 */
	public List<CtrlPropVO> selectCtrlPropListByDeptCd(String deptCd) {

		List<CtrlPropVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlPropListByDeptCd", deptCd);
		} catch (Exception ex) {
			logger.error("error in selectCtrlPropListByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response control item data by user id
	 * 
	 * @param userId string
	 * @return CtrlItemVO
	 */
	public List<CtrlItemVO> selectCtrlItemByUserId(String userId) {

		List<CtrlItemVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlItemByUserId", userId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlItemByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}
	
	/**
	 * response control property list by user id
	 * 
	 * @param userId string
	 * @return CtrlPropVO List
	 */
	public List<CtrlPropVO> selectCtrlPropListByUserId(String userId) {

		List<CtrlPropVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectCtrlPropListByUserId", userId);
		} catch (Exception ex) {
			logger.error("error in selectCtrlPropListByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * 정책 - 적용 그룹 리스트
	 * @param options HashMap<String, Object> options for select
	 * @return List<ActivateGroupViewVO> ActivateGroupList
	 */
	public List<ActivateGroupViewVO> selectActivateGroupListPaged(HashMap<String, Object> options) {

		List<ActivateGroupViewVO> re = null;
		try {
			re = sqlSessionMeta.selectList("CtrlMstDAO.selectActivateGroupListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectActivateGroupListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response Total control item and property list data by options.
	 *
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectActivateGroupListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("CtrlMstDAO.selectActivateGroupListTotalCount", options);
	}

	/**
	 * response filtered control item and property list data by options.
	 *
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectActivateGroupListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("CtrlMstDAO.selectActivateGroupListFilteredCount", options);
	}
}
