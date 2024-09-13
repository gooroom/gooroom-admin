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

import jakarta.annotation.Resource;
import kr.gooroom.gpms.account.service.ActHistoryVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.user.service.AdminUserService;
import kr.gooroom.gpms.user.service.AdminUserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * administrator user management service implements class
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@Service("adminUserService")
public class AdminUserServiceImpl implements AdminUserService {

	private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);

	@Resource(name = "adminUserDAO")
	private AdminUserDAO adminUserDao;

	@Autowired
	private Pbkdf2PasswordEncoder passwordEncoder;

	private final String[][] RULEINFOS = { { "client_admin", "client_admin", "isClientAdmin" },
			{ "user_admin", "user_admin", "isUserAdmin" }, { "desktop_admin", "desktop_admin", "isDesktopAdmin" }, 
			{ "notice_admin", "notice_admin", "isNoticeAdmin" },{ "portable_admin", "portable_admin", "isPortableAdmin" }};


	private void saveClientConfiguration(AdminUserVO adminUserVO) throws SQLException {
		long cnt = -1;
		// save client configuration information
		for (String[] items : RULEINFOS) {
			cnt = adminUserDao.insertOrUpdateAdminRule(adminUserVO.getAdminId(), items[0], items[1],
					adminUserVO.getValueByString(items[2]), LoginInfoHelper.getUserId());

			if (cnt < 0) {
				throw new SQLException();
			}
			cnt = -1;
		}

		// save conn ips
		adminUserDao.deleteAdminUserConnIps(adminUserVO.getAdminId());
		if (adminUserVO.getConnIps() != null && adminUserVO.getConnIps().size() > 0) {
			cnt = -1;
			for (int i = 0; i < adminUserVO.getConnIps().size(); i++) {
				cnt = adminUserDao.insertAdminUserConnIp(adminUserVO.getAdminId(), adminUserVO.getRegUserId(),
						adminUserVO.getConnIps().get(i));
				if (cnt < 0) {
					throw new SQLException();
				}
				cnt = -1;
			}
		}

		// save admin's managed group ids
		adminUserDao.deleteAdminUserGrpIds(adminUserVO.getAdminId());
		if (adminUserVO.getGrpIds() != null && adminUserVO.getGrpIds().size() > 0) {
			cnt = -1;
			for (int i = 0; i < adminUserVO.getGrpIds().size(); i++) {
				cnt = adminUserDao.insertAdminUserGrpId(adminUserVO.getAdminId(), adminUserVO.getRegUserId(),
						adminUserVO.getGrpIds().get(i));
				if (cnt < 0) {
					throw new SQLException();
				}
				cnt = -1;
			}
		}

		// save admin's managed dept cds
		adminUserDao.deleteAdminUserDeptCds(adminUserVO.getAdminId());
		if (adminUserVO.getDeptCds() != null && adminUserVO.getDeptCds().size() > 0) {
			cnt = -1;
			for (int i = 0; i < adminUserVO.getDeptCds().size(); i++) {
				cnt = adminUserDao.insertAdminUserDeptCd(adminUserVO.getAdminId(), adminUserVO.getRegUserId(),
						adminUserVO.getDeptCds().get(i));
				if (cnt < 0) {
					throw new SQLException();
				}
				cnt = -1;
			}
		}

	}

	/**
	 * modify administrator user information data with divided rules.
	 * 
	 * @param adminUserVO AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateAdminUserData(AdminUserVO adminUserVO) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			adminUserVO.setModUserId(LoginInfoHelper.getUserId());
			adminUserVO.setRegUserId(LoginInfoHelper.getUserId());

			String adminPw =  adminUserVO.getAdminPw();
			adminUserVO.setAdminPw(passwordEncoder.encode(adminPw));

			long reCnt = adminUserDao.updateAdminUserData(adminUserVO);
			if (reCnt > 0) {
				saveClientConfiguration(adminUserVO);
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("admin.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("admin.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * modify current administrator user information data (pollingCycle)
	 * 
	 * @param adminUserVO AdminUserVO data bean
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO setCurrentAdminUserData(AdminUserVO adminUserVO) {
		StatusVO statusVO = new StatusVO();
		try {
			adminUserVO.setAdminId(LoginInfoHelper.getUserId());
			adminUserVO.setModUserId(adminUserVO.getAdminId());
			long reCnt = adminUserDao.updateCurrentAdminUserData(adminUserVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("admin.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("admin.result.noupdate"));
			}
		} catch (Exception ex) {
			logger.error("error in setCurrentAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * delete administrator user information data
	 * 
	 * @param adminUserVO AdminUserVO data bean
	 * @return StatusVO result status
	 */
	@Override
	public StatusVO deleteAdminUserData(AdminUserVO adminUserVO) {

		StatusVO statusVO = new StatusVO();

		try {

			long reCnt = adminUserDao.deleteAdminUserData(adminUserVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
						MessageSourceHelper.getMessage("admin.result.delete"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("admin.result.nodelete"));
			}

		} catch (Exception ex) {
			logger.error("error in deleteAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * generate administrator user list data
	 * 
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO readAdminUserList() {
		ResultVO resultVO = new ResultVO();
		try {
			List<AdminUserVO> re = adminUserDao.selectAdminUserList();
			if (re != null && re.size() > 0) {
				AdminUserVO[] row = re.toArray(AdminUserVO[]::new);
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
			logger.error("error in readAdminUserList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		return resultVO;
	}

	/**
	 * generate administrator user list data
	 * 
	 * @param options HashMap data bean
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultPagingVO getAdminUserListPaged(HashMap<String, Object> options) {
		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<AdminUserVO> re = adminUserDao.selectAdminUserListPaged(options);
			long totalCount = adminUserDao.selectAdminUserListTotalCount(options);
			long filteredCount = adminUserDao.selectAdminUserListFilteredCount(options);

			if (re != null && re.size() > 0) {

				List<AdminUserVO> result = re.stream().map(temp -> {
					AdminUserVO obj = temp;
					if (GPMSConstants.STS_NORMAL_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.normal"));
					} else if (GPMSConstants.STS_DELETE_USER.equals(temp.getStatus())) {
						obj.setStatus(MessageSourceHelper.getMessage("user.status.delete"));
					}
					return obj;
				}).toList();

				AdminUserVO[] row = result.toArray(AdminUserVO[]::new);
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
			logger.error("error in getAdminUserListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * check duplicate user id
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 */
	@Override
	public StatusVO isExistAdminUserId(String adminId) {

		StatusVO statusVO = new StatusVO();

		try {

			boolean re = adminUserDao.isExistAdminUserId(adminId);

			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("admin.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("admin.result.noduplicate"));
			}

		} catch (Exception ex) {
			logger.error("error in isExistAdminUserId : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * create new administrator user data
	 * 
	 * @param  adminUserVO AdminUserVO data bean
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO createAdminUser(AdminUserVO adminUserVO) throws Exception {
		StatusVO statusVO = new StatusVO();

		try {
			adminUserVO.setModUserId(LoginInfoHelper.getUserId());
			adminUserVO.setRegUserId(LoginInfoHelper.getUserId());
			adminUserVO.setStatus(GPMSConstants.STS_NORMAL_USER);

			String hashedPassword = passwordEncoder.encode(adminUserVO.getAdminPw());
			adminUserVO.setAdminPw(hashedPassword);

			long reCnt = adminUserDao.createAdminUser(adminUserVO);
			if (reCnt > 0) {
				saveClientConfiguration(adminUserVO);
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("admin.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("admin.result.noinsert"));
			}

		} catch (SQLException sqlEx) {
			logger.error("error in createAdminUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in createAdminUser : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * response administrator user information data
	 * 
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO selectAdminUserData(String adminId) {

		ResultVO resultVO = new ResultVO();

		try {
			HashMap<String, Object> options = new HashMap<>();
			options.put("adminId", adminId);

			AdminUserVO re = adminUserDao.selectAdminUserData(options);

			if (re != null) {

				AdminUserVO[] row = new AdminUserVO[1];
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
			logger.error("error in selectAdminUserData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * get administrator user information data by user id and password.
	 * 
	 * @param adminId string user id
	 * @param adminPw string user password
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getAdminUserAuthAndInfo(String adminId, String adminPw) {

		ResultVO resultVO = new ResultVO();

		try {
			AdminUserVO re = adminUserDao.selectAdminUserAuthAndInfo(adminId);
			if (re != null) {
				if (passwordEncoder.matches(adminPw, re.getAdminPw())) {
					AdminUserVO[] row = new AdminUserVO[1];
					row[0] = re;
					resultVO.setData(row);
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
							MessageSourceHelper.getMessage("system.common.selectdata")));
					return resultVO;
				}
			}

			Object[] o = new Object[0];
			resultVO.setData(o);
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
					MessageSourceHelper.getMessage("system.common.noselectdata")));
		} catch (Exception ex) {
			logger.error("error in getAdminUserAuthAndInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * get administrator user action logging data.
	 * <p>
	 * logging history data.
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultPagingVO result data bean
	 */
	@Override
	public ResultPagingVO getAdminActListPaged(HashMap<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			List<ActHistoryVO> re = adminUserDao.selectAdminActListPaged(options);
			long totalCount = adminUserDao.selectAdminActListTotalCount(options);
			long filteredCount = adminUserDao.selectAdminActListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ActHistoryVO[] row = re.toArray(ActHistoryVO[]::new);
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
			logger.error("error in getAdminActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * get administrator user action logging data paging.
	 * <p>
	 * logging history data.
	 * 
	 * @param options HashMap data bean
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultPagingVO getAdminRecordListPaged(HashMap<String, Object> options) {
		ResultPagingVO resultVO = new ResultPagingVO();

		try {
			List<ActHistoryVO> re = adminUserDao.selectAdminRecordListPaged(options);
			long totalCount = adminUserDao.selectAdminRecordListTotalCount(options);
			long filteredCount = adminUserDao.selectAdminRecordListFilteredCount(options);

			if (re != null && re.size() > 0) {
				ActHistoryVO[] row = re.toArray(ActHistoryVO[]::new);
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
			logger.error("error in getAdminRecordListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}
	
	
	/**
	 * clear administrator login trial info
	 * 
	 * @return StatusVO result status
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateLoginTrialData() throws Exception {
		StatusVO statusVO = new StatusVO();
		
		try {
			long reCnt = adminUserDao.updateLoginTrialData(LoginInfoHelper.getUserId());
			
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("admin.result.update"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("admin.result.noupdate"));
			}
		} catch (SQLException sqlEx) {
			logger.error("error in updateLoginTrialData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateLoginTrialData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}
		return statusVO;
	}

	/**
	 * get administrator user type.
	 *
	 * @param adminId string user id
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getAdminUserInfo(String adminId) {

		ResultVO resultVO = new ResultVO();

		try {
			AdminUserVO re = adminUserDao.selectAdminUserInfo(adminId);

			Object[] o = new Object[0];
			resultVO.setData(o);
			if (re != null) {
				if (!re.getAdminTp().equalsIgnoreCase("S")) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							MessageSourceHelper.getMessage("admin.result.noinsert")));
					return resultVO;
				}
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in selectAdminUserInfo : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}


	/**
	 * get administrator user Authority.
	 *
	 * @param adminId string adminId
	 * @param adminRule String adminRule
	 * @return ResultVO result data bean
	 */
	@Override
	public ResultVO getAuthority(String adminId, String adminRule) {

		ResultVO resultVO = new ResultVO();

		try {
			AdminUserVO re = adminUserDao.selectAdminUserAuthority(adminId, adminRule);

			Object[] o = new Object[0];
			resultVO.setData(o);
			if (re != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getAuthority : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	@Override
	public StatusVO updateOtpSecret(String adminId, String secret) throws Exception {
		StatusVO statusVO = new StatusVO();
		try {
			long result = adminUserDao.insertOrUpdateAdminSecret(adminId, secret);
			if (result > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("gcsp.result.update"));
			} else {
				throw new SQLException();
			}
		} catch	(Exception e) {
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
					MessageSourceHelper.getMessage("job.result.noupdate"));
		}

		return statusVO;
	}
}
