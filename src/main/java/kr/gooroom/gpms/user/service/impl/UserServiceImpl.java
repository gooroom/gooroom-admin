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

package kr.gooroom.gpms.user.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import kr.gooroom.gpms.common.service.ExcelCommonService;
import kr.gooroom.gpms.common.utils.CommonUtils;
import kr.gooroom.gpms.config.service.impl.ClientConfDAO;
import kr.gooroom.gpms.dept.service.DeptVO;
import kr.gooroom.gpms.dept.service.impl.DeptDAO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ExcelStyleDateFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.UserAndClientVO;
import kr.gooroom.gpms.user.service.UserRoleVO;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;

/**
 * user management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Resource(name = "deptDAO")
	private DeptDAO deptDao;

	@Resource(name = "userDAO")
	private UserDAO userDao;

	@Resource(name = "clientConfDAO")
	private ClientConfDAO clientConfDao;

	@Resource(name = "excelCommonService")
	private ExcelCommonService excelCommonService;

	/**
	 * modify user information data
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO updateUserData(UserVO userVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			userVO.setModUserId(LoginInfoHelper.getUserId());
			if (GPMSConstants.GUBUN_YES.equalsIgnoreCase(userVO.getIsChangePasswd())) {
				userVO.setPasswordStatus(GPMSConstants.STS_TEMP_PASSWORD);
			}

			long reCnt = userDao.updateUserData(userVO);
			if (reCnt > 0) {
				long cnt = -1;
				String cfgId = userVO.getBrowserRuleId();

				// 브라우져설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_BROWSERRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_BROWSERRULE);
				}

				cnt = -1;
				cfgId = userVO.getMediaRuleId();
				// 매체제어설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_MEDIARULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_MEDIARULE);
				}

				cnt = -1;
				cfgId = userVO.getSecurityRuleId();
				// 단말보안설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_SECURITYRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_SECURITYRULE);
				}

				cnt = -1;
				cfgId = userVO.getFilteredSoftwareRuleId();
				// filtered software rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_FILTEREDSOFTWARE);
				}

				cnt = -1;
				cfgId = userVO.getCtrlCenterItemRuleId();
				// control center item rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_CTRLCENTERITEMRULE);
				}

				cnt = -1;
				cfgId = userVO.getPolicyKitRuleId();
				// policy kit rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_POLICYKITRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_POLICYKITRULE);
				}

				cnt = -1;
				cfgId = userVO.getDesktopConfId();
				// 데스크톱설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_DESKTOPCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_DESKTOPCONF);
				}

				// long reConfCnt = userDao.insertOrUpdateDesktopConfWithUser(userVO);

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("user.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("user.result.noupdate"));
			}

		} catch (Exception ex) {
			logger.error("error in updateUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * reset user login trial count
	 * 
	 * @param userId String user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO updateUserLoginTrialCount(String userId) throws Exception {

		StatusVO statusVO = new StatusVO();

		UserVO userVO = new UserVO();
		userVO.setModUserId(LoginInfoHelper.getUserId());
		userVO.setUserId(userId);

		try {
			long reCnt = userDao.updateUserLoginTrialCount(userVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("user.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("user.result.noupdate"));
			}

		} catch (Exception ex) {
			logger.error("error in updateUserLoginTrialCount : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * delete user information data
	 * 
	 * @param userId String user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO deleteUserData(String userId) throws Exception {

		StatusVO statusVO = new StatusVO();

		UserVO userVO = new UserVO();
		userVO.setModUserId(LoginInfoHelper.getUserId());
		userVO.setUserId(userId);
		userVO.setStatus(GPMSConstants.STS_DELETE_USER);

		try {

			long reCnt = userDao.deleteUserData(userVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("user.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("user.result.nodelete"));
			}

		} catch (Exception ex) {
			logger.error("error in deleteUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * generate user list data
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readUserList() throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			List<UserVO> re = userDao.readUserList();

			if (re != null && re.size() > 0) {

				List<UserVO> result = re.stream().map(temp -> {
					UserVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).collect(Collectors.toList());

				UserVO[] row = result.stream().toArray(UserVO[]::new);
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
			logger.error("error in readUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate user list data for paging.
	 * 
	 * @param options HashMap<String, Object>.
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getUserListPaged(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<UserVO> re = userDao.selectUserListPaged(options);
			long totalCount = userDao.selectUserListTotalCount(options);
			long filteredCount = userDao.selectUserListFilteredCount(options);

			if (re != null && re.size() > 0) {

				List<UserVO> result = re.stream().map(temp -> {
					UserVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).collect(Collectors.toList());

				UserVO[] row = result.stream().toArray(UserVO[]::new);
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
			logger.error("error in getUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * check duplicate user id
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public StatusVO isNoExistUserId(String userId) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			boolean re = userDao.isExistUserId(userId);

			if (re) {
				// warning : duplicate is fail status.
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("user.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("user.result.noduplicate"));
			}

		} catch (Exception ex) {
			logger.error("error in isExistUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * check duplicate user id list
	 *
	 * @param ids list of user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO isNoExistInUserIdList(HashMap<String, Object> ids) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {
			List<String> idList = userDao.selectUserListForDuplicateUserId(ids);
			if (idList.size() == 0) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("user.result.noduplicate")));
			} else {
				resultVO.setData(idList.toArray());
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("user.result.duplicate")));
			}
		} catch (Exception ex) {
			logger.error("error in duplicate id : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * create new user data
	 *
	 * @param vo UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO createUserData(UserVO vo, boolean isPortable) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			vo.setModUserId(LoginInfoHelper.getUserId());
			vo.setRegUserId(LoginInfoHelper.getUserId());
			vo.setStatus(GPMSConstants.STS_NORMAL_USER);
			if (!isPortable)
				vo.setPasswordStatus(GPMSConstants.STS_TEMP_PASSWORD);

			long resultCnt = userDao.createUser(vo);
			if (resultCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("user.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("user.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * create new user data with rule
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO createUserDataWithRule(UserVO userVO, boolean isPortable) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			userVO.setModUserId(LoginInfoHelper.getUserId());
			userVO.setRegUserId(LoginInfoHelper.getUserId());
			userVO.setStatus(GPMSConstants.STS_NORMAL_USER);

			if (isPortable)
				userVO.setPasswordStatus(GPMSConstants.STS_NORMAL_USER);
			else
				userVO.setPasswordStatus(GPMSConstants.STS_TEMP_PASSWORD);

			long resultCnt = userDao.createUser(userVO);
			if (resultCnt > 0) {

				long cnt = -1;
				String cfgId = userVO.getBrowserRuleId();

				// 브라우져설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_BROWSERRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_BROWSERRULE);
				}

				cnt = -1;
				cfgId = userVO.getMediaRuleId();
				// 매체제어설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_MEDIARULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_MEDIARULE);
				}

				cnt = -1;
				cfgId = userVO.getSecurityRuleId();
				// 단말보안설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_SECURITYRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_SECURITYRULE);
				}

				cnt = -1;
				cfgId = userVO.getFilteredSoftwareRuleId();
				// filtered software rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_FILTEREDSOFTWARE);
				}

				cnt = -1;
				cfgId = userVO.getCtrlCenterItemRuleId();
				// control center item rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_CTRLCENTERITEMRULE);
				}

				cnt = -1;
				cfgId = userVO.getPolicyKitRuleId();
				// policy kit rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_POLICYKITRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_POLICYKITRULE);
				}

				cnt = -1;
				cfgId = userVO.getDesktopConfId();
				// 데스크톱정보
				if (cfgId != null && cfgId.length() > 0) {
					cnt = userDao.insertOrUpdateConfigWithUser(userVO.getUserId(), userVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_DESKTOPCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = userDao.deleteConfigWithUser(userVO.getUserId(), GPMSConstants.TYPE_DESKTOPCONF);
				}

				// long reConfCnt = userDao.insertOrUpdateDesktopConfWithUser(userVO);
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("user.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("user.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createUserDataWithRule : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response user information data
	 * 
	 * @param userId string user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO readUserData(String userId) throws Exception {

		ResultVO resultVO = new ResultVO();

		try {

			UserVO re = userDao.readUserData(userId);

			if (re != null) {

				UserVO[] row = new UserVO[1];
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
			logger.error("error in readUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * register NFC data to user information data
	 * 
	 * @param userVO UserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	public StatusVO editUserNfcData(UserVO userVO) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {
			userVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = userDao.editUserNfcData(userVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("usernfc.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("usernfc.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in editUserNfcData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * response user list data in organization specified.
	 * 
	 * @param deptCd string organization id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserListInDept(String deptCd) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {
			String[] deptArray = deptCd.split(",");
			List<UserVO> re = userDao.selectUserListInDept(deptArray);

			if (re != null && re.size() > 0) {

				List<UserVO> result = re.stream().map(temp -> {
					UserVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).collect(Collectors.toList());

				UserVO[] row = result.stream().toArray(UserVO[]::new);
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
			logger.error("error in getUserListInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * generate user list data for paging.
	 * 
	 * @param options HashMap<String, Object>.
	 * @return ResultPagingVO result object
	 * @throws Exception
	 */
	@Override
	public ResultPagingVO getUserListPagedInDept(HashMap<String, Object> options) throws Exception {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			
			String grRole = LoginInfoHelper.getUserGRRole();
			if (GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
				options.put("adminId", LoginInfoHelper.getUserId());
			}

			List<UserVO> re = userDao.selectUserListPagedInDept(options);
			long totalCount = userDao.selectUserListInDeptTotalCount(options);
			long filteredCount = userDao.selectUserListInDeptFilteredCount(options);

			if (re != null && re.size() > 0) {

				List<UserVO> result = re.stream().map(temp -> {
					UserVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).collect(Collectors.toList());

				UserVO[] row = result.stream().toArray(UserVO[]::new);
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
			logger.error("error in getUserListPagedInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * response user list data in organization array specified.
	 * 
	 * @param deptCds string array organization id list
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserListInDeptArray(String[] deptCds) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {
			List<UserVO> re = userDao.selectUserListInDept(deptCds);

			if (re != null && re.size() > 0) {
				UserVO[] row = re.stream().toArray(UserVO[]::new);
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
			logger.error("error in getUserListInDeptArray : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * response user list data in online user.
	 * 
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserListInOnline() throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			// select client in online
			List<UserAndClientVO> re = userDao.selectUserListInOnline();

			if (re != null && re.size() > 0) {
				UserAndClientVO[] row = re.stream().toArray(UserAndClientVO[]::new);
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
			logger.error("error in getUserListInOnline : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get user configuration information data by user id.
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserConfIdByUserId(String userId) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			UserRoleVO re = userDao.selectUserConfIdByUserId(userId);

			if (re != null) {
				UserRoleVO[] o = new UserRoleVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				UserRoleVO[] o = new UserRoleVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getUserConfIdByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get user configuration information data by dept code from user id.
	 * 
	 * @param userId String user id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserConfIdByDeptCdFromUserId(String userId) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {

			UserRoleVO re = userDao.selectUserConfIdByDeptCdFromUserId(userId);

			if (re != null) {
				UserRoleVO[] o = new UserRoleVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				UserRoleVO[] o = new UserRoleVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getUserConfIdByUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get user configuration information data by dept code.
	 * @param deptCd
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserConfIdByDeptCd(String deptCd) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {
			UserRoleVO re = userDao.selectUserConfIdByDeptCd(deptCd);
			if (re != null) {
				UserRoleVO[] o = new UserRoleVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				UserRoleVO[] o = new UserRoleVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getUserConfIdByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * get user configuration information data by group id.
	 * 
	 * @param groupId String group id
	 * @return ResultVO result data bean
	 * @throws Exception
	 */
	@Override
	public ResultVO getUserConfIdByGroupId(String groupId) throws Exception {

		ResultVO resultVO = new ResultVO();
		try {
			UserRoleVO re = userDao.selectUserConfIdByGroupId(groupId);
			if (re != null) {
				UserRoleVO[] o = new UserRoleVO[1];
				o[0] = re;
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				UserRoleVO[] o = new UserRoleVO[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getUserConfIdByGroupId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}
		return resultVO;
	}

	/**
	 * insert user data into organization.
	 * 
	 * @param deptCd    String organization id
	 * @param user_list String array user id
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createUsersInDept(String deptCd, String[] user_list) throws Exception {

		StatusVO statusVO = new StatusVO();
		try {
			boolean isOk = true;
			for (int i = 0; i < user_list.length; i++) {
				long cnt = userDao.updateUserWithDept(deptCd, user_list[i]);
				if (cnt < 1) {
					isOk = false;
					break;
				}
			}
			if (isOk) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("org.result.update"));
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("org.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in createUsersInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createUsersInDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 *
	 * @param headList
	 * @return
	 * @throws Exception
	 */
	@Override
	public StatusVO isUserHeadListExist(List<String> headList) throws Exception {
		StatusVO statusVO = new StatusVO();
		boolean re = true;
		try {
			//title
			String[] excelHeadList = {"사용자아이디",
					"사용자이름",
					"패스워드",
					"조직아이디",
					"이메일",
					"사용자만료일",
					"패스워드만료일"};

			for(int idx=0;idx<4;idx++) {
				if(!excelHeadList[idx].equals(headList.get(idx))) {
					re = false;
					break;
				}
			}

			if(!re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NOEXIST,
						MessageSourceHelper.getMessage("org.result.noexcelhead"));
			}

		} catch (Exception ex) {
			logger.error("error in isUserHeadListExist : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 *
	 * @param rowData
	 * @return
	 * @throws Exception
	 */
	@Override
	public StatusVO isRequiredDataExist(List<String> rowData) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {

			for(int idx=0;idx<7;idx++) {
				if(idx == 2 || idx == 3 || idx == 5 || idx == 6) {
					continue;
				}

				if(rowData.get(idx).equals("")) {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NOEXIST,
							MessageSourceHelper.getMessage("org.result.norequireddata"));
					break;
				}
			}

		} catch (Exception ex) {
			logger.error("error in isRequiredDataExist : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	/**
	 * 사용자 정보 일괄 등록 체크(파일자료)
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResultVO isCanUpdateUserDataFromFile(List<List<String>> dataList) throws Exception {

		ResultVO resultVO = new ResultVO();

		List<UserVO> userList = new ArrayList<UserVO>();
		try {
			// excel head list check
			StatusVO statusVO = isUserHeadListExist(dataList.get(0));
			if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(statusVO.getResult())) {
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							"사용자 정보 파일에 항목이 존재하지 않습니다."));
				}
				return resultVO;
			}
			dataList.remove(0);  //head list 삭제

			if(dataList.size() > 0) {
				for (List<String> list : dataList) {
					// check required data
					statusVO = isRequiredDataExist(list);
					if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(statusVO.getResult())) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"사용자 필수정보가 입력되지 않았습니다."));
						}
						return resultVO;
					}

					UserVO userVO = new UserVO(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
					// check regex
					if (!excelCommonService.userIdRegex(userVO.getUserId())) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"사용자 아이디 형식이 맞지 않습니다"));
						}
						return resultVO;
					}

					if (!excelCommonService.emailRegex(userVO.getUserEmail())) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"이메일형식이 맞지 않습니다."));
						}
						return resultVO;
					}

					// not exist dept
					if (deptDao.selectDeptData(userVO.getDeptCd()) == null) {
						userVO.setDeptCd("DEPTDEFAULT");
					}

					// set passwd
					if (userVO.getUserPasswd().equals("")) {
						userVO.setUserPasswd("1"); //set basic passwd
						userVO.setPasswordStatus(GPMSConstants.STS_TEMP_PASSWORD);
					} else {
						userVO.setPasswordStatus(GPMSConstants.STS_NORMAL_USER);
					}

					// set expired date
					if (CommonUtils.validationDate(list.get(5))) {
						userVO.setExpireDate(CommonUtils.yyyyMMddToDate(list.get(5)));
					} else {
						if (!list.get(5).equals("")) {
							if (resultVO != null) {
								resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
										"사용자 만료일 형식이 맞지 않습니다."));
							}
							return resultVO;
						}
					}

					// set passwd expired date
					if (CommonUtils.validationDate(list.get(6))) {
						userVO.setPasswordExpireDate(CommonUtils.yyyyMMddToDate(list.get(6)));
					} else {
						if (!list.get(6).equals("")) {
							if (resultVO != null) {
								resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
										"패스워드 만료일 형식이 맞지 않습니다."));
							}
							return resultVO;
						}
					}

					userList.add(userVO);
				}
			} else {
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							"저장할 내용이 없음"));
				}
			}

			if (userList != null && userList.size() > 0) {
				UserVO[] row = userList.stream().toArray(UserVO[]::new);
				resultVO.setData(row);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, "GRSM0000",
						"파일데이터확인 완료"));
			}

		} catch (Exception ex) {
			logger.error("error in isCanUpdateUserDataFromFile : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * insert user data all from file.
	 * @param userVOs
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateUserDataFromFile(List<UserVO> userVOs) throws Exception {

		StatusVO statusVO = new StatusVO();
		try {
			
			if(userVOs != null && userVOs.size() > 0) {
				
				// remove all data
				boolean isDelete = userDao.deleteUserAll();
				if(isDelete) {
					boolean reFlag = true;
					for(UserVO vo: userVOs) {
						vo.setRegUserId(LoginInfoHelper.getUserId());
						vo.setStatus(GPMSConstants.STS_NORMAL_USER);
						//vo.setPasswordStatus(GPMSConstants.STS_TEMP_PASSWORD);
						vo.setLoginTrial(String.valueOf(
								clientConfDao.selectSiteLoginTrialCount("")));
						
						long re = userDao.createUserRawData(vo);
						if(re < 1) {
							reFlag = false;
							break;
						}
					}
					
					if(reFlag) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "사용자 정보가 등록되었습니다.");	
					} else {
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "사용자 정보가 등록되지 않았습니다.[1]");
						throw new SQLException();
					}
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "사용자 정보가 등록되지 않았습니다.[2]");
					throw new SQLException();
				}
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "사용자 정보가 등록되지 않았습니다.[3]");
			}

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "ERR999", "시스템 오류가 발생하였습니다.");
		}

		return statusVO;
	}

	/**
	 * 사용자 정보 일괄 다운로드
	 *
	 * @return XSSFWorkbook
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public XSSFWorkbook createUserFileFromData() throws Exception {

		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("status", GPMSConstants.STS_NORMAL_USER);

		try {
			//List<UserVO> re = userDao.readUserList(); //삭제된 사용자 포함시
			List<UserVO> re = userDao.readUserListWithoutDel(options);
			List<List<String>> excleWriteList = new ArrayList<List<String>>();

			//title
			excleWriteList.add(Arrays.asList("사용자아이디",
					"사용자이름",
					"패스워드",
					"조직아이디",
					"이메일",
					"사용자만료일",
					"패스워드만료일"));

			//contents
			for(UserVO userVO : re) {

				excleWriteList.add(Arrays.asList(
						userVO.getUserId(),
						userVO.getUserNm(),
						userVO.getUserPasswd(),
						userVO.getDeptCd(),
						userVO.getUserEmail(),
						CommonUtils.convertDataToString(userVO.getExpireDate()),
						CommonUtils.convertDataToString(userVO.getPasswordExpireDate())
				));
			}

			return excelCommonService.write(excleWriteList);

		} catch (Exception ex) {
			logger.error("error in createDeptDFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return new XSSFWorkbook();
	}

	/**
	 * 사용자 정보 업로드 샘플파일 다운로드
	 *
	 * @return XSSFWorkbook
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public XSSFWorkbook createUserSampleFileFromData() throws Exception {

		try {
			List<List<String>> excleWriteList = new ArrayList<List<String>>();

			//title
			excleWriteList.add(Arrays.asList("사용자아이디",
					"사용자이름",
					"패스워드",
					"조직아이디",
					"이메일",
					"사용자만료일",
					"패스워드만료일"));

			return excelCommonService.write(excleWriteList);

		} catch (Exception ex) {
			logger.error("error in createUserSampleFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return new XSSFWorkbook();
	}

	/**
	 * 여러사용자 권한정보 일괄 수정
	 */
	public StatusVO updateRuleInfoToMultiUser(ArrayList<String> users, String browserRuleId, String mediaRuleId,
											  String securityRuleId, String filteredSoftwareRuleId, String ctrlCenterItemRuleId, String policyKitRuleId,
											  String desktopConfId) throws Exception {
		StatusVO statusVO = new StatusVO();

		try {
			if (users != null && users.size() > 0) {
				String modUserId = LoginInfoHelper.getUserId();
				// 사용자 정책이 적용 되어 있으면 삭제(사용자 정책이 없을 경우 조직의 정책을 따라감)
				for (int i=0; i < users.size(); i++) {
					long cnt = -1;
					// 브라우져설정
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_BROWSERRULE);

					// 매체제어설정
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_MEDIARULE);

					// 단말보안설정
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_SECURITYRULE);

					// filtered software rule
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_FILTEREDSOFTWARE);

					// control center item
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_CTRLCENTERITEMRULE);

					// policy kit
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_POLICYKITRULE);

					// 데스크톱설정
					userDao.deleteConfigWithUser(users.get(i), GPMSConstants.TYPE_DESKTOPCONF);

					statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
							MessageSourceHelper.getMessage("user.result.update"));
				}
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("user.result.update"));
			}
		} catch (Exception ex) {
			logger.error("error in updateRuleInfoToMultiUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return  statusVO;
	}
}
