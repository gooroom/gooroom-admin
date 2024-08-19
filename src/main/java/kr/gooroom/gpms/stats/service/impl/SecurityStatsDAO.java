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

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.ViolatedCountVO;
import kr.gooroom.gpms.stats.service.ViolatedLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * data access object class for protector attacked statistic process.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Repository("securityStatsDAO")
public class SecurityStatsDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(SecurityStatsDAO.class);

	/**
	 * generate daily count data
	 * 
	 * @param map HashMap<Sring,String>
	 * @param countType string
	 * @return ViolatedCountVO List
	 */
	public List<ViolatedCountVO> selectViolatedCount(HashMap<String, String> map, String countType) {

		List<ViolatedCountVO> re = null;
		try {
			if ("day".equalsIgnoreCase(countType)) {
				re = sqlSessionMeta.selectList("selectViolatedDailyCount", map);
			} else if ("week".equalsIgnoreCase(countType)) {
				re = sqlSessionMeta.selectList("selectViolatedWeeklyCount", map);
			} else if ("month".equalsIgnoreCase(countType)) {
				re = sqlSessionMeta.selectList("selectViolatedMonthlyCount", map);
			}
		} catch (Exception ex) {
			logger.error("error in selectViolatedCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * generate protector attacked data list by specified date.
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date.
	 * @return ViolatedLogVO List
	 */
	public List<ViolatedLogVO> selectViolatedList(String searchType, String searchDate) {

		List<ViolatedLogVO> re = null;

		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("searchType", searchType.toLowerCase());
			map.put("searchDate", searchDate);
			map.put("defaultViolatedLogType", GPMSConstants.DEFAULT_VIOLATED_LOGTYPE);

			re = sqlSessionMeta.selectList("selectViolatedList", map);

		} catch (Exception ex) {
			logger.error("error in selectViolatedList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * generate protector attacked data list by specified date and paged
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return ViolatedLogVO List
	 */
	public List<ViolatedLogVO> selectViolatedListPaged(HashMap<String, Object> options) {
		List<ViolatedLogVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectViolatedListPaged", options);
		} catch (Exception ex) {
			logger.error("error in selectViolatedListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}

	/**
	 * response total count for protector attacked data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectViolatedTotalCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectViolatedTotalCount", options);
	}

	/**
	 * response filtered count for protector attacked data.
	 * 
	 * @param options HashMap<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectViolatedFilteredCount(HashMap<String, Object> options) {
		return sqlSessionMeta.selectOne("selectViolatedFilteredCount", options);
	}

}
