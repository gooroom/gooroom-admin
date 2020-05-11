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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.IconGroupVO;
import kr.gooroom.gpms.mng.service.IconVO;

/**
 * data access object class for icon management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("iconMngDAO")
public class IconMngDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(IconMngDAO.class);

	/**
	 * get icon information list data
	 * 
	 * @param grpId string icon group id
	 * @return IconVO List selected list data
	 * @throws SQLException
	 */
	public List<IconVO> selectIconList(String grpId) throws SQLException {

		List<IconVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("grpId", grpId);

			re = sqlSessionMeta.selectList("selectIconList", map);

		} catch (Exception ex) {
			logger.error("error in selectIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * get icon information list data that not include any icon group.
	 * 
	 * @return IconVO List selected list data
	 * @throws SQLException
	 */
	public List<IconVO> selectNoGroupIconList() throws SQLException {

		List<IconVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoGroupIconList");
		} catch (Exception ex) {
			logger.error("error in selectNoGroupIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * get icon information data.
	 * 
	 * @param iconId String icon id
	 * @return IconVO selected data
	 * @throws SQLException
	 */
	public IconVO selectIconData(String iconId) throws SQLException {

		IconVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectIconData", iconId);

		} catch (Exception ex) {
			logger.error("error in selectIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * insert new icon information data.
	 * 
	 * @param vo IconVO icon information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertIconData(IconVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertIconData", vo);

	}

	/**
	 * delete icon information data.
	 * 
	 * @param iconId string target icon id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconData(String iconId) throws SQLException {

		return (long) sqlSessionMeta.delete("deleteIconData", iconId);

	}

	/**
	 * modify icon information data.
	 * 
	 * @param vo IconVO icon information data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateIconData(IconVO vo) throws SQLException {

		return (long) sqlSessionMeta.update("updateIconData", vo);

	}

	/**
	 * insert new icon group information data.
	 * 
	 * @param vo IconGroupVO icon group information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertIconGroupData(IconGroupVO vo) throws SQLException {

		return (long) sqlSessionMeta.insert("insertIconGroupData", vo);

	}

	/**
	 * get icon group information data.
	 * 
	 * @param groupId String icon group id
	 * @return IconGroupVO selected data
	 * @throws SQLException
	 */
	public IconGroupVO selectIconGroupData(String groupId) throws SQLException {

		IconGroupVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectIconGroupData", groupId);
		} catch (Exception ex) {
			logger.error("error in selectIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * get icon group information list data.
	 * 
	 * @return IconGroupVO list selected list data
	 * @throws SQLException
	 */
	public List<IconGroupVO> selectIconGroupList() throws SQLException {

		List<IconGroupVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectIconGroupList");
		} catch (Exception ex) {
			logger.error("error in selectIconGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * delete icon group data.
	 * 
	 * @param groupId string target icon group id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconGroup(String groupId) throws SQLException {

		return (long) sqlSessionMeta.delete("deleteIconGroup", groupId);

	}

	/**
	 * delete icon group mapper data.
	 * 
	 * @param groupId string target icon group id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconGroupLink(String groupId) throws SQLException {

		return (long) sqlSessionMeta.delete("deleteIconGroupLink", groupId);

	}

	/**
	 * modify icon group data.
	 * 
	 * @param vo IconGroupVO icon group information data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateIconGroup(IconGroupVO iconGroupVO) throws SQLException {

		return (long) sqlSessionMeta.update("updateIconGroup", iconGroupVO);

	}

	/**
	 * insert icon into icon group.
	 * <p>
	 * insert data into mapper table.
	 * 
	 * @param grpId  string target icon group id
	 * @param iconId string target icon id
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertIconInGroup(String grpId, String iconId) throws SQLException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("grpId", grpId);
		map.put("iconId", iconId);
		map.put("regUserId", LoginInfoHelper.getUserId());

		return (long) sqlSessionMeta.insert("insertIconInGroup", map);
	}

	/**
	 * remove icon from icon group mapper.
	 * <p>
	 * if iconId is 'ALL', delete all icon.
	 * 
	 * @param grpId  string target icon group id
	 * @param iconId string target icon id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconInGroup(String grpId, String iconId) throws SQLException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("grpId", grpId);
		map.put("iconId", iconId);

		if (iconId != null && "ALL".equals(iconId)) {
			return (long) sqlSessionMeta.delete("deleteAllIconInGroup", map);
		} else {
			return (long) sqlSessionMeta.delete("deleteIconInGroup", map);
		}
	}

	/**
	 * remove icon from icon group mapper.
	 * <p>
	 * use this, icon delete.
	 * 
	 * @param iconId string target icon id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconInGroupMapper(String iconId) throws SQLException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("iconId", iconId);

		return (long) sqlSessionMeta.delete("deleteIconInGroupMapper", map);
	}
}
