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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.annotation.Resource;

import kr.gooroom.gpms.config.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.client.service.ClientGroupVO;
import kr.gooroom.gpms.client.service.impl.ClientDAO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;

/**
 * gooroom rule and configuration management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("ctrlMstService")
public class CtrlMstServiceImpl implements CtrlMstService {

	private static final Logger logger = LoggerFactory.getLogger(CtrlMstServiceImpl.class);

	@Resource(name = "ctrlMstDAO")
	private CtrlMstDAO ctrlMstDao;

	@Resource(name = "clientDAO")
	private ClientDAO clientDao;

	@Resource(name = "desktopConfDAO")
	private DesktopConfDAO desktopConfDao;

	/**
	 * response control item list data
	 * 
	 * @param mngObjTp string control item type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItemList(String mngObjTp) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			List<CtrlItemVO> re = ctrlMstDao.selectCtrlItemList(mngObjTp);
			if (re != null && re.size() > 0) {
				CtrlItemVO[] row = re.stream().toArray(CtrlItemVO[]::new);
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
			logger.error("error in readCtrlItemList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response control item and property list data
	 * 
	 * @param mngObjTp string control item type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItemAndPropList(String mngObjTp) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mngObjTp", mngObjTp);
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole)) {
				map.put("adminId", LoginInfoHelper.getUserId());
			} else if (GPMSConstants.GRROLE_PART.equals(grRole)) {
				map.put("partAdminId", LoginInfoHelper.getUserId());
			}
			
			List<CtrlItemVO> re = ctrlMstDao.selectCtrlItemAndPropList(map);
			if (re != null && re.size() > 0) {
				CtrlItemVO[] row = re.stream().toArray(CtrlItemVO[]::new);
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
			logger.error("error in readCtrlItemAndPropList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response control item and property list data with paging.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO readCtrlItemAndPropListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			} else if (GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("partAdminId", LoginInfoHelper.getUserId());
			}

			List<CtrlItemVO> re = ctrlMstDao.selectCtrlItemAndPropListPaged(options);
			long totalCount = ctrlMstDao.selectCtrlItemAndPropListTotalCount(options);
			long filteredCount = ctrlMstDao.selectCtrlItemAndPropListFilteredCount(options);

			if (re != null && re.size() > 0) {
				CtrlItemVO[] row = re.stream().toArray(CtrlItemVO[]::new);
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
			logger.error("error in readCtrlItemAndPropListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response control item data by item id
	 * 
	 * @param objId string control item id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItem(String objId) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<CtrlPropVO> propRe = ctrlMstDao.selectCtrlPropList(objId);
			CtrlItemVO re = ctrlMstDao.selectCtrlItem(objId);

			if (re != null) {

				CtrlPropVO[] props = new CtrlPropVO[propRe.size()];
				props = propRe.toArray(props);

				re.setPropList((ArrayList<CtrlPropVO>) propRe);

				CtrlItemVO[] row = new CtrlItemVO[1];
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
			logger.error("error in readCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response control property list by item id
	 * 
	 * @param objId string control item id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlPropList(String objId) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			List<CtrlPropVO> re = ctrlMstDao.selectCtrlPropList(objId);
			if (re != null && re.size() > 0) {

				CtrlPropVO[] row = re.stream().toArray(CtrlPropVO[]::new);
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
			logger.error("error in readCtrlPropList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createCtrlItem(CtrlItemVO itemVo, CtrlPropVO[] propVos) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// 1. insert into ctrl_item_mstr table
			long resultCnt = ctrlMstDao.insertCtrlItem(itemVo);
			if (resultCnt > 0) {

				// 2. insert into ctrl_item_prop table
				if (propVos != null && propVos.length > 0) {
					boolean isOk = true;
					for (CtrlPropVO propVo : propVos) {
						propVo.setObjId(itemVo.getObjId());
						long cnt = ctrlMstDao.insertCtrlProp(propVo);
						if (cnt < 1) {
							isOk = false;
							break;
						}
					}
					if (isOk) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
								MessageSourceHelper.getMessage("common.result.insert"));
					} else {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
								MessageSourceHelper.getMessage("common.result.noinsert"));
					}
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							MessageSourceHelper.getMessage("common.result.insert"));
				}

			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("common.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create cloned control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO cloneCtrlItem(CtrlItemVO itemVo) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// 1. insert into ctrl_item_mstr table : cloned(copy)
			long resultCnt = ctrlMstDao.insertClonedCtrlItem(itemVo);
			if (resultCnt > 0) {

				// 2. insert into ctrl_item_prop table : cloned(copy)
				CtrlPropVO propVO = new CtrlPropVO();
				propVO.setModUserId(itemVo.getModUserId());
				propVO.setNewObjId(itemVo.getNewObjId());
				propVO.setObjId(itemVo.getObjId());
				ctrlMstDao.insertClonedCtrlProp(propVO);

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("common.result.insert"));

			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("common.result.noinsert"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create default control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createCtrlDefaultItem(CtrlItemVO itemVo, CtrlPropVO[] propVos) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// 1. insert into ctrl_item_default_mstr table
			long resultCnt = ctrlMstDao.insertCtrlDefaultItem(itemVo);
			if (resultCnt > 0) {

				// 2. insert into ctrl_item_prop table
				if (propVos != null && propVos.length > 0) {
					boolean isOk = true;
					for (CtrlPropVO propVo : propVos) {
						propVo.setObjId(itemVo.getObjId());
						long cnt = ctrlMstDao.insertCtrlProp(propVo);
						if (cnt < 1) {
							isOk = false;
							break;
						}
					}

					if (isOk) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
								MessageSourceHelper.getMessage("common.result.insert"));
					} else {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
								MessageSourceHelper.getMessage("common.result.noinsert"));
					}
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
							MessageSourceHelper.getMessage("common.result.insert"));
				}

			} else {

				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("common.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createCtrlDefaultItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createCtrlDefaultItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * modify control item and property data
	 * 
	 * @param itemVo CtrlItemVO control item object bean
	 * @param propVo CtrlPropVO array control property object array
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateCtrlItem(CtrlItemVO itemVo, CtrlPropVO[] propVos) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// 1. update into ctrl_item_mstr table
			long resultCnt = ctrlMstDao.updateCtrlItem(itemVo);
			if (resultCnt > 0) {

				// 1.1 delete from ctrl_item_prop table
				ctrlMstDao.deleteCtrlProp(itemVo.getObjId());

				// 2. insert into ctrl_item_prop table
				if (propVos != null && propVos.length > 0) {
					boolean isOk = true;
					for (CtrlPropVO propVo : propVos) {
						propVo.setObjId(itemVo.getObjId());
						long cnt = ctrlMstDao.insertCtrlProp(propVo);
						if (cnt < 1) {
							isOk = false;
							break;
						}
					}
					if (isOk) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
								MessageSourceHelper.getMessage("common.result.update"));
					} else {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
								MessageSourceHelper.getMessage("common.result.noupdate"));
					}
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
							MessageSourceHelper.getMessage("common.result.update"));
				}

			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("common.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete control item and property data
	 * 
	 * @param objId    string item id
	 * @param ctrlType string control type
	 * @param confTp   string configuration type
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteCtrlItem(String objId, String ctrlType, String confTp) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			if (GPMSConstants.TYPE_RULE_CLIENT.equals(ctrlType)) {

				// delete rule tables (3)
				ctrlMstDao.deleteGroupRuleConf(objId, confTp);

				// 1. delete from ctrl_item_prop table
				ctrlMstDao.deleteCtrlProp(objId);
				ctrlMstDao.deleteCtrlPropWithLink(objId);

				// 2. delete from ctrl_item_mstr table
				long resultItemCnt = ctrlMstDao.deleteCtrlItem(objId);

				if (resultItemCnt > 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
							MessageSourceHelper.getMessage("common.result.delete"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
							MessageSourceHelper.getMessage("common.result.nodelete"));
				}

			} else if (GPMSConstants.TYPE_RULE_USER.equals(ctrlType)) {

				// delete rule tables (3)
				ctrlMstDao.deleteGroupRuleConf(objId, confTp);
				ctrlMstDao.deleteDeptRuleConf(objId, confTp);
				ctrlMstDao.deleteUserRuleConf(objId, confTp);

				// 1. delete from ctrl_prop_mstr table
				ctrlMstDao.deleteCtrlProp(objId);
				ctrlMstDao.deleteCtrlPropWithLink(objId);

				// 2. delete from ctrl_item_mstr table
				long resultItemCnt = ctrlMstDao.deleteCtrlItem(objId);

				if (resultItemCnt > 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
							MessageSourceHelper.getMessage("common.result.delete"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
							MessageSourceHelper.getMessage("common.result.nodelete"));
				}

			} else {

				// delete rule tables (3)
				ctrlMstDao.deleteGroupRuleConf(objId, confTp);
				ctrlMstDao.deleteDeptRuleConf(objId, confTp);
				ctrlMstDao.deleteUserRuleConf(objId, confTp);

				// 1. delete from ctrl_prop_mstr table
				ctrlMstDao.deleteCtrlProp(objId);
				ctrlMstDao.deleteCtrlPropWithLink(objId);

				// 2. delete from ctrl_item_mstr table
				long resultItemCnt = ctrlMstDao.deleteCtrlItem(objId);

				if (resultItemCnt > 0) {
					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
							MessageSourceHelper.getMessage("common.result.delete"));
				} else {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
							MessageSourceHelper.getMessage("common.result.nodelete"));
				}
			}

		} catch (SQLException sqlEx) {
			logger.error("error in deleteCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteCtrlItem : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response client group id by configuration id
	 * 
	 * @param confId string configuration id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readClientGroupIdByConfId(String confId) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<ClientGroupVO> re = ctrlMstDao.selectClientGroupIdByConfId(confId);

			if (re != null && re.size() > 0) {
				ClientGroupVO[] row = re.stream().toArray(ClientGroupVO[]::new);
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
			logger.error("error in readClientGroupIdByConfId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response configuration id by client group and configuration type
	 * 
	 * @param groupId  string client group id
	 * @param confType string configuration type
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readConfIdInClientGroupId(String groupId, String confType) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			List<String> re = ctrlMstDao.selectConfIdInClientGroupId(groupId, confType);
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
			logger.error("error in readConfIdInClientGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;

	}

	/**
	 * response total rule id.
	 * 
	 * @param userId   string user id
	 * @param clientId string client id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getRuleIdsByClientAndUser(String userId, String clientId) throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			List<RuleIdsVO> re = ctrlMstDao.selectRuleIdsByClientAndUser(userId, clientId);

			if (re != null && re.size() > 0) {
				RuleIdsVO[] row = re.stream().toArray(RuleIdsVO[]::new);
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
			logger.error("error in readConfIdInClientGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response rule ids by Group id.
	 * 
	 * @param groupId string group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getRuleIdsByGroupId(String groupId) throws Exception {
		ResultVO resultVO = new ResultVO();
		try {
			RuleIdsVO re = ctrlMstDao.selectRuleIdsByGroupId(groupId);
			if (re != null) {
				RuleIdsVO[] o = new RuleIdsVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				RuleIdsVO[] o = new RuleIdsVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getRuleIdsByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response control item data by group id
	 * 
	 * @param groupId string group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItemByGroupId(String groupId) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			HashMap<String, Object> hm_total = new HashMap<String, Object>();

			// Desktop Conf
			DesktopConfVO desktopConfVO = desktopConfDao.selectDesktopConfByGroupId(groupId);
			setDesktopResult(hm_total, desktopConfVO);

			// Rest rules.
			List<CtrlPropVO> propRe = ctrlMstDao.selectCtrlPropListByGroupId(groupId);
			List<CtrlItemVO> itemRe = ctrlMstDao.selectCtrlItemByGroupId(groupId);

			if (itemRe != null && itemRe.size() > 0) {

				CtrlPropVO[] props = new CtrlPropVO[propRe.size()];
				props = propRe.toArray(props);

				CtrlItemVO[] items = new CtrlItemVO[itemRe.size()];
				items = itemRe.toArray(items);

				for (CtrlItemVO item : items) {

					String confType = "";

					switch (item.getMngObjTp()) {
					case GPMSConstants.CTRL_CLIENT_SETUP_CONF:
						confType = GPMSConstants.TYPE_CLIENTCONF;
						break;
					case GPMSConstants.CTRL_HOSTS_SETUP_CONF:
						confType = GPMSConstants.TYPE_HOSTNAMECONF;
						break;
					case GPMSConstants.CTRL_UPDATE_SERVER_CONF:
						confType = GPMSConstants.TYPE_UPDATESERVERCONF;
						break;

					case GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE:
						confType = GPMSConstants.TYPE_BROWSERRULE;
						break;
					case GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE:
						confType = GPMSConstants.TYPE_MEDIARULE;
						break;
					case GPMSConstants.CTRL_ITEM_GRSECU_RULE:
						confType = GPMSConstants.TYPE_SECURITYRULE;
						break;
					case GPMSConstants.CTRL_ITEM_SWFILTER_RULE:
						confType = GPMSConstants.TYPE_FILTEREDSOFTWARE;
						break;
					case GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE:
						confType = GPMSConstants.TYPE_CTRLCENTERITEMRULE;
						break;
					case GPMSConstants.CTRL_ITEM_POLICYKIT_RULE:
						confType = GPMSConstants.TYPE_POLICYKITRULE;
						break;
					default:
						break;
					}

					addCtrolProp(hm_total, props, item, confType);
				}

				Object[] t = { hm_total };

				resultVO.setData(t);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readCtrlItemByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	private void addCtrolProp(HashMap<String, Object> hm_total, CtrlPropVO[] props, CtrlItemVO item, String confType) {
		if (!"".equals(confType)) {
			for (CtrlPropVO prop : props) {
				if (confType.equals(prop.getMngObjTp())) {
					item.getPropList().add(prop);
				}
			}
		}

		Object[] data = { item };
		Object[] extend = { item.getExtValue() };
		HashMap<String, Object> ruleMap = new HashMap<String, Object>();
		ruleMap.put("data", data);
		ruleMap.put("extend", extend);

		hm_total.put(confType, ruleMap);
	}

	/**
	 * response control item data by dept cd
	 * 
	 * @param deptCd string
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItemByDeptCd(String deptCd) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			HashMap<String, Object> hm_total = new HashMap<String, Object>();

			// Desktop Conf
			DesktopConfVO desktopConfVO = desktopConfDao.selectDesktopConfByDeptCd(deptCd);
			setDesktopResult(hm_total, desktopConfVO);

			// Rest rules.
			List<CtrlItemVO> itemRe = ctrlMstDao.selectCtrlItemByDeptCd(deptCd);
			List<CtrlPropVO> propRe = ctrlMstDao.selectCtrlPropListByDeptCd(deptCd);

			if (itemRe != null && itemRe.size() > 0) {

				CtrlPropVO[] props = new CtrlPropVO[propRe.size()];
				props = propRe.toArray(props);

				CtrlItemVO[] items = new CtrlItemVO[itemRe.size()];
				items = itemRe.toArray(items);

				for (CtrlItemVO item : items) {

					String confType = "";

					switch (item.getMngObjTp()) {
					case GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE:
						confType = GPMSConstants.TYPE_BROWSERRULE;
						break;
					case GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE:
						confType = GPMSConstants.TYPE_MEDIARULE;
						break;
					case GPMSConstants.CTRL_ITEM_GRSECU_RULE:
						confType = GPMSConstants.TYPE_SECURITYRULE;
						break;
					case GPMSConstants.CTRL_ITEM_SWFILTER_RULE:
						confType = GPMSConstants.TYPE_FILTEREDSOFTWARE;
						break;
					case GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE:
						confType = GPMSConstants.TYPE_CTRLCENTERITEMRULE;
						break;
					case GPMSConstants.CTRL_ITEM_POLICYKIT_RULE:
						confType = GPMSConstants.TYPE_POLICYKITRULE;
						break;
					default:
						break;
					}

					addCtrolProp(hm_total, props, item, confType);
				}

				Object[] t = { hm_total };

				resultVO.setData(t);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readCtrlItemByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	private void setDesktopResult(HashMap<String, Object> hm_total, DesktopConfVO desktopConfVO) {
		ResultVO desktopResultVO = new ResultVO();
		if (desktopConfVO != null) {
			DesktopConfVO[] row = { desktopConfVO };
			desktopResultVO.setData(row);
			desktopResultVO.setExtend(new String[] { desktopConfVO.getConfGrade() });
			desktopResultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
					MessageSourceHelper.getMessage("system.common.selectdata")));

			hm_total.put(GPMSConstants.TYPE_DESKTOPCONF, desktopResultVO);
		}
	}

	/**
	 * response control item data by group id
	 * 
	 * @param groupId string group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readCtrlItemByUserId(String userId) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			HashMap<String, Object> hm_total = new HashMap<String, Object>();

			// Desktop Conf
			DesktopConfVO desktopConfVO = desktopConfDao.selectDesktopConfByUserId(userId);
			setDesktopResult(hm_total, desktopConfVO);

			// Rest rules.
			List<CtrlItemVO> itemRe = ctrlMstDao.selectCtrlItemByUserId(userId);
			List<CtrlPropVO> propRe = ctrlMstDao.selectCtrlPropListByUserId(userId);

			if (itemRe != null && itemRe.size() > 0) {

				CtrlPropVO[] props = new CtrlPropVO[propRe.size()];
				props = propRe.toArray(props);

				CtrlItemVO[] items = new CtrlItemVO[itemRe.size()];
				items = itemRe.toArray(items);

				for (CtrlItemVO item : items) {

					String confType = "";

					switch (item.getMngObjTp()) {
					case GPMSConstants.CTRL_ITEM_BROWSERCTRL_RULE:
						confType = GPMSConstants.TYPE_BROWSERRULE;
						break;
					case GPMSConstants.CTRL_ITEM_MEDIACTRL_RULE:
						confType = GPMSConstants.TYPE_MEDIARULE;
						break;
					case GPMSConstants.CTRL_ITEM_GRSECU_RULE:
						confType = GPMSConstants.TYPE_SECURITYRULE;
						break;
					case GPMSConstants.CTRL_ITEM_SWFILTER_RULE:
						confType = GPMSConstants.TYPE_FILTEREDSOFTWARE;
						break;
					case GPMSConstants.CTRL_ITEM_CTRLCENTERITEM_RULE:
						confType = GPMSConstants.TYPE_CTRLCENTERITEMRULE;
						break;
					case GPMSConstants.CTRL_ITEM_POLICYKIT_RULE:
						confType = GPMSConstants.TYPE_POLICYKITRULE;
						break;
					default:
						break;
					}

					addCtrolProp(hm_total, props, item, confType);
				}

				Object[] t = { hm_total };

				resultVO.setData(t);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in readCtrlItemByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * 정책 - 적용 그룹 리스트
	 */
	@Override
	public ResultPagingVO readActivateGroupListPaged(HashMap<String, Object> options) throws Exception {
		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			List<ActivateGroupViewVO> re = ctrlMstDao.selectActivateGroupListPaged(options);
			long totalCount = ctrlMstDao.selectActivateGroupListTotalCount(options);
			long filteredCount = ctrlMstDao.selectActivateGroupListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ActivateGroupViewVO[] row = re.stream().toArray(ActivateGroupViewVO[]::new);
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
			logger.error("error in readActivateGroupListPaged( : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

}
