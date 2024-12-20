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

package kr.gooroom.gpms.stats.service.impl;

import kr.gooroom.gpms.client.service.ClientStatsVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.ClientMngCountVO;
import kr.gooroom.gpms.stats.service.LoginCountVO;
import kr.gooroom.gpms.stats.service.LoginDataVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * data access object class for use gooroom service statistic process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("useStatsDAO")
public class UseStatsDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(UseStatsDAO.class);

	/**
	 * generate daily login action count data
	 * 
	 * @param options HashMap<String,Object>
	 * @return LoginCountVO List
	 */
	public List<LoginCountVO> selectLoginDailyCount(HashMap<String, Object> options) {

		List<LoginCountVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectLoginDailyCount", options);
		} catch (Exception ex) {
			logger.error("error in selectLoginDailyCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * generate login action count by type and specified data
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date
	 * @return LoginDataVO List
	 */
	public List<LoginDataVO> selectLoginList(String searchType, String searchDate) {
		List<LoginDataVO> re = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("searchType", searchType.toUpperCase());
			map.put("searchDate", searchDate);
			re = sqlSessionMeta.selectList("selectLoginList", map);
		} catch (Exception ex) {
			logger.error("error in selectLoginList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * generate login action count by type and specified data and paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return LoginDataVO List
	 */
	public List<LoginDataVO> selectLoginListPaged(HashMap<String, Object> options) {
		List<LoginDataVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectLoginListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectLoginListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response total count for login action count data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectLoginTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectLoginTotalCount", options);
	}

	/**
	 * response filtered count for login action count data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectLoginFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectLoginFilteredCount", options);
	}

	/**
	 * generate daily client create and revoke action count data
	 * 
	 * @param options HashMap<String,Object>
	 * @return ClientMngCountVO List
	 */
	public List<ClientMngCountVO> selectClientMngCount(HashMap<String, Object> options) {

		List<ClientMngCountVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientMngCount", options);
		} catch (Exception ex) {
			logger.error("error in selectClientMngCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * generate client list data paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ClientVO List
	 */
	public List<ClientStatsVO> selectClientMngListPaged(HashMap<String, Object> options) {
		List<ClientStatsVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectClientMngListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectClientMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response total count for login action count data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectClientMngTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientMngTotalCount", options);
	}

	/**
	 * response filtered count for login action count data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectClientMngFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectClientMngFilteredCount", options);
	}

}
