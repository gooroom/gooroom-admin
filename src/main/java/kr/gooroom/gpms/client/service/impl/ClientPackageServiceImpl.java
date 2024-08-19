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

package kr.gooroom.gpms.client.service.impl;

import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.client.service.ClientPackageService;
import kr.gooroom.gpms.client.service.ClientPackageVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;

/**
 * Client management service implement class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("clientPackageService")
public class ClientPackageServiceImpl implements ClientPackageService {

	private static final Logger logger = LoggerFactory.getLogger(ClientPackageServiceImpl.class);

	@Resource(name = "clientPackageDAO")
	private ClientPackageDAO clientPackageDAO;

	/**
	 * generate total package list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 */
	@Override
	public ResultPagingVO readTotalPackageListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientPackageVO> re = clientPackageDAO.selectTotalPackageListPaged(options);
			long packageCount = clientPackageDAO.selectTotalPackageListTotalCount(options);
			long packageFiltered = clientPackageDAO.selectTotalPackageListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientPackageVO[] row = re.toArray(ClientPackageVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(packageCount));
				resultVO.setRecordsFiltered(String.valueOf(packageFiltered));

			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readTotalPackageListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate package list data in client
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 */
	@Override
	public ResultPagingVO readPackageListPagedInClient(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientPackageVO> re = clientPackageDAO.selectClientPackageListPaged(options);
			long packageCount = clientPackageDAO.selectClientPackageListTotalCount(options);
			long packageFiltered = clientPackageDAO.selectClientPackageListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ClientPackageVO[] row = re.toArray(ClientPackageVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(packageCount));
				resultVO.setRecordsFiltered(String.valueOf(packageFiltered));

			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readPackageListInClient : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate total package list data
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO bean for package data result
	 */
	@Override
	public ResultPagingVO readTotalPackageList(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<ClientPackageVO> re = clientPackageDAO.selectTotalPackageList(options);
			long packageCount = clientPackageDAO.selectTotalPackageCount(options);
			long packageFiltered = clientPackageDAO.selectTotalPackageFiltered(options);

			if (re != null && re.size() > 0) {
				ClientPackageVO[] row = re.toArray(ClientPackageVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(packageCount));
				resultVO.setRecordsFiltered(String.valueOf(packageFiltered));

			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readTotalPackageList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}
}
