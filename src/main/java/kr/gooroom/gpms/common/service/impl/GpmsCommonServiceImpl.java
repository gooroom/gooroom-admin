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

package kr.gooroom.gpms.common.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.GpmsCommonService;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.ServerAddrInfoVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;

/**
 * Interface for common service
 * <p>
 * get server information, insert administrator log, manage connection ip. etc.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("gpmsCommonService")
public class GpmsCommonServiceImpl implements GpmsCommonService {

	private static final Logger logger = LoggerFactory.getLogger(GpmsCommonServiceImpl.class);

	@Resource(name = "gpmsCommonDAO")
	private GpmsCommonDAO gpmsCommonDAO;

	/**
	 * create(insert) administrator user action task in logging table.
	 * 
	 * @param actType  string action type
	 * @param actItem  string action item
	 * @param actData  string action date like parameter
	 * @param accessIp string network access ip information
	 * @param userId   string user id
	 * @return StatusVO result status data
	 * @throws Exception
	 */
	@Override
	public StatusVO createUserActLogHistory(String actType, String actItem, String actData, String accessIp,
			String userId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long reCnt = gpmsCommonDAO.createUserActLogHistory(actType, actItem, actData, accessIp, userId);

			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata"));
			}
		} catch (Exception ex) {
			logger.error("error in createUserActLogHistory : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response servers network information.
	 * <p>
	 * GPMS, GLM, GKM, GRM.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getGpmsServersInfo() throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			ServerAddrInfoVO re = gpmsCommonDAO.getGpmsServersInfo();

			if (re != null) {
				ServerAddrInfoVO[] row = new ServerAddrInfoVO[1];
				row[0] = re;
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
			logger.error("error in getGpmsServersInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response available network ip information.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getGpmsAvailableNetwork() throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<String> re = gpmsCommonDAO.selectGpmsAvailableNetwork();

			if (re != null && re.size() > 0) {
				String[] row = re.stream().toArray(String[]::new);
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
			logger.error("error in getGpmsAvailableNetwork : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response available network ip information by admin id
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getGpmsAvailableNetworkByAdminId(String adminId) throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<String> re = gpmsCommonDAO.selectGpmsAvailableNetworkByAdminId(adminId);
			if (re != null && re.size() > 0) {
				String[] row = re.stream().toArray(String[]::new);
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
			logger.error("error in getGpmsAvailableNetworkByAdminId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

}
