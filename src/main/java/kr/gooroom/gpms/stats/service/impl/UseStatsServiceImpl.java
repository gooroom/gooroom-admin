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
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.client.service.ClientStatsVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.stats.service.ClientMngCountVO;
import kr.gooroom.gpms.stats.service.LoginCountVO;
import kr.gooroom.gpms.stats.service.LoginDataVO;
import kr.gooroom.gpms.stats.service.UseStatsService;

/**
 * Amount of used gooroom service statistics data management service implements
 * class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("useStatsService")
public class UseStatsServiceImpl implements UseStatsService {

	private static final Logger logger = LoggerFactory.getLogger(UseStatsServiceImpl.class);

	@Resource(name = "useStatsDAO")
	private UseStatsDAO useStatsDAO;

	/**
	 * generate daily login action count data
	 * 
	 * @param fromDate String start date
	 * @param toDate   String end date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getLoginDailyCount(String fromDate, String toDate) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			}

			List<LoginCountVO> re = useStatsDAO.selectLoginDailyCount(map);

			if (re != null && re.size() > 0) {

				LoginCountVO[] row = re.stream().toArray(LoginCountVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
				resultVO.setExtend(new String[] { fromDate, toDate });

			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
				resultVO.setExtend(new String[] { fromDate, toDate });
			}

		} catch (Exception ex) {
			logger.error("error in getLoginDailyCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate login action count by type and specified data
	 * 
	 * @param searchType String search type
	 * @param searchDate String search date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getLoginList(String searchType, String searchDate) throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<LoginDataVO> re = useStatsDAO.selectLoginList(searchType, searchDate);
			if (re != null && re.size() > 0) {
				LoginDataVO[] row = re.stream().toArray(LoginDataVO[]::new);
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
			logger.error("error in getLoginList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * generate login action count by type and specified data and paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getLoginListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}
			
			List<LoginDataVO> re = useStatsDAO.selectLoginListPaged(options);
			long totalCount = useStatsDAO.selectLoginTotalCount(options);
			long filteredCount = useStatsDAO.selectLoginFilteredCount(options);
			if (re != null && re.size() > 0) {
				LoginDataVO[] row = re.stream().toArray(LoginDataVO[]::new);
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
			logger.error("error in getLoginListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * generate daily client create and revoke action count data
	 * 
	 * @param fromDate String start date
	 * @param toDate   String end date
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getClientMngCount(String fromDate, String toDate) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("fromDate", fromDate);
			map.put("toDate", toDate);
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			}

			List<ClientMngCountVO> re = useStatsDAO.selectClientMngCount(map);

			if (re != null && re.size() > 0) {

				ClientMngCountVO[] row = re.stream().toArray(ClientMngCountVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
				resultVO.setExtend(new String[] { fromDate, toDate });

			} else {

				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
				resultVO.setExtend(new String[] { fromDate, toDate });
			}

		} catch (Exception ex) {
			logger.error("error in getClientMngCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate client list data paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getClientMngListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if(GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}
			
			List<ClientStatsVO> re = useStatsDAO.selectClientMngListPaged(options);
			long totalCount = useStatsDAO.selectClientMngTotalCount(options);
			long filteredCount = useStatsDAO.selectClientMngFilteredCount(options);

			if (re != null && re.size() > 0) {
				
				re.stream().map(temp -> {
					ClientStatsVO obj = temp;
					obj.setViewStatus(CommonUtils.getViewStatusForClient(temp.getClientStatus(), "", "0"));
					return obj;
				}).collect(Collectors.toList());
				
				ClientStatsVO[] row = re.stream().toArray(ClientStatsVO[]::new);
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
			logger.error("error in getClientMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
