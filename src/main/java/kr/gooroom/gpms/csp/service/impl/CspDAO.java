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

package kr.gooroom.gpms.csp.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.csp.service.CspVO;

/**
 * data access object class for gooroom csp management process.
 * <p>
 * csp : cloud service provider.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("cspDAO")
public class CspDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(CspDAO.class);

	/**
	 * response boolean for csp id was duplicated.
	 * 
	 * @param gcspId string target csp id.
	 * @return boolean true if csp id was duplicate.
	 */
	public boolean isExistGcspId(String gcspId) {

		boolean re = true;
		try {
			re = sqlSessionMeta.selectOne("isExistGcspId", gcspId);
		} catch (Exception ex) {
			re = true;
		}

		return re;
	}

	/**
	 * generate gooroom csp list data.
	 * 
	 * @param gcsp_status string status value.
	 * @param search_key string search keyword.
	 * @return CspVO list
	 */
	public List<CspVO> selectGcspDataList(String gcsp_status, String search_key) {

		List<CspVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("gcspStatus", gcsp_status);
			map.put("searchKey", search_key);
			re = sqlSessionMeta.selectList("selectGcspDataList", map);
		} catch (Exception ex) {
			logger.error("CspDAO.selectGcspDataList error occured : ", ex);
		}
		return re;
	}

	/**
	 * generate gooroom csp list data paging.
	 * 
	 * @param options HashMap.
	 * @return CspVO list
	 */
	public List<CspVO> selectGcspListPaged(HashMap<String, Object> options) {

		List<CspVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectGcspListPaged", options);
		} catch (Exception ex) {
			logger.error("CspDAO.selectGcspListPaged error occured : ", ex);
		}
		return re;
	}

	/**
	 * generate gooroom csp list data total count
	 * 
	 * @param options HashMap.
	 * @return long total count number.
	 */
	public long selectGcspListTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectGcspListTotalCount", options);
	}

	/**
	 * generate gooroom csp list data filtered count.
	 * 
	 * @param options HashMap.
	 * @return long filtered count number.
	 */
	public long selectGcspListFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectGcspListFilteredCount", options);
	}

	/**
	 * generate gooroom csp data by csp id.
	 * 
	 * @param gcspId string csp id.
	 * @return CspVO csp configuration data bean
	 */
	public CspVO selectGcspData(String gcspId) {

		CspVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectGcspData", gcspId);
		} catch (Exception ex) {
			re = null;
		}

		return re;
	}

	/**
	 * create new gooroom csp configuration data.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 */
	public long createGcspData(CspVO vo) {
		return sqlSessionMeta.insert("insertGcspData", vo);
	}

	/**
	 * modify gooroom csp information
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 */
	public long editGcspData(CspVO vo) {
		return sqlSessionMeta.insert("updateGcspData", vo);
	}

	/**
	 * save certificate information.
	 * 
	 * @param vo CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long saveGcspCert(CspVO vo) throws SQLException {
		return sqlSessionMeta.insert("updateGcspCert", vo);
	}

	/**
	 * delete gooroom csp information data
	 * 
	 * @param vo AdminUserVO administrator user data bean
	 * @return long data delete result count.
	 */
	public long deleteGcspData(CspVO vo) {
		return sqlSessionMeta.delete("deleteGcspData", vo);
	}

}
