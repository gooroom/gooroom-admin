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

package kr.gooroom.gpms.user.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.account.service.ActHistoryVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.AdminUserVO;

/**
 * data access object class for administrator user management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("adminUserDAO")
public class AdminUserDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(AdminUserDAO.class);

	/**
	 * response administrator user list data
	 * 
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public List<AdminUserVO> selectAdminUserList() throws SQLException {
		List<AdminUserVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectAdminUserList", "");
		} catch (Exception ex) {
			logger.error("error in selectAdminUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response administrator user list data for paging
	 * 
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public List<AdminUserVO> selectAdminUserListPaged(HashMap<String, Object> options) throws SQLException {
		List<AdminUserVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectAdminUserListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectAdminUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response filtered count for administrator user list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectAdminUserListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectAdminUserListFilteredCount", options);
	}

	/**
	 * response total count for administrator user list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectAdminUserListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectAdminUserListTotalCount", options);
	}

	/**
	 * check duplicate user id
	 * 
	 * @param adminId string administrator user id
	 * @return boolean true if exist
	 * @throws SQLException
	 */
	public boolean isExistAdminUserId(String adminId) throws SQLException {

		boolean re = true;
		try {
			re = ((Boolean) sqlSessionMeta.selectOne("isExistAdminUserId", adminId)).booleanValue();
		} catch (Exception ex) {
			logger.error("error in isExistAdminUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = true;
		}

		return re;
	}

	/**
	 * create new administrator user information data
	 * 
	 * @param adminId string administrator user id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createAdminUser(AdminUserVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertAdminUser", vo);

	}

	/**
	 * response administrator user information by user id
	 * 
	 * @param adminId string administrator user id
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public AdminUserVO selectAdminUserData(HashMap<String, Object> options) throws SQLException {

		AdminUserVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectAdminUserData", options);

		} catch (Exception ex) {
			logger.error("error in selectAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * modify administrator user information data
	 * 
	 * @param vo AdminUserVO administrator user data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateAdminUserData(AdminUserVO vo) throws SQLException {
		return (long) sqlSessionMeta.update("updateAdminUserData", vo);
	}

	/**
	 * modify current administrator user information data (pollingCycle)
	 * 
	 * @param vo AdminUserVO administrator user data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateCurrentAdminUserData(AdminUserVO vo) throws SQLException {
		return (long) sqlSessionMeta.update("updateCurrentAdminUserData", vo);
	}

	/**
	 * delete administrator user information data
	 * 
	 * @param vo AdminUserVO administrator user data bean
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteAdminUserData(AdminUserVO vo) throws SQLException {
		return (long) sqlSessionMeta.delete("deleteAdminUserData", vo);
	}

	/**
	 * response administrator user information by user id and password for
	 * authority.
	 * 
	 * @param adminId string administrator user id
	 * @param adminPw string administrator user password
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public AdminUserVO selectAdminUserAuthAndInfo(String adminId, String adminPw) throws SQLException {
		AdminUserVO re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("adminId", adminId);
			map.put("adminPw", adminPw);

			re = sqlSessionMeta.selectOne("selectAdminUserAuthAndInfo", map);

		} catch (Exception ex) {
			logger.error("error in selectAdminUserAuthAndInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response administrator user action logging list paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ActHistoryVO List
	 * @throws SQLException
	 */
	public List<ActHistoryVO> selectAdminActListPaged(HashMap<String, Object> options) {
		List<ActHistoryVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectAdminActListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectAdminActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}
	
	/**
	 * response total count for administrator user action logging list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectAdminActListTotalCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectAdminActListTotalCount", options);
	}

	/**
	 * response filtered count for administrator user action logging list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectAdminActListFilteredCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectAdminActListFilteredCount", options);
	}

	/**
	 * response administrator action logging history data for paging
	 * 
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public List<ActHistoryVO> selectAdminRecordListPaged(HashMap<String, Object> options) throws SQLException {
		List<ActHistoryVO> re = null;
		try {
			re = sqlSessionMeta.selectList("adminDAO.selectAdminRecordListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectAdminRecordListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response filtered count for administrator action logging history data by
	 * options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectAdminRecordListFilteredCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("adminDAO.selectAdminRecordListFilteredCount", options);
	}

	/**
	 * response total count for administrator action logging history data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectAdminRecordListTotalCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("adminDAO.selectAdminRecordListTotalCount", options);
	}

	/**
	 * assign configuration data to admin user
	 * 
	 * @param adminId   string target admin Id
	 * @param ruleType  string rule type
	 * @param ruleName  string rule name
	 * @param ruleValue string rule value
	 * @param regUserId string administrator user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertOrUpdateAdminRule(String adminId, String ruleType, String ruleName, String ruleValue,
			String regUserId) throws SQLException {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("ruleType", ruleType);
		map.put("ruleName", ruleName);
		map.put("ruleValue", ruleValue);
		map.put("regUserId", regUserId);

		return (long) sqlSessionMeta.update("insertOrUpdateAdminRule", map);
	}

	/**
	 * delete admin connectable ip list
	 * 
	 * @param adminId string target admin Id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteAdminUserConnIps(String adminId) throws SQLException {
		return (long) sqlSessionMeta.delete("deleteAdminUserConnIps", adminId);
	}

	/**
	 * insert admin connectable ip data
	 * 
	 * @param adminId   string target admin Id
	 * @param regUserId string administrator user id
	 * @param connIp    string target ip
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertAdminUserConnIp(String adminId, String regUserId, String connIp) throws SQLException {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("regUserId", regUserId);
		map.put("connIp", connIp);
		return (long) sqlSessionMeta.update("insertAdminUserConnIp", map);
	}

	/**
	 * delete admin's managed group id list
	 * 
	 * @param adminId string target admin Id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteAdminUserGrpIds(String adminId) throws SQLException {
		return (long) sqlSessionMeta.delete("deleteAdminUserGrpIds", adminId);
	}

	/**
	 * insert admin's managed group id
	 * 
	 * @param adminId   string target admin Id
	 * @param regUserId string administrator user id
	 * @param connIp    string target ip
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertAdminUserGrpId(String adminId, String regUserId, String grpId) throws SQLException {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("regUserId", regUserId);
		map.put("grpId", grpId);
		return (long) sqlSessionMeta.update("insertAdminUserGrpId", map);
	}

	/**
	 * delete admin's managed dept cd list
	 * 
	 * @param adminId string target admin Id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteAdminUserDeptCds(String adminId) throws SQLException {
		return (long) sqlSessionMeta.delete("deleteAdminUserDeptCds", adminId);
	}

	/**
	 * insert admin's managed dept cd
	 * 
	 * @param adminId   string target admin Id
	 * @param regUserId string administrator user id
	 * @param connIp    string target ip
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertAdminUserDeptCd(String adminId, String regUserId, String deptCd) throws SQLException {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("regUserId", regUserId);
		map.put("deptCd", deptCd);
		return (long) sqlSessionMeta.update("insertAdminUserDeptCd", map);
	}

	/**
	 * modify administrator user present (ip, sessionId) data
	 * 
	 * @param adminId   String
	 * @param connectIp String
	 * @param sessionId String
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long insertAdminUserPresentData(String adminId, String connectIp, String sessionId) throws SQLException {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("connectIp", connectIp);
		map.put("sessionId", sessionId);

		long rePresentData = (long) sqlSessionMeta.update("insertAdminUserPresentData", map);
		
		map.clear();
		map.put("actType", "L");
		map.put("actItem", "LOGIN");
		map.put("actData", "SUCCESS");
		map.put("accessIp", connectIp);
		map.put("userId", adminId);
		
		long reActLogData = (long) sqlSessionMeta.insert("insertUserActLogHistory", map);

		return (rePresentData > reActLogData) ? rePresentData : reActLogData;
	}

	/**
	 * delete administrator user present (ip, sessionId) data - logout
	 * 
	 * @param adminId String
	 * @param connectIp String
	 * @param sessionId String
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long deleteAdminUserPresentData(String adminId, String connectIp, String sessionId) throws SQLException {
		
		long rePresentData = (long) sqlSessionMeta.update("deleteAdminUserPresentData", adminId);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("actType", "L");
		map.put("actItem", "LOGOUT");
		map.put("actData", "SUCCESS");
		map.put("accessIp", connectIp);
		map.put("userId", adminId);
		
		long reActLogData = (long) sqlSessionMeta.insert("insertUserActLogHistory", map);

		return (rePresentData > reActLogData) ? rePresentData : reActLogData;
	}

	/**
	 * modify administrator login data - duplicate id login request
	 * 
	 * @param adminId String
	 * @param connectIp String
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateDuplicateReqLoginData(String adminId, String connectIp) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		map.put("connectIp", connectIp);
		
		return (long) sqlSessionMeta.update("updateDuplicateReqLoginData", map);
	}

	/**
	 * delete duplicate login request data
	 * 
	 * @param adminId String
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long deleteDuplicateReqLoginData(String adminId) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("adminId", adminId);
		
		return (long) sqlSessionMeta.update("deleteDuplicateReqLoginData", map);
	}

	/**
	 * select duplicate login trial info.
	 * 
	 * @param adminId String
	 * @return AdminUserVO
	 * @throws SQLException
	 */
	public AdminUserVO selectDuplicateReqLoginData(String adminId) throws SQLException {
		AdminUserVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDuplicateReqLoginData", adminId);
		} catch (Exception ex) {
			logger.error("error in selectDuplicateReqLoginData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}
	
	/**
	 * clear administrator login trial info
	 * 
	 * @param adminId String
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateLoginTrialData(String adminId) throws SQLException {
		return (long) sqlSessionMeta.update("updateLoginTrialData", adminId);
	}

	/**
	 * response administrator user Info
	 * authority.
	 *
	 * @param adminId string administrator user id
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public AdminUserVO selectAdminUserInfo(String adminId) throws SQLException {
		AdminUserVO re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("adminId", adminId);

			re = sqlSessionMeta.selectOne("selectAdminUserInfo", map);

		} catch (Exception ex) {
			logger.error("error in selectAdminUserInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response administrator user Authority
	 * authority.
	 *
	 * @param adminId string administrator user id
	 * @param adminRule string administrator Rule
	 * @return AdminUserVO List
	 * @throws SQLException
	 */
	public AdminUserVO selectAdminUserAuthority(String adminId, String adminRule) throws SQLException {
		AdminUserVO re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("adminId", adminId);
			map.put("adminRule", adminRule);

			re = sqlSessionMeta.selectOne("selectAdminUserAuthority", map);

		} catch (Exception ex) {
			logger.error("error in selectAdminUserAuthority : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}
	
}
