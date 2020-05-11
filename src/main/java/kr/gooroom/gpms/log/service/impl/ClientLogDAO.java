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

package kr.gooroom.gpms.log.service.impl;

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
import kr.gooroom.gpms.log.service.ClientLogVO;

/**
 * data access object class for gpms logging management process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("clientLogDAO")
public class ClientLogDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(ClientLogDAO.class);

	/**
	 * response general log list data.
	 * 
	 * @param param Map parameter
	 * @return ClientLogVO list
	 * 
	 */
	public List<ClientLogVO> selectGeneralLogList(Map<String, Object> param) {
		List<ClientLogVO> re = null;
		try {
			re = sqlSessionMeta.selectList("clientLogDAO.selectGeneralLogList", param);
		} catch (Exception ex) {
			logger.error("error in selectGeneralLogList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response general log list data paged
	 * 
	 * @param param Map parameter
	 * @return ClientLogVO list
	 * 
	 */
	public List<ClientLogVO> selectGeneralLogListPaged(HashMap<String, Object> options) {
		List<ClientLogVO> re = null;
		try {
			re = sqlSessionMeta.selectList("clientLogDAO.selectGeneralLogListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectGeneralLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response total count for general log list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectGeneralLogTotalCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectGeneralLogTotalCount", options);
	}

	/**
	 * response filtered count for general log list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectGeneralLogFilteredCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectGeneralLogFilteredCount", options);
	}

	/**
	 * response security log list data paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientLogVO list
	 * 
	 */
	public List<ClientLogVO> selectSecurityLogListPaged(HashMap<String, Object> options) {
		List<ClientLogVO> re = null;
		try {
			re = sqlSessionMeta.selectList("clientLogDAO.selectSecurityLogListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectSecurityLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response total count for security log list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectSecurityLogTotalCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSecurityLogTotalCount", options);
	}

	/**
	 * response filtered count for security log list data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectSecurityLogFilteredCount(HashMap<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectSecurityLogFilteredCount", options);
	}

}
