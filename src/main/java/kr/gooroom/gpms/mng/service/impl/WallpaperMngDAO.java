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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.WallpaperVO;

/**
 * data access object class for wallpaper management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("wallpaperMngDAO")
public class WallpaperMngDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(WallpaperMngDAO.class);

	/**
	 * get wallpaper information list data
	 * 
	 * @return WallpaperVO List selected list data
	 */
	public List<WallpaperVO> selectWallpaperList() {

		List<WallpaperVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectWallpaperList");
		} catch (Exception ex) {
			logger.error("error in selectWallpaperList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get wallpaper information list data that not include any wallpaper group.
	 * 
	 * @return WallpaperVO List selected list data
	 */
	public List<WallpaperVO> selectNoGroupWallpaperList() {

		List<WallpaperVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoGroupWallpaperList");
		} catch (Exception ex) {
			logger.error("error in selectNoGroupWallpaperList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * get wallpaper information data.
	 * 
	 * @param wallpaperId String wallpaper id
	 * @return WallpaperVO selected data
	 */
	public WallpaperVO selectWallpaperData(String wallpaperId) {

		WallpaperVO re = null;
		try {

			re = sqlSessionMeta.selectOne("selectWallpaperData", wallpaperId);

		} catch (Exception ex) {
			logger.error("error in selectWallpaperData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * insert new wallpaper information data.
	 * 
	 * @param vo WallpaperVO wallpaper information data bean
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long insertWallpaperData(WallpaperVO vo) throws SQLException {
		return sqlSessionMeta.insert("insertWallpaperData", vo);
	}

	/**
	 * delete wallpaper information data.
	 * 
	 * @param wallpaperId string target wallpaper id
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteWallpaperData(String wallpaperId) throws SQLException {
		return sqlSessionMeta.delete("deleteWallpaperData", wallpaperId);
	}

	/**
	 * modify wallpaper information data.
	 * 
	 * @param vo WallpaperVO wallpaper information data bean
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateWallpaperData(WallpaperVO vo) throws SQLException {
		return sqlSessionMeta.update("updateWallpaperData", vo);
	}

}
