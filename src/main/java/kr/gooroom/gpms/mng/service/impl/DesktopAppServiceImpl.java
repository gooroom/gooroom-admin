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

package kr.gooroom.gpms.mng.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import kr.gooroom.gpms.mng.service.DesktopAppService;
import kr.gooroom.gpms.mng.service.DesktopAppVO;

/**
 * Desktop application management service implemts class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("desktopAppService")
public class DesktopAppServiceImpl implements DesktopAppService {

	private static final Logger logger = LoggerFactory.getLogger(DesktopAppServiceImpl.class);

	@Resource(name = "desktopAppDAO")
	private DesktopAppDAO desktopAppDAO;

	/**
	 * create new desktop application data
	 * 
	 * @param desktopAppVO DesktopAppVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO createDesktopApp(DesktopAppVO desktopAppVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			desktopAppVO.setRegUserId(LoginInfoHelper.getUserId());
			desktopAppVO.setModUserId(LoginInfoHelper.getUserId());
			long resultCnt = desktopAppDAO.createDesktopAppData(desktopAppVO);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("desktopapp.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("desktopapp.result.noinsert"));
			}
		} catch (Exception ex) {
			logger.error("error in createDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create cloned desktop application data
	 * 
	 * @param appId String app id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO cloneDesktopApp(String appId) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			DesktopAppVO desktopAppVO = new DesktopAppVO();
			desktopAppVO.setAppId(appId);
			desktopAppVO.setRegUserId(LoginInfoHelper.getUserId());
			desktopAppVO.setModUserId(LoginInfoHelper.getUserId());
			long resultCnt = desktopAppDAO.cloneDesktopAppData(desktopAppVO);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("desktopapp.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("desktopapp.result.noinsert"));
			}
		} catch (Exception ex) {
			logger.error("error in cloneDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete desktop application data
	 * 
	 * @param desktopAppId String desktop application id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO deleteDesktopApp(String desktopAppId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			DesktopAppVO desktopAppVO = new DesktopAppVO();
			desktopAppVO.setAppId(desktopAppId);
			desktopAppVO.setStatusCd(GPMSConstants.STS_SERVICE_STOP);
			desktopAppVO.setModUserId(LoginInfoHelper.getUserId());

			long re = desktopAppDAO.deleteDesktopApp(desktopAppVO);

			if (re > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("desktopapp.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("desktopapp.result.nodelete"));
			}

		} catch (Exception ex) {

			logger.error("error in deleteDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * modify desktop application data
	 * 
	 * @param desktopAppVO DesktopAppVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO editDesktopApp(DesktopAppVO desktopAppVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			desktopAppVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = desktopAppDAO.updateDesktopAppData(desktopAppVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("desktopapp.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("desktopapp.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in editDesktopApp : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * get desktop application list data
	 * 
	 * @param contextAddress String use desktop application.
	 * @return ResultVO
	 * @throws Exception
	 */
	@Override
	public ResultVO getDesktopAppList() throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<DesktopAppVO> re = desktopAppDAO.selectNormalAppInfoList();
			if (re != null && re.size() > 0) {
				DesktopAppVO[] row = re.stream().toArray(DesktopAppVO[]::new);
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
			logger.error("error in getDesktopAppList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop application list data for paging.
	 * 
	 * @param contextAddress String use desktop application.
	 * @return ResultVO
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getDesktopAppListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();
		try {

			List<DesktopAppVO> re = desktopAppDAO.selectDesktopAppListPaged(options);
			long totalCount = desktopAppDAO.selectDesktopAppListTotalCount(options);
			long filteredCount = desktopAppDAO.selectDesktopAppListFilteredCount(options);

			String contextAddress = (String) options.get("GR_CONTEXT");

			if (re != null && re.size() > 0) {

				List<DesktopAppVO> result = re.stream().map(temp -> {
					DesktopAppVO obj = temp;
					if (GPMSConstants.STS_SERVICE_RUN.equals(temp.getStatusCd())) {
						obj.setStatus(MessageSourceHelper.getMessage("common.label.use"));
					} else if (GPMSConstants.STS_SERVICE_STOP.equals(temp.getStatusCd())) {
						obj.setStatus(MessageSourceHelper.getMessage("common.label.unuse"));
					}

					if ("library".equalsIgnoreCase(temp.getIconGubun())) {
						obj.setIconUrl(contextAddress + GPMSConstants.PATH_FOR_ICONURL + "/" + temp.getFileNm());
					}

					return obj;
				}).collect(Collectors.toList());

				DesktopAppVO[] row = result.stream().toArray(DesktopAppVO[]::new);
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
			logger.error("error in getDesktopAppListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * get desktop application information data by id
	 * 
	 * @param desktopAppId String desktop application id.
	 * @return ResultVO
	 * @throws Exception
	 */
	@Override
	public ResultVO getDesktopAppData(String desktopAppId) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			DesktopAppVO re = desktopAppDAO.selectDesktopAppData(desktopAppId);

			if (re != null) {

				DesktopAppVO[] row = new DesktopAppVO[1];
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

			logger.error("error in getDesktopAppData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
