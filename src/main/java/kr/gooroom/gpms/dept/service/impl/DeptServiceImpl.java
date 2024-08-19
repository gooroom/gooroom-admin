package kr.gooroom.gpms.dept.service.impl;

import java.sql.SQLException;
import java.util.*;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;

import kr.gooroom.gpms.common.service.ExcelCommonService;
import kr.gooroom.gpms.common.utils.CommonUtils;
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
import kr.gooroom.gpms.dept.service.DeptService;
import kr.gooroom.gpms.dept.service.DeptVO;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.user.service.UserVO;
import kr.gooroom.gpms.user.service.impl.UserDAO;

/**
 * @Class Name : UserServiceImpl.java
 * @Description :
 * @Modification Information
 *
 * @author
 * @since 2017-06-05
 * @version 1.0
 * @see
 * 
 */

@Service("deptService")
public class DeptServiceImpl implements DeptService {

	private static final Logger logger = LoggerFactory.getLogger(DeptServiceImpl.class);

	@Resource(name = "deptDAO")
	private DeptDAO deptDao;

	@Resource(name = "userDAO")
	private UserDAO userDao;

	@Resource(name = "excelCommonService")
	private ExcelCommonService excelCommonService;

	@Inject
	private CustomJobMaker jobMaker;

	/**
	 * 조직 트리에 하위 조직 리스트 조회
	 * 
	 * @param
	 * @return ResultVO
	 */
	@Override
	public ResultVO getChildrenDeptList(String deptCd, String hasWithRoot) {

		ResultVO resultVO = new ResultVO();

		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("deptCd", deptCd);

			if (GPMSConstants.DEFAULT_DEPTCD.equals(deptCd) || "0".equals(deptCd)) {
				String grRole = LoginInfoHelper.getUserGRRole();
				if (GPMSConstants.GRROLE_ADMIN.equals(grRole) || GPMSConstants.GRROLE_PART.equals(grRole)) {
					map.put("adminId", LoginInfoHelper.getUserId());
				}
			}

			List<DeptVO> re = deptDao.selectChildrenDeptListByAdmin(map);
			if (re != null && re.size() > 0) {
				DeptVO[] row = re.toArray(DeptVO[]::new);
				resultVO.setData(row);
				
				if(GPMSConstants.GUBUN_YES.equalsIgnoreCase(hasWithRoot)) {
					DeptVO rootInfo = deptDao.selectRootChildrenDeptInfo();
					resultVO.setExtend(new Object[] {rootInfo});
				}
				
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));
			} else {
				Object[] o = new Object[0];
				resultVO.setData(o);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}

		} catch (Exception ex) {
			logger.error("error in getChildrenDeptList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 트리에 하위의 모든 조직 리스트 조회
	 * 
	 * @param
	 * @return ResultVO
	 */
	@Override
	public ResultVO getAllChildrenDeptList(String deptCd) {

		ResultVO resultVO = new ResultVO();

		try {

			List<DeptVO> re = deptDao.selectAllChildrenDeptList(deptCd);

			if (re != null && re.size() > 0) {
				DeptVO[] row = re.toArray(DeptVO[]::new);
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
			logger.error("error in getAllChildrenDeptList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 수정
	 * 
	 * @param deptVO
	 * @return StatusVO
	 */
	@Override
	public StatusVO updateDeptData(DeptVO deptVO) {

		StatusVO statusVO = new StatusVO();

		try {
			deptVO.setModUserId(LoginInfoHelper.getUserId());

			// update dept master
			long reCnt = deptDao.updateDeptData(deptVO);

			// update dept config(rules)
			if (reCnt > 0) {
				long cnt = -1;
				String cfgId = deptVO.getBrowserRuleId();

				// 브라우져설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_BROWSERRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_BROWSERRULE);
				}

				cnt = -1;
				cfgId = deptVO.getMediaRuleId();
				// 매체제어설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_MEDIARULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_MEDIARULE);
				}

				cnt = -1;
				cfgId = deptVO.getSecurityRuleId();
				// 단말보안설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_SECURITYRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_SECURITYRULE);
				}

				cnt = -1;
				cfgId = deptVO.getFilteredSoftwareRuleId();
				// filtered software rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_FILTEREDSOFTWARE);
				}

				cnt = -1;
				cfgId = deptVO.getCtrlCenterItemRuleId();
				// control center item
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_CTRLCENTERITEMRULE);
				}

				cnt = -1;
				cfgId = deptVO.getPolicyKitRuleId();
				// policy kit
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_POLICYKITRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_POLICYKITRULE);
				}

				cnt = -1;
				cfgId = deptVO.getDesktopConfId();
				// 데스크톱설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(deptVO.getDeptCd(), deptVO.getModUserId(), cfgId,
							GPMSConstants.TYPE_DESKTOPCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				} else {
					cnt = deptDao.deleteConfigWithDept(deptVO.getDeptCd(), GPMSConstants.TYPE_DESKTOPCONF);
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");

			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 수정되지 않았습니다.");
			}

		} catch (Exception ex) {
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "ERR999", "시스템 오류가 발생하였습니다.");
		}

		return statusVO;
	}

	/**
	 * 하위 조직 만료일 정보 수정
	 * 
	 * @param deptVO
	 * @return StatusVO
	 */
	@Override
	public StatusVO updateChildrenDeptExpireData(DeptVO deptVO) {
		StatusVO statusVO = new StatusVO();
		try {
			deptVO.setModUserId(LoginInfoHelper.getUserId());

			// update dept master
			long reCnt = deptDao.updateChildrenDeptExpireData(deptVO);
			if (reCnt > 0) {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "하위 조직 정보가 수정되었습니다.");
			} else {
				// 수정이 필요 없는 경우도 성공으로 처리해야함
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "하위 조직 정보가 수정되지 않았습니다.");
			}
		} catch (Exception ex) {
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "ERR999", "시스템 오류가 발생하였습니다.");
		}

		return statusVO;
	}

	/**
	 * 조직 정책 수정
	 * @param deptCds
	 * @param browserRuleId
	 * @param mediaRuleId
	 * @param securityRuleId
	 * @param filteredSoftwareRuleId
	 * @param ctrlCenterItemRuleId
	 * @param policyKitRuleId
	 * @param desktopConfId
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateRuleInfoToMultiDept(String[] deptCds, String browserRuleId, String mediaRuleId,
			String securityRuleId, String filteredSoftwareRuleId, String ctrlCenterItemRuleId, String policyKitRuleId,
			String desktopConfId) {

		StatusVO statusVO = new StatusVO();

		try {
			if (deptCds != null && deptCds.length > 0) {
				String modUserId = LoginInfoHelper.getUserId();
				for (String deptCd : deptCds) {

					long cnt = -1;
					// 브라우져설정
					if (browserRuleId != null && !"".equals(browserRuleId)) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, browserRuleId,
								GPMSConstants.TYPE_BROWSERRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_BROWSERRULE);
					}

					cnt = -1;
					// 매체제어설정
					if (mediaRuleId != null && mediaRuleId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, mediaRuleId,
								GPMSConstants.TYPE_MEDIARULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_MEDIARULE);
					}

					cnt = -1;
					// 단말보안설정
					if (securityRuleId != null && securityRuleId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, securityRuleId,
								GPMSConstants.TYPE_SECURITYRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						cnt = deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_SECURITYRULE);
					}

					cnt = -1;
					// filtered software rule
					if (filteredSoftwareRuleId != null && filteredSoftwareRuleId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, filteredSoftwareRuleId,
								GPMSConstants.TYPE_FILTEREDSOFTWARE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						cnt = deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_FILTEREDSOFTWARE);
					}

					cnt = -1;
					// control center item
					if (ctrlCenterItemRuleId != null && ctrlCenterItemRuleId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, ctrlCenterItemRuleId,
								GPMSConstants.TYPE_CTRLCENTERITEMRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						cnt = deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					}

					cnt = -1;
					// policy kit
					if (policyKitRuleId != null && policyKitRuleId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, policyKitRuleId,
								GPMSConstants.TYPE_POLICYKITRULE);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						cnt = deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_POLICYKITRULE);
					}

					cnt = -1;
					// 데스크톱설정
					if (desktopConfId != null && desktopConfId.length() > 0) {
						cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, modUserId, desktopConfId,
								GPMSConstants.TYPE_DESKTOPCONF);
						if (cnt < 0) {
							throw new SQLException();
						}
					} else {
						cnt = deptDao.deleteConfigWithDept(deptCd, GPMSConstants.TYPE_DESKTOPCONF);
					}
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");

			} else {
				// error, no exist deptCd
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 수정되지 않았습니다.");
			}

		} catch (Exception ex) {

			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in updateRuleInfoToMultiDept : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}

		return statusVO;
	}

	/**
	 * 조직 연동 설정 수정
	 * @param deptCd
	 * @param cfgId
	 * @param confType
	 * @return
	 */
	@Override
	public StatusVO updateDeptConf(String deptCd, String cfgId, String confType) {
		StatusVO statusVO = new StatusVO();
		try {
			long cnt = -1;
			if (cfgId != null && cfgId.length() > 0) {
				cnt = deptDao.insertOrUpdateConfigWithDept(deptCd, LoginInfoHelper.getUserId(), cfgId, confType);
				if (cnt < 0) {
					throw new SQLException();
				}
			} else {
				cnt = deptDao.deleteConfigWithDept(deptCd, confType);
			}
			statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 수정되었습니다.");

		} catch (Exception ex) {
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "ERR999", "시스템 오류가 발생하였습니다.");
		}

		return statusVO;
	}

	/**
	 * 조직 아이디 중복 검사
	 * @param deptCd
	 * @return
	 */
	@Override
	public StatusVO isExistDeptCd(String deptCd) {
		StatusVO statusVO = new StatusVO();
		try {
			boolean re = deptDao.isExistDeptCd(deptCd);
			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("org.result.duplicate"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("org.result.noduplicate"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * 신규 조직 정보 등록
	 * @param vo
	 * @return
	 */
	@Override
	public StatusVO createDeptData(DeptVO vo) {

		StatusVO statusVO = new StatusVO();

		try {

			vo.setModUserId(LoginInfoHelper.getUserId());
			vo.setRegUserId(LoginInfoHelper.getUserId());

			// create dept master
			long resultCnt = deptDao.createDeptMaster(vo);

			// create dept config(rule).
			if (resultCnt > 0) {

				long cnt = -1;
				String cfgId = vo.getBrowserRuleId();
				// 브라우져설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_BROWSERRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getMediaRuleId();
				// 매체제어설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_MEDIARULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getSecurityRuleId();
				// 단말보안설정
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_SECURITYRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getFilteredSoftwareRuleId();
				// filtered software rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_FILTEREDSOFTWARE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getCtrlCenterItemRuleId();
				// control center item rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_CTRLCENTERITEMRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getPolicyKitRuleId();
				// policy kit rule
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_POLICYKITRULE);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				cnt = -1;
				cfgId = vo.getDesktopConfId();
				// 데스크톱정보
				if (cfgId != null && cfgId.length() > 0) {
					cnt = deptDao.insertOrUpdateConfigWithDept(vo.getDeptCd(), vo.getRegUserId(), cfgId,
							GPMSConstants.TYPE_DESKTOPCONF);
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				// insert ADMIN_CLIENTGRP : if group's upper group id is 'CGRPDEFAULT'
				cnt = -1;
				if (GPMSConstants.DEFAULT_DEPTCD.equals(vo.getUprDeptCd())) {
					cnt = deptDao.insertDeptInAdminRelation(vo.getDeptCd(), vo.getRegUserId());
					if (cnt < 0) {
						throw new SQLException();
					}
				}

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
						MessageSourceHelper.getMessage("org.result.insert"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
						MessageSourceHelper.getMessage("org.result.noinsert"));
			}

		} catch (Exception ex) {
			logger.error("error in createDeptData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * 하위 포함하여 조직을 미허용으로 수정
	 * @param deptCd
	 * @return
	 */
	@Override
	public StatusVO updateDeptUnusedWithChildren(String deptCd) {

		StatusVO statusVO = new StatusVO();
		DeptVO deptVO = new DeptVO();

		try {
			deptVO.setDeptCd(deptCd);
			deptVO.setModUserId(LoginInfoHelper.getUserId());

			long reCnt = deptDao.updateDeptUnusedWithChildren(deptVO);
			if (reCnt > 0) {

				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
						MessageSourceHelper.getMessage("org.result.updatenotuse"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
						MessageSourceHelper.getMessage("org.result.noupdatenotuse"));
			}

		} catch (Exception ex) {
			logger.error("error in updateDeptUnusedWithChildren : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * 하위 포함하여 조직 정보 삭제
	 * @param deptCd
	 * @return
	 */
	@Override
	public StatusVO deleteDeptWithChildren(String deptCd) {

		StatusVO statusVO = new StatusVO();
		DeptVO deptVO = new DeptVO();

		try {
			deptVO.setDeptCd(deptCd);
			deptVO.setModUserId(LoginInfoHelper.getUserId());

			List<DeptVO> re = deptDao.selectAllChildrenDeptList(deptCd);
			if (re != null && re.size() > 0) {

				DeptVO[] deptVOs = re.toArray(DeptVO[]::new);
				if (deptVOs != null && deptVOs.length > 0) {
					String[] depdCds = new String[deptVOs.length];
					for (int i = 0; i < depdCds.length; i++) {
						depdCds[i] = deptVOs[i].getDeptCd();
					}

					// 사용자 정보에서 조직 정보 제거
					userDao.removeUserInDeptList(depdCds);

					// 조직 테이블에서 조직 삭제
					// deptDao.deleteDeptData(depdCds);
					long reCnt = deptDao.deleteDeptWithChildren(deptVO);
					if (reCnt > 0) {

						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
								MessageSourceHelper.getMessage("org.result.delete"));
					} else {
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
								MessageSourceHelper.getMessage("org.result.nodelete"));
					}

				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
							MessageSourceHelper.getMessage("org.result.nodelete"));
				}

			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DELETEERROR,
						MessageSourceHelper.getMessage("org.result.nodelete"));
			}

		} catch (Exception ex) {
			logger.error("error in deleteDeptWithChildren : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * delete dept array
	 * 
	 * @param deptCds string[] target dept cd array
	 * @return StatusVO result status object
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO deleteDeptList(String[] deptCds, String isDeleteUser) throws Exception {

		StatusVO statusVO = new StatusVO();

		try {

			// 1. get all dept id - child
			String[] newDeptCds = deptCds;
			List<DeptVO> re = deptDao.selectAllChildrenDeptListByParents(deptCds);
			if (re != null && re.size() > 0) {
				newDeptCds = new String[re.size()];
				for (int i = 0; i < re.size(); i++) {
					newDeptCds[i] = re.get(i).getDeptCd();
				}
			}

			// 1-1. get client list from job and revoke
			List<UserVO> userRe = userDao.selectUserListInDept(newDeptCds);
			String[] newUserIds = new String[userRe.size()];
			for (int i = 0; i < userRe.size(); i++) {
				newUserIds[i] = userRe.get(i).getUserId();
			}

			// DELETE DEPT
			String regUserId = LoginInfoHelper.getUserId();
			for (String newDeptCd : newDeptCds) {
				// 2. insert history
				long histRe = deptDao.createDeptHist("DELETE", newDeptCd, regUserId);
				if (histRe > 0) {
					// 3. delete master
					long mstrRe = deptDao.deleteDeptData(newDeptCd);
					if (mstrRe > 0) {

						// DELETE DEPT IN Relation tables
						// 4. delete rule config
						deptDao.deleteDeptFromRule(newDeptCd);
						// 5. delete admin config
						deptDao.deleteDeptForAdmin(newDeptCd);
						// 6. delete notify config
						deptDao.deleteDeptForNoti(newDeptCd);
					} else {
						throw new SQLException();
					}
				} else {
					throw new SQLException();
				}
			}

			if (newUserIds != null && newUserIds.length > 0) {
				deptDao.updateDeptToUser(GPMSConstants.DEFAULT_DEPTCD, newUserIds);
				if ("Y".equalsIgnoreCase(isDeleteUser)) {
					for (String newUserId : newUserIds) {
						// ##. insert history
						long histRe = userDao.createUserHist("DELETE", newUserId, regUserId);
						if (histRe > 0) {
							// ##. delete user mstr
							long deleteRe = userDao.deleteUserDataById(newUserId, regUserId);
							if (deleteRe > 0) {
								// ##. delete rule config
								userDao.deleteUserFromRule(newUserId);
								// ##. delete notify config
								userDao.deleteUserForNoti(newUserId);
							} else {
								throw new SQLException();
							}
						} else {
							throw new SQLException();
						}
					}
				} else {
					// create JOB for user config(rule)
					// DeptVO defaultDeptVO = deptDao.selectDeptData(GPMSConstants.DEFAULT_DEPTCD);
					jobMaker.createJobForUseRuleByChangeDept(newUserIds);
				}
			}

			// RESULT
			statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_DELETE,
					MessageSourceHelper.getMessage("clientgroup.result.delete"));

		} catch (SQLException sqlEx) {
			logger.error("error in deleteDeptList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			throw sqlEx;
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			logger.error("error in deleteDeptList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
		}

		return statusVO;
	}

	/**
	 * 조직 정보 조회
	 * 
	 * @param
	 * @return ResultVO
	 */
	@Override
	public ResultVO getDeptData(String deptCd) {
		ResultVO resultVO = new ResultVO();
		try {
			DeptVO re = deptDao.selectDeptData(deptCd);
			if (re != null) {
				DeptVO[] row = new DeptVO[1];
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
			logger.error("error in getDeptData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * generate dept information data list
	 * 
	 * @param deptCds string target dept cd array
	 * @return ResultVO result object
	 */
	@Override
	public ResultVO readDeptNodeList(String[] deptCds) {
		ResultVO resultVO = new ResultVO();
		try {
			List<DeptVO> re = deptDao.selectDeptNodeList(deptCds);

			if (re != null && re.size() > 0) {
				DeptVO[] row = re.toArray(DeptVO[]::new);
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
			logger.error("error in readDeptNodeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (resultVO != null) {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
			}
		}

		return resultVO;
	}

	/**
	 * 조직 리스트 조회 - 페이징
	 * 
	 * @param options HashMap<String, Object> option data
	 * @return ResultVO
	 */
	@Override
	public ResultPagingVO getDeptListPaged(HashMap<String, Object> options) {
		ResultPagingVO resultVO = new ResultPagingVO();

		try {

			List<DeptVO> re = deptDao.selectDeptListPaged(options);
			long totalCount = deptDao.selectDeptListTotalCount(options);
			long filteredCount = deptDao.selectDeptListFilteredCount(options);

			if (re != null && re.size() > 0) {
				DeptVO[] row = re.toArray(DeptVO[]::new);
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
			logger.error("error in getDeptListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 일괄 등록 체크(파일자료)
	 *
	 * @param dataList List<List<String>> dataList
	 * @return StatusVO
	 */
	@Override
	public ResultVO isCanUpdateDeptDataFromFile(List<List<String>> dataList) {

		ResultVO resultVO = new ResultVO();

		List<DeptVO> deptList = new ArrayList<>();
		try {

			// excel head list check
			StatusVO statusVO = isDeptHeadListExist(dataList.get(0));
			if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(statusVO.getResult())) {
				if (resultVO != null) {
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
							"조직 정보 파일에 항목이 존재하지 않습니다."));
				}
				return resultVO;
			}
			dataList.remove(0);  //head list 삭제

			// set base dept
			deptList.add((DeptVO) getDeptData("0").getData()[0]);
			deptList.add((DeptVO) getDeptData("DEPTDEFAULT").getData()[0]);

			if(dataList.size() > 0) {
				for (List<String> list : dataList) {
					// required data check
					statusVO = isRequiredDataExist(list);
					if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(statusVO.getResult())) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"조직 필수정보가 입력되지 않았습니다."));
						}
						return resultVO;
					}

					// start basic logic
					DeptVO deptVO = new DeptVO(list.get(0), list.get(1), list.get(3));
					// deptcd regex check
					if (!excelCommonService.deptCdRegex((deptVO.getDeptCd()))) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"조직 코드 형식이 맞지 않습니다."));
						}
						return resultVO;
					}

					// uprdeptcd regetx check
					if (!excelCommonService.deptCdRegex(deptVO.getUprDeptCd())) {
						if (resultVO != null) {
							resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
									"조직 코드 형식이 맞지 않습니다."));
						}
						return resultVO;
					} else {
						if (deptVO.getUprDeptCd().equals("")) {  //상위조직 존재안할때
							deptVO.setUprDeptCd("DEPTDEFAULT");
						}
					}

					// search uprDeptVO from deptList
					DeptVO uprDeptVO = null;
					for (DeptVO searchDeptVO : deptList) {
						if (searchDeptVO.getDeptCd().equals(deptVO.getUprDeptCd())) {
							uprDeptVO = searchDeptVO;
						}

						// check dept cd duplicate
						if (deptVO.getDeptCd().equals(searchDeptVO.getDeptCd())) {
							if (resultVO != null) {
								resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
										"조직코드가 중복되었습니다."));
							}
							return resultVO;
						}

						// check dept nm duplicate
						if (deptVO.getDeptNm().equals(searchDeptVO.getDeptNm())) {
							if (resultVO != null) {
								resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
										"조직명이 중복되었습니다."));
							}
							return resultVO;
						}
					}

					if (uprDeptVO == null) {  // 입력된 상위조직 존재안할때
						deptVO.setUprDeptCd("DEPTDEFAULT");
						uprDeptVO = (DeptVO) getDeptData("DEPTDEFAULT").getData()[0];
					}

					// set expire date String -> Date
					if (CommonUtils.validationDate(list.get(2))) {
						deptVO.setExpireDate(CommonUtils.yyyyMMddToDate(list.get(2)));
						// 상위조직 만료일보다 뒤일때
						if (uprDeptVO.getExpireDate().compareTo(deptVO.getExpireDate()) < 0) {
							deptVO.setExpireDate(uprDeptVO.getExpireDate());
						}
					} else {
						if (!list.get(2).equals("")) {
							if (resultVO != null) {
								resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
										"조직 만료일 형식이 맞지 않습니다."));
							}
							return resultVO;
						}
					}

					//merget whle
					deptVO.setDeptLevel(String.valueOf(Integer.parseInt(uprDeptVO.getDeptLevel()) + 1));
					deptVO.setWhleDeptCd(uprDeptVO.getWhleDeptCd() + deptVO.getDeptCd() + ";");

					//add to dpetlist
					deptList.add(deptVO);
				}

				if (deptList != null && deptList.size() > 0) {
					DeptVO[] row = deptList.toArray(DeptVO[]::new);
					resultVO.setData(row);
					resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, "GRSM0000",
							"파일데이터확인 완료"));
				}

			} else {
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR, "저장할 내용이 없음"));
			}

		} catch (Exception ex) {
			logger.error("error in isCanDeptDataFromFile : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}

		return resultVO;
	}

	/**
	 * 조직 정보 일괄 등록 (파일자료)
	 * @param deptVOs DeptVOs
	 * @return
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StatusVO updateDeptDataFromFile(List<DeptVO> deptVOs) {

		StatusVO statusVO = new StatusVO();

		try {
			// delete old dept cd and userList in dept
			List<DeptVO> depts = deptDao.selectAllChildrenDeptList("0");
			List<String> willDeleteDept = new ArrayList<>();
			boolean isExist = false;
			for(DeptVO oldDeptVO : depts) {
				for(DeptVO newDeptVO : deptVOs) {
					if(oldDeptVO.getDeptCd().equals(newDeptVO.getDeptCd())) {
						isExist = true;
						break;
					}
				}
				if(!isExist) {
					willDeleteDept.add(oldDeptVO.getDeptCd());
				}
				isExist = false;
			}
			if(willDeleteDept.size() > 0) {
				deleteDeptList(willDeleteDept.toArray(new String[0]), GPMSConstants.GUBUN_NO);
			}

			// main logic
			if (deptVOs != null && deptVOs.size() > 0) {

				// remove all data
				boolean isDelete = deptDao.deleteDeptAll();
				if (isDelete) {
					boolean reFlag = true;
					for (DeptVO vo : deptVOs) {
						vo.setRegUserId(LoginInfoHelper.getUserId());
						long re = deptDao.createDeptRawData(vo);
						if (re < 1) {
							reFlag = false;
							break;
						}
					}

					if (reFlag) {
						statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, "GRSM0000", "조직 정보가 등록되었습니다.");
					} else {
						statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 등록되지 않았습니다.[1]");
						throw new SQLException();
					}
				} else {
					statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 등록되지 않았습니다.[2]");
					throw new SQLException();
				}
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "GRSM4121", "조직 정보가 등록되지 않았습니다.[3]");
			}

		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

			statusVO.setResultInfo(GPMSConstants.MSG_FAIL, "ERR999", "시스템 오류가 발생하였습니다.");
		}

		return statusVO;
	}

	/**
	 * 조직 정보 일괄 다운로드
	 *
	 * @return XSSFWorkbook
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public XSSFWorkbook createDeptFileFromData() {

		try {
            List<DeptVO> re = deptDao.selectAllChildrenDeptList("DEPTDEFAULT"); //최상위부터 전체 조직 리스트
			List<List<String>> excleWriteList = new ArrayList<>();

			// sort dept level
			re.sort(Comparator.comparingInt(DeptVO::getDeptLevelInt));

			//title
			excleWriteList.add(Arrays.asList("조직아이디",
					"조직이름",
					"조직만료일",
					"상위조직아이디"));

			//contents
			for(DeptVO deptVO : re) {
				if(!deptVO.getDeptCd().equals("DEPTDEFAULT")) {//DEPTDEFAULT 제외
					excleWriteList.add(Arrays.asList(
							deptVO.getDeptCd(),
							deptVO.getDeptNm(),
							CommonUtils.convertDataToString(deptVO.getExpireDate()),
							deptVO.getUprDeptCd()
					));
				}
			}

			return excelCommonService.write(excleWriteList);

        } catch (Exception ex) {
            logger.error("error in createDeptDFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }

		return new XSSFWorkbook();
	}

	/**
	 * 조직 정보 업로드 샘플파일 다운로드
	 *
	 * @return XSSFWorkbook
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public XSSFWorkbook createDeptSampleFileFromData() {

		try {
			List<List<String>> excleWriteList = new ArrayList<>();

			//title
			excleWriteList.add(Arrays.asList("조직아이디",
					"조직이름",
					"조직만료일",
					"상위조직아이디"));

			return excelCommonService.write(excleWriteList);

		} catch (Exception ex) {
			logger.error("error in createDeptSampleFileFromData : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return new XSSFWorkbook();
	}

	/**
	 * 조직 일괄등록 항목 리스트 존재하는지 확인
	 * @param headList
	 * @return
	 */
	@Override
	public StatusVO isDeptHeadListExist(List<String> headList) {
		StatusVO statusVO = new StatusVO();
		boolean re = true;
		try {
			//title
			String[] excelHeadList = {"조직아이디",
					"조직이름",
					"조직만료일",
					"상위조직아이디"};

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
			logger.error("error in isHeadListExist : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	@Override
	public StatusVO isRequiredDataExist(List<String> rowData) {
		StatusVO statusVO = new StatusVO();
		try {

			for(int idx=0;idx<4;idx++) {
				if(idx == 2 || idx == 3) {
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

	@Override
	public StatusVO isExistDeptNameByParentCd(String parentDeptCd, String deptName) {
		StatusVO statusVO = new StatusVO();
		try {
			boolean re = deptDao.isExistDeptNameByParentCd(parentDeptCd, deptName);
			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("org.result.duplicatename"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("org.result.noduplicatename"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistDeptNameByParentCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

	@Override
	public StatusVO isExistDeptNameByDeptCd(String deptCd, String deptName) {
		StatusVO statusVO = new StatusVO();
		try {
			boolean re = deptDao.isExistDeptNameByDeptCd(deptCd, deptName);
			if (re) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_DUPLICATE,
						MessageSourceHelper.getMessage("org.result.duplicatename"));
			} else {
				statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_NODUPLICATE,
						MessageSourceHelper.getMessage("org.result.noduplicatename"));
			}
		} catch (Exception ex) {
			logger.error("error in isExistDeptNameByDeptCd : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			if (statusVO != null) {
				statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
						MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
			}
		}
		return statusVO;
	}

}
