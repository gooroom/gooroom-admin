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
	 * @param gscpId string target csp id.
	 * @return boolean true if csp id was duplicate.
	 * @throws SQLException
	 */
	public boolean isExistGcspId(String gcspId) throws SQLException {

		boolean re = true;
		try {
			re = ((Boolean) sqlSessionMeta.selectOne("isExistGcspId", gcspId)).booleanValue();

		} catch (Exception ex) {
			re = true;
		}

		return re;
	}

	/**
	 * generate gooroom csp list data.
	 * 
	 * @param statusCd   string status value.
	 * @param search_key string search keyword.
	 * @return CspVO list
	 * @throws SQLException
	 */
	public List<CspVO> selectGcspDataList(String gcsp_status, String search_key) throws SQLException {
		List<CspVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("gcspStatus", gcsp_status);
			map.put("searchKey", search_key);
			re = sqlSessionMeta.selectList("selectGcspDataList", map);
		} catch (Exception ex) {
			logger.error("CspDAO.selectGcspDataList error occured : ", ex);
			re = null;
		}
		return re;
	}

	/**
	 * generate gooroom csp list data paging.
	 * 
	 * @param options HashMap.
	 * @return CspVO list
	 * @throws SQLException
	 */
	public List<CspVO> selectGcspListPaged(HashMap<String, Object> options) throws SQLException {
		List<CspVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectGcspListPaged", options);
		} catch (Exception ex) {
			logger.error("CspDAO.selectGcspListPaged error occured : ", ex);
			re = null;
		}
		return re;
	}

	/**
	 * generate gooroom csp list data total count
	 * 
	 * @param options HashMap.
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectGcspListTotalCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectGcspListTotalCount", options);
	}

	/**
	 * generate gooroom csp list data filtered count.
	 * 
	 * @param options HashMap.
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectGcspListFilteredCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectGcspListFilteredCount", options);
	}

	/**
	 * generate gooroom csp data by csp id.
	 * 
	 * @param gcspId string csp id.
	 * @return CspVO csp configuration data bean
	 * @throws SQLException
	 */
	public CspVO selectGcspData(String gcspId) throws SQLException {

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
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createGcspData(CspVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("insertGcspData", vo);
	}

	/**
	 * modify gooroom csp information
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long editGcspData(CspVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("updateGcspData", vo);
	}

	/**
	 * save certificate information.
	 * 
	 * @param cspVO CspVO csp cofiguration data bean.
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long saveGcspCert(CspVO vo) throws SQLException {
		return (long) sqlSessionMeta.insert("updateGcspCert", vo);
	}

	/**
	 * delete gooroom csp information data
	 * 
	 * @param vo AdminUserVO administrator user data bean
	 * @return long data delete result count.
	 * @throws SQLException
	 */
	public long deleteGcspData(CspVO vo) throws SQLException {
		return (long) sqlSessionMeta.delete("deleteGcspData", vo);
	}

}
