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
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.mng.service.IconGroupVO;
import kr.gooroom.gpms.mng.service.IconMngService;
import kr.gooroom.gpms.mng.service.IconVO;

/**
 * Icon management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("iconMngService")
public class IconMngServiceImpl implements IconMngService {

	private static final Logger logger = LoggerFactory.getLogger(IconMngServiceImpl.class);

	@Resource(name = "iconMngDAO")
	private IconMngDAO iconMngDAO;

	/**
	 * create new icon data
	 * 
	 * @param iconVO IconVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createIconData(IconVO iconVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			iconVO.setModUserId(LoginInfoHelper.getUserId());
			iconVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = iconMngDAO.insertIconData(iconVO);
			boolean groupFrag = true;
			if (iconVO.getGrpId() != null && iconVO.getGrpId().length() > 0) {
				groupFrag = false;
				long resultGroupCnt = iconMngDAO.insertIconInGroup(iconVO.getGrpId(), iconVO.getIconId());

				groupFrag = resultGroupCnt > 0;
			}

			if (resultCnt > 0 && groupFrag) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("icon.result.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("icon.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete icon data.
	 * 
	 * @param iconId String icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteIconData(String iconId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re = iconMngDAO.deleteIconData(iconId);
			iconMngDAO.deleteIconInGroupMapper(iconId);

			if (re > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("icon.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("icon.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * edit icon data
	 * 
	 * @param iconVO IconVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO editIconData(IconVO iconVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			iconVO.setModUserId(LoginInfoHelper.getUserId());
			long reCnt = iconMngDAO.updateIconData(iconVO);

			boolean groupFrag = true;
			if (iconVO.getGrpId() != null && iconVO.getGrpId().length() > 0) {
				groupFrag = false;
				long resultGroupCnt = iconMngDAO.insertIconInGroup(iconVO.getGrpId(), iconVO.getIconId());

				groupFrag = resultGroupCnt > 0;
			}

			if (reCnt > 0 && groupFrag) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("icon.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("icon.result.noupdate"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in editIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in editIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response icon list data by group id
	 * 
	 * @param grpId string group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getIconList(String grpId) {

		ResultVO resultVO = new ResultVO();

		try {

			List<IconVO> re = iconMngDAO.selectIconList(grpId);

			if (re != null && re.size() > 0) {

				IconVO[] row = re.toArray(IconVO[]::new);

				// create icon url by filepath and filename
				for (IconVO vo : row) {
					vo.setIconUrl(GPMSConstants.PATH_FOR_ICONURL + "/" + vo.getFileNm());
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

			logger.error("error in getIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;

	}

	/**
	 * response icon list data that not include any group.
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getNoGroupIconList() {

		ResultVO resultVO = new ResultVO();

		try {

			List<IconVO> re = iconMngDAO.selectNoGroupIconList();

			if (re != null && re.size() > 0) {

				IconVO[] row = re.toArray(IconVO[]::new);
				// create icon url by filepath and filename
				for (IconVO vo : row) {
					vo.setIconUrl(GPMSConstants.PATH_FOR_ICONURL + "/" + vo.getFileNm());
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

			logger.error("error in getNoGroupIconList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;

	}

	/**
	 * response icon information data by icon id
	 * 
	 * @param iconId String icon id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getIconData(String iconId) {

		ResultVO resultVO = new ResultVO();

		try {

			IconVO re = iconMngDAO.selectIconData(iconId);

			if (re != null) {

				IconVO[] row = new IconVO[1];
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

			logger.error("error in getIconData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * create new icon group data
	 * 
	 * @param iconGroupVO IconGroupVO data bean
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO createIconGroupData(IconGroupVO iconGroupVO) {

		StatusVO statusVO = new StatusVO();

		try {

			iconGroupVO.setModUserId(LoginInfoHelper.getUserId());
			iconGroupVO.setRegUserId(LoginInfoHelper.getUserId());

			long resultCnt = iconMngDAO.insertIconGroupData(iconGroupVO);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("icongroup.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("icongroup.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * delete icon group data
	 * 
	 * @param iconGroupId String icon group id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteIconGroupData(String iconGroupId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long re1 = iconMngDAO.deleteIconGroup(iconGroupId);
			iconMngDAO.deleteIconGroupLink(iconGroupId);

			if (re1 > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("icongroup.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("icongroup.result.nodelete"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {

			logger.error("error in deleteIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * modify icon group data.
	 * 
	 * @param iconGroupVO IconGroupVO data bean
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO editIconGroupData(IconGroupVO iconGroupVO) {

		StatusVO statusVO = new StatusVO();

		try {

			iconGroupVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = iconMngDAO.updateIconGroup(iconGroupVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("icongroup.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("icongroup.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in editIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * response icon group list data
	 * 
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getIconGroupList() {

		ResultVO resultVO = new ResultVO();

		try {

			List<IconGroupVO> re = iconMngDAO.selectIconGroupList();

			if (re != null && re.size() > 0) {

				IconGroupVO[] row = re.toArray(IconGroupVO[]::new);
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

			logger.error("error in getIconGroupList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;

	}

	/**
	 * response icon group information data
	 * 
	 * @param iconGroupId string icon group id
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO getIconGroupData(String iconGroupId) {

		ResultVO resultVO = new ResultVO();

		try {

			IconGroupVO re = iconMngDAO.selectIconGroupData(iconGroupId);

			if (re != null) {

				IconGroupVO[] row = new IconGroupVO[1];
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

			logger.error("error in getIconGroupData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * insert icon data into icon group
	 * 
	 * @param grpId     string icon group id
	 * @param icon_list string array that icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createIconsInGroup(String grpId, String[] icon_list) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			boolean isOk = true;
			for (String s : icon_list) {
				long cnt = iconMngDAO.insertIconInGroup(grpId, s);
				if (cnt < 1) {
					isOk = false;
					break;
				}
			}

			if (isOk) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("icon.icongroup.insert"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("icon.icongroup.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createIconsInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createIconsInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * remove icon from into icon group
	 * 
	 * @param grpId  string icon group id
	 * @param iconId string icon id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteIconInGroup(String grpId, String iconId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			long cnt = iconMngDAO.deleteIconInGroup(grpId, iconId);

			if (cnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("icon.icongroup.delete"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("icon.icongroup.nodelete"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in deleteIconInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteIconInGroup : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}
}
