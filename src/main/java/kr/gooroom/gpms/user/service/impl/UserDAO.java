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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.UserAndClientVO;
import kr.gooroom.gpms.user.service.UserRoleVO;
import kr.gooroom.gpms.user.service.UserVO;

/**
 * data access object class for user management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("userDAO")
public class UserDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

	/**
	 * response user list data
	 * 
	 * @return UserVO List
	 */
	public List<UserVO> readUserList() {

		List<UserVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectUserList", "");

		} catch (Exception ex) {
			logger.error("error in readUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user list data without delete user
	 * @return UserVO List
	 */
	public List<UserVO> readUserListWithoutDel(HashMap<String, Object> options) {

		List<UserVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectUserListWithoutDel", options);

		} catch (Exception ex) {
			logger.error("error in readUserListWithoutDel : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user list data for paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return UserVO List
	 */
	public List<UserVO> selectUserListPaged(HashMap<String, Object> options) {

		List<UserVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectUserListPaged", options);

		} catch (Exception ex) {
			logger.error("error in selectUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered count for user list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectUserListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectUserListFilteredCount", options);
	}

	/**
	 * response total count for user list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectUserListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectUserListTotalCount", options);
	}

	/**
	 * check duplicate user id
	 * 
	 * @param usertId string user id
	 * @return boolean true if exist
	 */
	public boolean isExistUserId(String usertId) {

		boolean re = true;
		try {
			re = sqlSessionMeta.selectOne("isExistUserId", usertId);

		} catch (Exception ex) {
			logger.error("error in isExistUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = true;
		}

		return re;
	}

	/**
	 * create new user information data
	 * 
	 * @param vo UserVO user information data bean
	 * @return long data insert result count.
	 */
	public long createUser(UserVO vo) {
		return sqlSessionMeta.insert("insertUser", vo);
	}
	
	/**
	 * create new user information data - from file
	 * 
	 * @param vo UserVO
	 * @return long
	 */
	public long createUserRawData(UserVO vo) {
		return sqlSessionMeta.insert("insertUserRawData", vo);
	}

	/**
	 * response user information data
	 * 
	 * @param userId string user id
	 * @return UserVO user data bean
	 * @throws SQLException
	 */
	public UserVO readUserData(String userId) throws SQLException {

		UserVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectUserData", userId);

		} catch (Exception ex) {
			logger.error("error in readUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * modify user information data
	 * 
	 * @param vo UserVO user data bean
	 * @return long data update result count.
	 */
	public long updateUserData(UserVO vo) {
		return sqlSessionMeta.update("updateUserData", vo);
	}

	/**
	 * assign configuration data to user
	 * 
	 * @param userId    string target user Id
	 * @param regUserId string administrator user id
	 * @param configId  string configuration id
	 * @param configTp  string configuration type
	 * @return long query result count
	 */
	public long insertOrUpdateConfigWithUser(String userId, String regUserId, String configId, String configTp) {

		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("regUserId", regUserId);
		map.put("configId", configId);
		map.put("configTp", configTp);

		return sqlSessionMeta.update("insertOrUpdateConfigWithUser", map);
	}

	/**
	 * delete config data for user
	 * 
	 * @param userId   string target user Id
	 * @param configTp string configuration type
	 * @return long query result count
	 */
	public long deleteConfigWithUser(String userId, String configTp) {

		HashMap<String, String> map = new HashMap<>();
		map.put("userId", userId);
		map.put("configTp", configTp);
		return sqlSessionMeta.update("deleteConfigWithUser", map);
	}

	/**
	 * delete user information data in organization.
	 * <p>
	 * use when delete organization.
	 * 
	 * @param deptCds string array organization id
	 * @return long data delete result count.
	 */
	public long removeUserInDeptList(String[] deptCds) {

		long re = 0L;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCds", deptCds);
			map.put("modUserId", LoginInfoHelper.getUserId());
			re = sqlSessionMeta.update("UserDAO.updateUserInDeptList", map);

		} catch (Exception ex) {
			logger.error("error in removeUserInDeptList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = 0L;
		}

		return re;
	}

	/**
	 * register user's NFC data
	 * 
	 * @param vo UserVO user data bean
	 * @return long data update result count.
	 */
	public long editUserNfcData(UserVO vo) {
		return sqlSessionMeta.insert("updateUserNfcData", vo);

	}

	/**
	 * reset user login trial count
	 * 
	 * @param vo UserVO user data bean
	 * @return long data delete result count.
	 */

	public long updateUserLoginTrialCount(UserVO vo) {
		return sqlSessionMeta.insert("updateUserLoginTrialCount", vo);
	}


	/**
	 * delete user information data
	 * 
	 * @param vo UserVO user data bean
	 * @return long data delete result count.
	 */

	public long deleteUserData(UserVO vo) {
		return sqlSessionMeta.insert("deleteUserData", vo);
	}

	/**
	 * delete user information data by id
	 * 
	 * @param userId String UserId
	 * @param modUserId String modUserId
	 * @return long data delete result count.
	 * @throws SQLException
	 */

	public long deleteUserDataById(String userId, String modUserId) throws SQLException {

		HashMap<String, Object> map = new HashMap<>();
		map.put("status", GPMSConstants.STS_DELETE_USER);
		map.put("modUserId", modUserId);
		map.put("userId", userId);
		
		return sqlSessionMeta.insert("deleteUserDataById", map);
	}

	/**
	 * response user configuration id by user id
	 * 
	 * @param userId string user id
	 * @return UserRoleVO user role data bean
	 */
	public UserRoleVO selectUserConfIdByUserId(String userId) {

		UserRoleVO re = null;
		try {
			re = sqlSessionMeta.selectOne("UserDAO.selectUserConfIdByUserId", userId);
		} catch (Exception ex) {
			logger.error("error in selectUserConfIdByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user configuration id by dept code from user id
	 * 
	 * @param userId string user id
	 * @return UserRoleVO user role data bean
	 */
	public UserRoleVO selectUserConfIdByDeptCdFromUserId(String userId) {

		UserRoleVO re = null;
		try {
			re = sqlSessionMeta.selectOne("UserDAO.selectUserConfIdByDeptCdFromUserId", userId);
		} catch (Exception ex) {
			logger.error("error in selectUserConfIdByDeptCdFromUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user configuration id by dept code
	 * 
	 * @param deptCd string dept code
	 * @return UserRoleVO user role data bean
	 */
	public UserRoleVO selectUserConfIdByDeptCd(String deptCd) {

		UserRoleVO re = null;
		try {
			re = sqlSessionMeta.selectOne("UserDAO.selectUserConfIdByDeptCd", deptCd);
		} catch (Exception ex) {
			logger.error("error in selectUserConfIdByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user configuration id by group id
	 * 
	 * @param groupId string group id
	 * @return UserRoleVO user role data bean
	 */
	public UserRoleVO selectUserConfIdByGroupId(String groupId) {

		UserRoleVO re = null;
		try {
			re = sqlSessionMeta.selectOne("UserDAO.selectUserConfIdByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectUserConfIdByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user in organizations.
	 *
	 * @return UserAndRoleVO List
	 */
	public List<UserVO> selectUserListNotInDept(String[] deptCds) {

		List<UserVO> re = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCds", deptCds);
			re = sqlSessionMeta.selectList("UserDAO.selectUserListNotInDept", map);

		} catch (Exception ex) {
			logger.error("error in selectUserListNotInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user in organizations.
	 * 
	 * @param deptCds string array organizations
	 * @return UserAndRoleVO List
	 * @throws SQLException
	 */
	public List<UserVO> selectUserListInDept(String[] deptCds) throws SQLException {

		List<UserVO> re = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCds", deptCds);
			re = sqlSessionMeta.selectList("UserDAO.selectUserListInDept", map);

		} catch (Exception ex) {
			logger.error("error in selectUserListInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response user list data in organizations for paged.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return UserVO List
	 */
	public List<UserVO> selectUserListPagedInDept(HashMap<String, Object> options) {

		List<UserVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectUserListPagedInDept", options);
		} catch (Exception ex) {
			logger.error("error in selectUserListPagedInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered count for user list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectUserListInDeptFilteredCount(HashMap<String, Object> options) {

		return sqlSessionMeta.selectOne("selectUserListInDeptFilteredCount", options);
	}

	/**
	 * response total count for user list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectUserListInDeptTotalCount(HashMap<String, Object> options) {

		return sqlSessionMeta.selectOne("selectUserListInDeptTotalCount", options);
	}

	/**
	 * response user in online.
	 * 
	 * @return UserAndClientVO List
	 */
	public List<UserAndClientVO> selectUserListInOnline() {

		List<UserAndClientVO> re = null;
		try {
			re = sqlSessionMeta.selectList("UserDAO.selectUserListInOnline");

		} catch (Exception ex) {
			logger.error("error in selectUserListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * insert user information in organization
	 * 
	 * @param deptCd string organization id
	 * @param userId string user id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long updateUserWithDept(String deptCd, String userId) throws SQLException {

		HashMap<String, Object> map = new HashMap<>();
		map.put("deptCd", deptCd);
		map.put("userId", userId);
		map.put("regUserId", LoginInfoHelper.getUserId());

		return sqlSessionMeta.insert("updateUserWithDept", map);
	}

	
	/**
	 * response result delete user table all
	 * 
	 * @return boolean
	 */
	public boolean deleteUserAll() {
		boolean reFlag = false; 
		
		long existRowCount = sqlSessionMeta.selectOne("selectUserALLCount");
		if(existRowCount > 0) {
			long deleteRowCount = sqlSessionMeta.delete("deleteUserALL");
			return existRowCount == deleteRowCount;			
		} else {
			reFlag = true;
		}
		
		return reFlag;
	}
	
	
	/**
	 * insert user history
	 * 
	 * @param chgTp string
	 * @param userId string
	 * @param regUserId string
	 * @return long query result count
	 * @throws SQLException
	 */
	public long createUserHist(String chgTp, String userId, String regUserId) throws SQLException {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("chgTp", chgTp);
		map.put("regUserId", regUserId);
		map.put("userId", userId);
		
		return sqlSessionMeta.insert("insertUserHist", map);
	}
	
	
	/**
	 * delete user mapping data with rule.
	 * 
	 * @param userId string target user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteUserFromRule(String userId) throws SQLException {
		return sqlSessionMeta.delete("deleteDeptFromRule", userId);
	}

	/**
	 * delete user mapping data with notify.
	 * 
	 * @param userId string target user id
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteUserForNoti(String userId) throws SQLException {
		return sqlSessionMeta.delete("deleteUserForNoti", userId);
	}

	/**
	 * user id duplicate checklist .
	 * @param ids
	 * @return list
	 */
	public List<String> selectUserListForDuplicateUserId (HashMap<String, Object> ids) {
		List<String> res = null;
		res = sqlSessionMeta.selectList("selectUserListForDuplicateUserId", ids);
		return res;
	}
}
