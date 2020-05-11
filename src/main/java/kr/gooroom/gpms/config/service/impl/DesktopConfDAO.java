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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.DesktopConfVO;
import kr.gooroom.gpms.config.service.DesktopInfoVO;

@Repository("desktopConfDAO")
public class DesktopConfDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(DesktopConfDAO.class);

	/**
	 * response desktop configuration list data
	 * 
	 * @return DesktopConfVO List data
	 * @throws SQLException
	 */
	public List<DesktopConfVO> readDesktopConfList() throws SQLException {

		List<DesktopConfVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectDesktopConfList", "");

		} catch (Exception ex) {
			logger.error("error in readDesktopConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response desktop configuration list data
	 * 
	 * @return DesktopConfVO List data
	 * @throws SQLException
	 */
	public List<DesktopConfVO> selectDesktopConfListPaged(HashMap<String, Object> options) throws SQLException {

		List<DesktopConfVO> re = null;
		try {

			re = sqlSessionMeta.selectList("selectDesktopConfListPaged", options);

		} catch (Exception ex) {
			logger.error("error in selectDesktopConfListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response filtered total count for desktop configuration list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectDesktopConfListFilteredCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectDesktopConfListFilteredCount", options);
	}

	/**
	 * response total count for desktop configuration list by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectDesktopConfListTotalCount(HashMap<String, Object> options) throws SQLException {

		return (long) sqlSessionMeta.selectOne("selectDesktopConfListTotalCount", options);
	}

	/**
	 * insert new desktop configuration data.
	 * 
	 * @param vo DesktopConfVO desktop configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createDesktopConf(DesktopConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertDesktopConf", vo);

	}

	/**
	 * create cloned desktop configuration data.
	 * 
	 * @param vo DesktopConfVO desktop configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long cloneDesktopConf(DesktopConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertClonedDesktopConf", vo);

	}

	/**
	 * insert new desktop configuration application data.
	 * 
	 * @param vo DesktopConfVO desktop configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createDesktopAppInConf(DesktopConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertDesktopAppInConf", vo);

	}

	/**
	 * create cloned desktop configuration application data.
	 * 
	 * @param vo DesktopConfVO desktop configuration data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long cloneDesktopAppInConf(DesktopConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertClonedDesktopAppInConf", vo);

	}

	/**
	 * response desktop configuration data
	 * 
	 * @return DesktopConfVO data bean
	 * @throws SQLException
	 */
	public DesktopConfVO getDesktopConfData(String desktopConfId) throws SQLException {

		DesktopConfVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectDesktopConf", desktopConfId);

		} catch (Exception ex) {
			logger.error("error in getDesktopConfData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response desktop configuration data
	 * 
	 * @return DesktopConfVO data bean
	 * @throws SQLException
	 */
	public DesktopInfoVO getDesktopInfoByConfId(Map<?, ?> paramMap) throws SQLException {

		DesktopInfoVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectDesktopInfoByConfId", paramMap);

		} catch (Exception ex) {
			logger.error("error in getDesktopInfoByConfId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * update desktop configuration data.
	 * 
	 * @param vo DesktopConfVO desktop configuration data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateDesktopConf(DesktopConfVO vo) throws SQLException {

		return (long) sqlSessionMeta.update("updateDesktopConf", vo);

	}

	/**
	 * delete desktop configuration data.
	 * 
	 * @param desktopConfId string desktop configuration id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteDesktopConf(String desktopConfId) throws SQLException {

		return sqlSessionMeta.delete("deleteDesktopConf", desktopConfId);
	}

	/**
	 * delete desktop configuration mapping data.
	 * 
	 * @param desktopConfId string desktop configuration id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteDesktopConfInClientGroup(String desktopConfId) throws SQLException {

		return sqlSessionMeta.delete("deleteDesktopConfInClientGroup", desktopConfId);
	}

	/**
	 * delete desktop application data in desktop configuration mapping data.
	 * 
	 * @param desktopConfId string desktop configuration id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteDesktopAppsInConf(String desktopConfId) throws SQLException {

		return sqlSessionMeta.delete("deleteDesktopAppsInConf", desktopConfId);
	}

	/**
	 * response desktop configuration data by group id
	 * 
	 * @param groupId string target group id
	 * @return DesktopInfoVO data bean
	 * @throws SQLException
	 */
	public DesktopConfVO selectDesktopConfByGroupId(String groupId) throws SQLException {
		DesktopConfVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDesktopConfByGroupId", groupId);
		} catch (Exception ex) {
			logger.error("error in selectDesktopConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response desktop configuration data by dept cd
	 * 
	 * @param deptCd string target dept cd
	 * @return DesktopInfoVO data bean
	 * @throws SQLException
	 */
	public DesktopConfVO selectDesktopConfByDeptCd(String deptCd) throws SQLException {
		DesktopConfVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDesktopConfByDeptCd", deptCd);
		} catch (Exception ex) {
			logger.error("error in selectDesktopConfByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response desktop configuration data by user id
	 * 
	 * @param userId string target user id
	 * @return DesktopInfoVO data bean
	 * @throws SQLException
	 */
	public DesktopConfVO selectDesktopConfByUserId(String userId) throws SQLException {

		DesktopConfVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDesktopConfByUserId", userId);
		} catch (Exception ex) {
			logger.error("error in selectDesktopConfByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}
}
