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

package kr.gooroom.gpms.site.service.impl;

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
import kr.gooroom.gpms.site.service.SiteMngService;
import kr.gooroom.gpms.site.service.SiteMngVO;

/**
 * site information management service implements class
 * 
 * @author HNC
 */

@Service("siteMngService")
public class SiteMngServiceImpl implements SiteMngService {

	private static final Logger logger = LoggerFactory.getLogger(SiteMngServiceImpl.class);

	@Resource(name = "siteMngDAO")
	private SiteMngDAO siteMngDao;

	/**
	 * generate site information list data
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readSiteMngList() throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<SiteMngVO> re = siteMngDao.selectSiteMngList();
			if (re != null && re.size() > 0) {
				SiteMngVO[] row = re.stream().toArray(SiteMngVO[]::new);
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
			logger.error("error in readSiteMngList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * generate site information list data for paging
	 * 
	 * @param options HashMap data bean
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getSiteMngListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			List<SiteMngVO> re = siteMngDao.selectSiteMngListPaged(options);
			long totalCount = siteMngDao.selectSiteMngListTotalCount(options);
			long filteredCount = siteMngDao.selectSiteMngListFilteredCount(options);

			if (re != null && re.size() > 0) {
				List<SiteMngVO> result = re.stream().map(temp -> {
					SiteMngVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).collect(Collectors.toList());

				SiteMngVO[] row = result.stream().toArray(SiteMngVO[]::new);
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
			logger.error("error in getSiteMngListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO createSiteMng(SiteMngVO vo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			vo.setModUserId(LoginInfoHelper.getUserId());
			vo.setRegUserId(LoginInfoHelper.getUserId());
			vo.setStatus(GPMSConstants.STS_NORMAL_USER);

			long resultCnt = siteMngDao.createSiteMng(vo);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("site.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("site.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createSiteMng : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response site information data
	 *
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO selectSiteMngData() throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			SiteMngVO re = siteMngDao.selectSiteMngData();
			if (re != null) {
				SiteMngVO[] row = new SiteMngVO[1];
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
			logger.error("error in selectSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * modify site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO updateSiteMngData(SiteMngVO siteMngVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			siteMngVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = siteMngDao.updateSiteMngData(siteMngVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("site.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("site.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in updateSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 * modify site's status data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO updateSiteStatusData(SiteMngVO siteMngVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			siteMngVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = siteMngDao.updateSiteStatusData(siteMngVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("site.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("site.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in updateSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 * delete site information data
	 * 
	 * @param vo SiteMngVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO deleteSiteMngData(SiteMngVO siteMngVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			long reCnt = siteMngDao.deleteSiteMngData(siteMngVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("site.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("site.result.nodelete"));
			}
		} catch (Exception ex) {
			logger.error("error in deleteSiteMngData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

}
