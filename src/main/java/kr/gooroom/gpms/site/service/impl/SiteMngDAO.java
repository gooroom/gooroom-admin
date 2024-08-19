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

package kr.gooroom.gpms.site.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.site.service.SiteMngVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * data access object class for site information management process.
 * 
 * @author HNC
 */

@Repository("siteMngDAO")
public class SiteMngDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(SiteMngDAO.class);

	/**
	 * response site information list data
	 * 
	 * @return SiteMngVO List
	 */
	public List<SiteMngVO> selectSiteMngList() {
		List<SiteMngVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectSiteMngList", "");
		} catch (Exception ex) {
			logger.error("error in selectSiteMngList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response site information list data for paging
	 * 
	 * @return SiteMngVO List
	 */
	public List<SiteMngVO> selectSiteMngListPaged(HashMap<String, Object> options) {
		List<SiteMngVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectSiteMngListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectSiteMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response filtered count for site info list data by options.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectSiteMngListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectSiteMngListFilteredCount", options);
	}

	/**
	 * response total count for site info list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectSiteMngListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectSiteMngListTotalCount", options);
	}

	/**
	 * create new site information data
	 * 
	 * @param vo SiteMngVO
	 * @return long data insert result count.
	 */
	public long createSiteMng(SiteMngVO vo) {
		return sqlSessionMeta.insert("insertSiteMng", vo);
	}

	/**
	 * response site information by site id
	 * 
	 * @return SiteMngVO List
	 */
	public SiteMngVO selectSiteMngData() {
		SiteMngVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectSiteMngData");
		} catch (Exception ex) {
			logger.error("error in selectSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * modify site information data
	 * 
	 * @param vo SiteMngVO site info data bean
	 * @return long data update result count.
	 */
	public long updateSiteMngData(SiteMngVO vo) {
		return sqlSessionMeta.update("updateSiteMngData", vo);
	}

	/**
	 * modify site's status data
	 * 
	 * @param vo SiteMngVO site info data bean
	 * @return long data update result count.
	 */
	public long updateSiteStatusData(SiteMngVO vo) {
		return sqlSessionMeta.update("updateSiteStatusData", vo);
	}

	/**
	 * delete site information data
	 * 
	 * @param vo SiteMngVO site info data bean
	 * @return long data delete result count.
	 */
	public long deleteSiteMngData(SiteMngVO vo) {
		return sqlSessionMeta.delete("deleteSiteMngData", vo);
	}

}
