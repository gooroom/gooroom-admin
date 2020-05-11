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

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.SecurityStatsService;
import kr.gooroom.gpms.stats.service.ViolatedCountVO;
import kr.gooroom.gpms.stats.service.ViolatedLogVO;

/**
 * gooroom security statistics data management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("securityStatsService")
public class SecurityStatsServiceImpl implements SecurityStatsService {

	private static final Logger logger = LoggerFactory.getLogger(SecurityStatsServiceImpl.class);

	@Resource(name = "securityStatsDAO")
	private SecurityStatsDAO securityStatsDAO;

	/**
	 * generate daily count data
	 * 
	 * @param fromDate String start date
	 * @param toDate   String end date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getViolatedCount(String fromDate, String toDate, String countType) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);
			map.put("defaultViolatedLogType", GPMSConstants.DEFAULT_VIOLATED_LOGTYPE);
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			}

			List<ViolatedCountVO> re = securityStatsDAO.selectViolatedCount(map, countType);

			if (re != null && re.size() > 0) {
				ViolatedCountVO[] row = re.stream().toArray(ViolatedCountVO[]::new);
//		if (row != null && row.length > 0) {
//		    for (int i = 0; i < row.length; i++) {
//			row[i].setMediaProtectorCount((row[i].getMediaProtectorCount() == 0) ? -1 : row[i].getMediaProtectorCount());
//		    }
//		}

				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getViolatedCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate protector attacked data list by specified date.
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date.
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getViolatedList(String searchType, String searchDate) throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<ViolatedLogVO> re = securityStatsDAO.selectViolatedList(searchType, searchDate);
			if (re != null && re.size() > 0) {
				ViolatedLogVO[] row = re.stream().toArray(ViolatedLogVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getViolatedList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * generate protector attacked data list by specified date and paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getViolatedListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}
			
			List<ViolatedLogVO> re = securityStatsDAO.selectViolatedListPaged(options);
			long totalCount = securityStatsDAO.selectViolatedTotalCount(options);
			long filteredCount = securityStatsDAO.selectViolatedFilteredCount(options);

			if (re != null && re.size() > 0) {
				ViolatedLogVO[] row = re.stream().toArray(ViolatedLogVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getViolatedListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
