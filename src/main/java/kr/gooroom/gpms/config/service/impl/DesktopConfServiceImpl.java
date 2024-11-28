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

package kr.gooroom.gpms.config.service.impl;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.DesktopApplicationInfoVO;
import kr.gooroom.gpms.config.service.DesktopConfService;
import kr.gooroom.gpms.config.service.DesktopConfVO;
import kr.gooroom.gpms.config.service.DesktopInfoVO;
import kr.gooroom.gpms.config.service.DesktopItemVO;

/**
 * Desktop configuration management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("desktopConfService")
public class DesktopConfServiceImpl implements DesktopConfService {

	private static final Logger logger = LoggerFactory.getLogger(DesktopConfServiceImpl.class);

	@Resource(name = "desktopConfDAO")
	private DesktopConfDAO desktopConfDao;

	/**
	 * generate desktop configuration list data
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO readDesktopConfList() {

		ResultVO resultVO = new ResultVO();
		try {
			List<DesktopConfVO> re = desktopConfDao.readDesktopConfList();
			if (re != null && re.size() > 0) {
				DesktopConfVO[] row = re.toArray(DesktopConfVO[]::new);
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
			logger.error("error in readDesktopConfList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate desktop configuration list data with paging
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultPagingVO getDesktopConfListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<DesktopConfVO> re = desktopConfDao.selectDesktopConfListPaged(options);
			long totalCount = desktopConfDao.selectDesktopConfListTotalCount(options);
			long filteredCount = desktopConfDao.selectDesktopConfListFilteredCount(options);

			if (re != null && re.size() > 0) {

				DesktopConfVO[] row = re.toArray(DesktopConfVO[]::new);
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
			logger.error("error in getDesktopConfListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * create new desktop configuration data.
	 * 
	 * @param desktopConfNm    String desktop configuration name.
	 * @param desktopTheme String
	 * @param appDatas String
	 * @param adminType String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createDesktopConf(String desktopConfNm, String desktopTheme, String[] appDatas, String adminType) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			DesktopConfVO desktopConfVO = new DesktopConfVO();
			desktopConfVO.setRegUserId(LoginInfoHelper.getUserId());
			desktopConfVO.setModUserId(LoginInfoHelper.getUserId());
			desktopConfVO.setConfNm(desktopConfNm);
			desktopConfVO.setThemeId(desktopTheme);
			desktopConfVO.setMngObjTp(GPMSConstants.CFG_DESKTOP_SETUP);
			desktopConfVO.setMngObjTpAbbr(GPMSConstants.CFG_DESKTOP_SETUP_ABBR);
			if ("S".equalsIgnoreCase(adminType)) {
				desktopConfVO.setStandardConf(true);
			}
			long reCnt = desktopConfDao.createDesktopConf(desktopConfVO);

			if (reCnt > 0) {
				// app data
				for (int i = 0; i < appDatas.length; i++) {
					desktopConfVO.setAppId(appDatas[i]);
					desktopConfVO.setAppOrder(String.valueOf(i + 1));
					desktopConfDao.createDesktopAppInConf(desktopConfVO);
				}
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("desktopconf.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("desktopconf.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * edit desktop configuration data.
	 * 
	 * @param desktopConfId    String desktop configuration id.
	 * @param desktopConfNm    String desktop configuration name.
	 * @param desktopTheme String
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateDesktopConf(String desktopConfId, String desktopConfNm, String desktopTheme,
			String[] appDatas) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			DesktopConfVO desktopConfVO = new DesktopConfVO();
			desktopConfVO.setModUserId(LoginInfoHelper.getUserId());
			desktopConfVO.setThemeId(desktopTheme);
			desktopConfVO.setConfId(desktopConfId);
			desktopConfVO.setConfNm(desktopConfNm);
			long reCnt = desktopConfDao.updateDesktopConf(desktopConfVO);

			if (reCnt > 0) {
				desktopConfDao.deleteDesktopAppsInConf(desktopConfVO.getConfId());
				// app data
				for (int i = 0; i < appDatas.length; i++) {
					desktopConfVO.setAppId(appDatas[i]);
					desktopConfVO.setAppOrder(String.valueOf(i + 1));
					desktopConfDao.createDesktopAppInConf(desktopConfVO);
				}
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("desktopconf.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("desktopconf.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * generate desktop configuration data by desktop configuration id
	 * 
	 * @param desktopConfId string target desktop configuration id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getDesktopConfData(String desktopConfId) {

		ResultVO resultVO = new ResultVO();

		try {

			DesktopConfVO re = desktopConfDao.getDesktopConfData(desktopConfId);

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("confId", desktopConfId);
			DesktopInfoVO desktopInfoVO = desktopConfDao.getDesktopInfoByConfId(paramMap);

			ArrayList<DesktopItemVO> appVOs = desktopInfoVO.getApps();
			StringBuilder iconUrlStr = new StringBuilder();
			iconUrlStr.append(GPMSConstants.ICON_SERVER_PROTOCOL).append("://");
			iconUrlStr.append(GPMSConstants.ICON_SERVERPATH).append("/");
			iconUrlStr.append(GPMSConstants.PATH_FOR_ICONURL).append("/");

			// wallpaper url set
			if (desktopInfoVO.getWallpaperFile() != null && desktopInfoVO.getWallpaperFile().length() > 0) {
				desktopInfoVO.setWallpaperFile(iconUrlStr + desktopInfoVO.getWallpaperFile());
			}

			for (DesktopItemVO app : appVOs) {
				DesktopApplicationInfoVO infoVO = app.getDesktop();
				if (infoVO != null) {
					if ("library".equalsIgnoreCase(infoVO.getIconGubun())) {
						infoVO.setIcon(iconUrlStr + infoVO.getIcon());
					}
				}
			}

			String jsonStr = "";
			try (StringWriter outputWriter = new StringWriter()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				mapper.writeValue(outputWriter, desktopInfoVO);
				jsonStr = outputWriter.toString();
			} catch (Exception jsonex) {
				logger.error("error in getDesktopConfData : {}", jsonex.toString());
			}

			if (re != null) {

				re.setConfInfo(jsonStr);

				DesktopConfVO[] row = new DesktopConfVO[1];
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
			logger.error("error in getDesktopConfData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * delete desktop configuration data by desktop configuration id
	 * 
	 * @param desktopConfId string target desktop configuration id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteDesktopConfData(String desktopConfId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			//
			// 1. delete master data
			long reCnt = desktopConfDao.deleteDesktopConf(desktopConfId);

			// 2. delete mapper data - apps
			desktopConfDao.deleteDesktopAppsInConf(desktopConfId);

			// 3. delete mapper data - client group
			desktopConfDao.deleteDesktopConfInClientGroup(desktopConfId);

			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("desktopconf.result.delete"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("desktopconf.result.nodelete"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in deleteDesktopConfData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteDesktopConfData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * generate desktop configuration data by client group id
	 * 
	 * @param groupId string target group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getDesktopConfByGroupId(String groupId) {

		ResultVO resultVO = new ResultVO();
		try {
			DesktopConfVO re = desktopConfDao.selectDesktopConfByGroupId(groupId);
			if (re != null) {
				DesktopConfVO[] row = new DesktopConfVO[1];
				row[0] = re;
				resultVO.setData(row);
				resultVO.setExtend(new String[] { re.getConfGrade() });
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getDesktopConfByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate desktop configuration data by dept cd
	 * 
	 * @param deptCd string dept cd
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getDesktopConfByDeptCd(String deptCd) {

		ResultVO resultVO = new ResultVO();
		try {
			DesktopConfVO re = desktopConfDao.selectDesktopConfByDeptCd(deptCd);
			if (re != null) {
				DesktopConfVO[] row = new DesktopConfVO[1];
				row[0] = re;
				resultVO.setData(row);
				resultVO.setExtend(new String[] { re.getConfGrade() });
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getDesktopConfByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * generate desktop configuration data by user id
	 * 
	 * @param userId string user id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getDesktopConfByUserId(String userId) {

		ResultVO resultVO = new ResultVO();
		try {
			DesktopConfVO re = desktopConfDao.selectDesktopConfByUserId(userId);
			if (re != null) {
				DesktopConfVO[] row = new DesktopConfVO[1];
				row[0] = re;
				resultVO.setData(row);
				resultVO.setExtend(new String[] { re.getConfGrade() });
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getDesktopConfByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * create cloned desktop configuration data (copy).
	 * 
	 * @param desktopConfId String desktop configuration id.
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO cloneDesktopConf(String desktopConfId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			DesktopConfVO desktopConfVO = new DesktopConfVO();
			desktopConfVO.setConfId(desktopConfId);
			desktopConfVO.setMngObjTp(GPMSConstants.CFG_DESKTOP_SETUP);
			desktopConfVO.setMngObjTpAbbr(GPMSConstants.CFG_DESKTOP_SETUP_ABBR);
			
			desktopConfVO.setRegUserId(LoginInfoHelper.getUserId());
			desktopConfVO.setModUserId(LoginInfoHelper.getUserId());

			// 1. insert into ctrl_item_mstr table : cloned(copy)
			long resultCnt = desktopConfDao.cloneDesktopConf(desktopConfVO);

			if (resultCnt > 0) {
				// app data
				desktopConfDao.cloneDesktopAppInConf(desktopConfVO);

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("desktopconf.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("desktopconf.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in cloneDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in cloneDesktopConf : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

}
