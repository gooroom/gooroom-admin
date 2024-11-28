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
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.DesktopItemVO;
import kr.gooroom.gpms.mng.service.DesktopAppVO;

/**
 * data access object class for desktop application management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("desktopAppDAO")
public class DesktopAppDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(DesktopAppDAO.class);

	/**
	 * get desktop application list data
	 * 
	 * @return DesktopItemVO List selected list data
	 */
	public List<DesktopAppVO> selectNormalAppInfoList() {
		List<DesktopAppVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNormalAppInfoList", "");
		} catch (Exception ex) {
			logger.error("error in selectNormalAppInfoList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * get desktop mount list data
	 * 
	 * @return DesktopAppVO List selected list data
	 */
	public List<DesktopItemVO> selectDesktopMounts(String keyword, String status) {
		List<DesktopItemVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("keyword", keyword);
			map.put("status", status);
			re = sqlSessionMeta.selectList("selectDesktopAppList", map);
		} catch (Exception ex) {
			logger.error("error in selectDesktopAppList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * get desktop application list data for paging.
	 * 
	 * @return DesktopAppVO List selected list data
	 */
	public List<DesktopAppVO> selectDesktopAppListPaged(HashMap<String, Object> options) {
		List<DesktopAppVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectDesktopAppListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectDesktopAppListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}

	/**
	 * response filtered count for desktop application list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectDesktopAppListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectDesktopAppListFilteredCount", options);
	}

	/**
	 * response total count for desktop application list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectDesktopAppListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectDesktopAppListTotalCount", options);
	}

	/**
	 * get desktop application information data
	 * 
	 * @param desktopAppId string desktop application id.
	 * @return DesktopAppVO selected data.
	 */
	public DesktopAppVO selectDesktopAppData(String desktopAppId) {

		DesktopAppVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectDesktopAppData", desktopAppId);
		} catch (Exception ex) {
			logger.error("error in selectDesktopAppData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * insert new desktop application data.
	 * 
	 * @param vo DesktopAppVO desktop application configuration data bean
	 * @return long data insert result count.
	 */
	public long createDesktopAppData(DesktopAppVO vo) {
		return sqlSessionMeta.insert("insertDesktopAppData", vo);
	}

	/**
	 * insert cloned desktop application data.
	 * 
	 * @param vo DesktopAppVO desktop application configuration data bean
	 * @return long data insert result count.
	 */
	public long cloneDesktopAppData(DesktopAppVO vo) {
		return sqlSessionMeta.insert("insertClonedDesktopAppData", vo);
	}

	/**
	 * delete desktop application data.
	 * 
	 * @param vo DesktopAppVO desktop application configuration data bean
	 * @return long data delete result count.
	 */
	public long deleteDesktopApp(DesktopAppVO vo) {
		return sqlSessionMeta.delete("deleteDesktopAppData", vo);
	}

	/**
	 * update desktop application data.
	 * 
	 * @param vo DesktopAppVO desktop application configuration data bean
	 * @return long data upadte result count.
	 */
	public long updateDesktopAppData(DesktopAppVO vo) {
		return sqlSessionMeta.update("updateDesktopAppData", vo);
	}
}
