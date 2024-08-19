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
	 */
	public List<IconVO> selectIconList(String grpId) {

		List<IconVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("grpId", grpId);

			re = sqlSessionMeta.selectList("selectIconList", map);

		} catch (Exception ex) {
			logger.error("error in selectIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get icon information list data that not include any icon group.
	 * 
	 * @return IconVO List selected list data
	 */
	public List<IconVO> selectNoGroupIconList() {

		List<IconVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoGroupIconList");
		} catch (Exception ex) {
			logger.error("error in selectNoGroupIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get icon information data.
	 * 
	 * @param iconId String icon id
	 * @return IconVO selected data
	 */
	public IconVO selectIconData(String iconId) {

		IconVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectIconData", iconId);

		} catch (Exception ex) {
			logger.error("error in selectIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
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
		return sqlSessionMeta.insert("insertIconData", vo);
	}

	/**
	 * delete icon information data.
	 * 
	 * @param iconId string target icon id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconData(String iconId) throws SQLException {
		return sqlSessionMeta.delete("deleteIconData", iconId);
	}

	/**
	 * modify icon information data.
	 * 
	 * @param vo IconVO icon information data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateIconData(IconVO vo) throws SQLException {
		return sqlSessionMeta.update("updateIconData", vo);
	}

	/**
	 * insert new icon group information data.
	 * 
	 * @param vo IconGroupVO icon group information data bean
	 * @return long data insert result count.
	 */
	public long insertIconGroupData(IconGroupVO vo) {
		return sqlSessionMeta.insert("insertIconGroupData", vo);
	}

	/**
	 * get icon group information data.
	 * 
	 * @param groupId String icon group id
	 * @return IconGroupVO selected data
	 */
	public IconGroupVO selectIconGroupData(String groupId) {

		IconGroupVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectIconGroupData", groupId);
		} catch (Exception ex) {
			logger.error("error in selectIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get icon group information list data.
	 * 
	 * @return IconGroupVO list selected list data
	 */
	public List<IconGroupVO> selectIconGroupList() {

		List<IconGroupVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectIconGroupList");
		} catch (Exception ex) {
			logger.error("error in selectIconGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
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
		return sqlSessionMeta.delete("deleteIconGroup", groupId);
	}

	/**
	 * delete icon group mapper data.
	 * 
	 * @param groupId string target icon group id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteIconGroupLink(String groupId) throws SQLException {
		return sqlSessionMeta.delete("deleteIconGroupLink", groupId);
	}

	/**
	 * modify icon group data.
	 * 
	 * @param iconGroupVO IconGroupVO icon group information data bean
	 * @return long data update result count.
	 */
	public long updateIconGroup(IconGroupVO iconGroupVO) {
		return sqlSessionMeta.update("updateIconGroup", iconGroupVO);
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

		HashMap<String, Object> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("iconId", iconId);
		map.put("regUserId", LoginInfoHelper.getUserId());

		return sqlSessionMeta.insert("insertIconInGroup", map);
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

		HashMap<String, Object> map = new HashMap<>();
		map.put("grpId", grpId);
		map.put("iconId", iconId);

		if (iconId.equals("ALL")) {
			return sqlSessionMeta.delete("deleteAllIconInGroup", map);
		} else {
			return sqlSessionMeta.delete("deleteIconInGroup", map);
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

		HashMap<String, Object> map = new HashMap<>();
		map.put("iconId", iconId);

		return sqlSessionMeta.delete("deleteIconInGroupMapper", map);
	}
}
