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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.FileVO;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.ThemeMngService;
import kr.gooroom.gpms.mng.service.ThemeVO;

/**
 * Theme management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("themeMngService")
public class ThemeMngServiceImpl implements ThemeMngService {

	private static final Logger logger = LoggerFactory.getLogger(ThemeMngServiceImpl.class);

	@Resource(name = "themeMngDAO")
	private ThemeMngDAO themeMngDAO;

	/**
	 * create new theme data
	 * 
	 * @param themeVO ThemeVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createThemeData(ThemeVO themeVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			themeVO.setModUserId(LoginInfoHelper.getUserId());
			themeVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = themeMngDAO.insertThemeData(themeVO);

			if (resultCnt > 0) {

				List<FileVO> icons = themeVO.getThemeIcons();
				for (FileVO vo : icons) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("themeId", themeVO.getThemeId());
					param.put("appNm", vo.getFileEtcInfo());
					param.put("fileNo", vo.getFileNo());
					param.put("regUserId", LoginInfoHelper.getUserId());

					resultCnt = themeMngDAO.insertThemeIconData(param);
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("theme.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("theme.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete theme data.
	 * 
	 * @param themeId String theme id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteThemeData(String themeId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re = themeMngDAO.deleteThemeData(themeId);
			if (re > 0) {
				themeMngDAO.deleteThemeIconData(themeId);
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("theme.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("theme.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * edit theme data
	 * 
	 * @param themeVO ThemeVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editThemeData(ThemeVO themeVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			themeVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = themeMngDAO.updateThemeData(themeVO);

			if (reCnt > 0) {
				List<FileVO> icons = themeVO.getThemeIcons();
				for (FileVO vo : icons) {
					HashMap<String, String> param = new HashMap<String, String>();
					param.put("themeId", themeVO.getThemeId());
					param.put("appNm", vo.getFileEtcInfo());
					param.put("fileNo", vo.getFileNo());
					param.put("regUserId", LoginInfoHelper.getUserId());

					reCnt = themeMngDAO.insertThemeIconData(param);
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("theme.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("theme.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in editThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response theme list data
	 * 
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getThemeList() throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<ThemeVO> re = themeMngDAO.selectThemeList();

			if (re != null && re.size() > 0) {

				ThemeVO[] row = re.stream().toArray(ThemeVO[]::new);

				// create theme url by filepath and filename
				for (ThemeVO vo : row) {
					vo.setWallpaperUrl(GPMSConstants.PATH_FOR_ICONURL + "/" + vo.getWallpaperFileNm());
				}

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

			logger.error("error in getThemeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;

	}

	/**
	 * response theme list data for paging
	 * 
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getThemeListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();
		try {
			List<ThemeVO> re = themeMngDAO.selectThemeListPaged(options);
			long totalCount = themeMngDAO.selectThemeListTotalCount(options);
			long filteredCount = themeMngDAO.selectThemeListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ThemeVO[] row = re.stream().toArray(ThemeVO[]::new);
				// create theme url by filepath and filename
				for (ThemeVO vo : row) {
					vo.setWallpaperUrl(GPMSConstants.PATH_FOR_ICONURL + "/" + vo.getWallpaperFileNm());
				}
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
			logger.error("error in getThemeListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response theme information data by theme id
	 * 
	 * @param themeId String theme id
	 * @return ResultVO result object
	 * @throws Exception
	 */
	@Override
	public ResultVO getThemeData(HashMap<String, Object> options) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			ThemeVO re = themeMngDAO.selectThemeData(options);

			if (re != null) {

				ThemeVO[] row = new ThemeVO[1];
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

			logger.error("error in getThemeData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
