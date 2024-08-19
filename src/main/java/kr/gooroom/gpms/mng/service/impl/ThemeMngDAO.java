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

import kr.gooroom.gpms.common.service.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.ThemeVO;

/**
 * data access object class for theme management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("themeMngDAO")
public class ThemeMngDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ThemeMngDAO.class);

	/**
	 * get theme information list data
	 * 
	 * @return ThemeVO List selected list data
	 */
	public List<ThemeVO> selectThemeList() {

		List<ThemeVO> re = null;
		try {
			HashMap<String, Object> options = new HashMap<>();
			options.put("ICON_ADDRESS", CommonUtils.createIconUrlPath());

			re = sqlSessionMeta.selectList("selectThemeList", options);
		} catch (Exception ex) {
			logger.error("error in selectThemeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get theme information list data for paging
	 * 
	 * @param options HashMap
	 * @return ThemeVO List selected list data
	 */
	public List<ThemeVO> selectThemeListPaged(HashMap<String, Object> options) {
		List<ThemeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectThemeListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectThemeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response filtered count for theme information list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectThemeListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectThemeListFilteredCount", options);
	}

	/**
	 * response total count for theme information list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectThemeListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectThemeListTotalCount", options);
	}

	/**
	 * get theme information list data that not include any theme group.
	 * 
	 * @return ThemeVO List selected list data
	 */
	public List<ThemeVO> selectNoGroupThemeList() {

		List<ThemeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoGroupThemeList");
		} catch (Exception ex) {
			logger.error("error in selectNoGroupThemeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get theme information data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ThemeVO selected data
	 */
	public ThemeVO selectThemeData(HashMap<String, Object> options) {

		ThemeVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectThemeData", options);

		} catch (Exception ex) {
			logger.error("error in selectThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get theme information data.
	 *
	 * @param themeId String themeId
	 * @return ThemeVO selected data
	 * @throws SQLException
	 */
	public ThemeVO selectThemeData(String themeId) throws SQLException {

		ThemeVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectThemeData", themeId);

		} catch (Exception ex) {
			logger.error("error in selectThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * insert new theme information data.
	 * 
	 * @param vo ThemeVO theme information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertThemeData(ThemeVO vo) throws SQLException {
		return sqlSessionMeta.insert("insertThemeData", vo);
	}

	/**
	 * insert theme icon(file) information data.
	 * 
	 * @param param Map<String, String> theme icon information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertThemeIconData(HashMap<String, String> param) throws SQLException {
		return sqlSessionMeta.insert("insertThemeIconData", param);
	}

	/**
	 * delete theme information data.
	 * 
	 * @param themeId string target theme id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteThemeData(String themeId) throws SQLException {
		return sqlSessionMeta.delete("deleteThemeData", themeId);
	}

	/**
	 * delete theme icons information data.
	 * 
	 * @param themeId string target theme id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteThemeIconData(String themeId) throws SQLException {
		return sqlSessionMeta.delete("deleteThemeIconData", themeId);
	}

	/**
	 * modify theme information data.
	 * 
	 * @param vo ThemeVO theme information data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateThemeData(ThemeVO vo) throws SQLException {
		return sqlSessionMeta.update("updateThemeData", vo);
	}

	/**
	 * modify file no sequence
	 *
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateFileSeqToReset() throws SQLException {
		return sqlSessionMeta.update("updateFileSeqToReset");
	}

	/**
	 * update wallpaper & icon file information data.
	 *
	 * @param vo FileVO File information object
	 * @return long query result count
	 * @throws SQLException
	 */
	public long updateThemeFileInfo(FileVO vo) throws SQLException {
		return sqlSessionMeta.update("updateThemeFileInfo", vo);
	}

	/**
	 * insert wallpaper file information data.
	 *
	 * @param vo FileVO File information object
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertWallpaperFileInfo(FileVO vo) throws SQLException {
		return sqlSessionMeta.insert("insertWallpaperFileInfo", vo);
	}

	/**
	 * insert file information data.
	 *
	 * @param vo FileVO File information object
	 * @return long query result count
	 * @throws SQLException
	 */
	public long insertIconFileInfo(FileVO vo) throws SQLException {
		return sqlSessionMeta.insert("insertIconFileInfo", vo);
	}

	/**
	 * delete file information data.
	 *
	 * @param themeId String - delete target themeId
	 * @return long query result count
	 * @throws SQLException
	 */
	public long deleteThemeFileInfo(String themeId) throws SQLException {
		return sqlSessionMeta.delete("deleteThemeFileInfo", themeId);
	}
}
