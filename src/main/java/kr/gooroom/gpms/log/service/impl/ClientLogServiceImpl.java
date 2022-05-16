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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import kr.gooroom.gpms.log.service.ClientLogService;
import kr.gooroom.gpms.log.service.ClientLogVO;

/**
 * GPMS logging management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientLogService")
public class ClientLogServiceImpl implements ClientLogService {

	private static final Logger logger = LoggerFactory.getLogger(ClientLogServiceImpl.class);

	@Resource(name = "clientLogDAO")
	private ClientLogDAO clientLogDAO;

	/**
	 * response general log list data.
	 * 
	 * @param fromDate string from date.
	 * @param toDate   string to date.
	 * @param logItem  string log type for select.
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getGeneralLogList(String fromDate, String toDate, String logItem) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("fromDate", fromDate);
			param.put("toDate", toDate);
			String[] items = null;

			if ("BROWSER".equalsIgnoreCase(logItem)) {
				items = new String[] { "gooroom-browser" };
			} else if ("AGENT".equalsIgnoreCase(logItem)) {
				items = new String[] { "gooroom-agent" };
			} else if ("ALL".equalsIgnoreCase(logItem)) {
				items = new String[] { "gooroom-browser", "gooroom-agent" };
			}

			param.put("logItems", items);

			List<ClientLogVO> re = clientLogDAO.selectGeneralLogList(param);

			if (re != null && re.size() > 0) {

				ClientLogVO[] row = re.stream().toArray(ClientLogVO[]::new);
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
			logger.error("error in getGeneralLogList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response general log list data paged.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getGeneralLogListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientLogVO> re = clientLogDAO.selectGeneralLogListPaged(options);
			long totalCount = clientLogDAO.selectGeneralLogTotalCount(options);
			long filteredCount = clientLogDAO.selectGeneralLogFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientLogVO[] row = re.stream().toArray(ClientLogVO[]::new);
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
			logger.error("error in getGeneralLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get security log list data paged
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getSecurityLogListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}

			List<ClientLogVO> re = clientLogDAO.selectSecurityLogListPaged(options);
			long totalCount = clientLogDAO.selectSecurityLogTotalCount(options);
			long filteredCount = clientLogDAO.selectSecurityLogFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientLogVO[] row = re.stream().toArray(ClientLogVO[]::new);
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
			logger.error("error in getSecurityLogListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * delete user client use hist
	 *
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	public StatusVO deleteUserClientUseHist(HashMap<String, Object> options) throws Exception {
		StatusVO statusVO = new StatusVO();

		try {

			long re = clientLogDAO.deleteUserClientUseHist(options);
			if(re >= 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("common.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.nodelete"));
			}

		} catch (Exception ex) {
			logger.error("error in deleteUserClientUseHist : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}
}
