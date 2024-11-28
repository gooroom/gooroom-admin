package kr.gooroom.gpms.user.service.impl;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.user.service.UserReqService;
import kr.gooroom.gpms.user.service.UserReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service("userReqService")
public class UserReqServiceImpl implements UserReqService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Resource(name = "clientService")
    private ClientService clientService;

    @Resource(name = "jobService")
    private JobService jobService;

    @Resource(name = "userReqDAO")
    private UserReqDAO userReqDao;

    @Inject
    private CustomJobMaker jobMaker;

    /**
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @param userId String
     * @return ResultVO result object
     */
    @Override
    public ResultVO getUserReqList(String userId) {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<UserReqVO> re = userReqDao.selectUserReqList(userId);
            if (re != null && re.size() > 0) {

                UserReqVO[] row = re.toArray(UserReqVO[]::new);
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
            logger.error("error in getUserReqList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

        return resultVO;
    }

    /**
     * 사용자 USB 등록/삭제 요청 데이터
     *
     * @param reqSeq String
     * @return ResultVO result object
     */
    @Override
    public ResultVO getUserReqData(String reqSeq) {
        ResultVO resultVO = new ResultVO();

        try {
            UserReqVO re = userReqDao.selectUserReq(reqSeq);
            if (re != null) {

                UserReqVO[] row = new UserReqVO[1];
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
            logger.error("error in getUserReqList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

        return resultVO;
    }

     /**
     * 사용자 요청(USB 등록/삭제) 리스트
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result object
      */
    @Override
    public ResultPagingVO getUserReqListPaged(HashMap<String, Object> options) {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<UserReqVO> re = userReqDao.selectUserReqListPaged(options);
			long totalCount = userReqDao.selectUserReqListTotalCount(options);
			long filteredCount = userReqDao.selectUserReqListFilteredCount(options);

            if (re != null && re.size() > 0) {
                UserReqVO[] row = re.toArray(UserReqVO[]::new);
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
            logger.error("error in getUserReqListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

        return resultVO;
    }

    /**
     * 사용자 요청관리 로그
     * <p>
     * logging history data.
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result data bean
     */
    @Override
    public ResultPagingVO getUserReqActListPaged(HashMap<String, Object> options) {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<UserReqVO> re = userReqDao.selectUserReqActListPaged(options);
            long totalCount = userReqDao.selectUserReqActListTotalCount(options);
            long filteredCount = userReqDao.selectUserReqActListFilteredCount(options);

            if (re != null && re.size() > 0) {
                UserReqVO[] row = re.toArray(UserReqVO[]::new);
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
            logger.error("error in getUserReqActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

        return resultVO;
    }

    /**
     * 사용자 USB 등록/삭제 요청 승인 - array
     *
     * @param reqSeqs string[] target request seq array
     * @return StatusVO result status object
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public StatusVO approvalUserReq(String[] reqSeqs) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            for (String reqSeq : reqSeqs) {
                UserReqVO re = userReqDao.selectUserReq(reqSeq);
                re.setModUserId(LoginInfoHelper.getUserId());
                re.setStatus(re.getActionType().equals(GPMSConstants.ACTION_REGISTERING)
                        ? GPMSConstants.REQ_STS_USABLE : GPMSConstants.REQ_STS_REVOKE);
                re.setAdminCheck(re.getActionType().equals(GPMSConstants.ACTION_REGISTERING)
                        ? GPMSConstants.ACTION_REGISTER_APPROVAL : GPMSConstants.ACTION_UNREGISTER_APPROVAL);

                //1. 관리자 확인(추가/삭제 승인) 업데이트
                long reCnt = userReqDao.updateUserReq(re);
                userReqDao.updateUserReqStatus(re);
                if (reCnt > 0) {
                    // 2. 승인 이력 생성
                    re.setRegUserId(LoginInfoHelper.getUserId());
                    reCnt = userReqDao.createUserReqHist(re);
                    if (reCnt > 0) {
                        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                                MessageSourceHelper.getMessage("approvalUserReq.result.update"));
                    } else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                                MessageSourceHelper.getMessage("approvalUserReq.result.noupdate"));
                    }
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                            MessageSourceHelper.getMessage("approvalUserReq.result.noupdate"));
                }

                //3. 같은 시리얼로 '반려' 신청된 항목 있으면 상태(expire) 변경
                UserReqVO test = new UserReqVO();
                test.setUserId(re.getUserId());
                test.setUsbSerialNo(re.getUsbSerialNo());
                UserReqVO denySerial = userReqDao.selectDenyReqList(test);
                if (denySerial != null) {
                    denySerial.setStatus(GPMSConstants.REQ_STS_EXPIRE);
                    userReqDao.updateUserReqStatus(denySerial);
                }

                HashMap<String, String> map = new HashMap<>();
                if (re.getActionType().equals(GPMSConstants.ACTION_UNREGISTERING)) {
                    map.put("action", GPMSConstants.ACTION_UNREGISTER_APPROVAL);
                } else {
                    map.put("action", GPMSConstants.ACTION_REGISTER_APPROVAL);
                }
                map.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                map.put("login_id", re.getUserId());
                map.put("usb_name", re.getUsbName());
                map.put("usb_product", re.getUsbProduct());
                map.put("usb_vendor", re.getUsbVendor());
                map.put("usb_model", re.getUsbModel());
                map.put("usb_serial", re.getUsbSerialNo());
                map.put("req_seq", re.getReqSeq());

                ResultVO vo = clientService.getOnlineClientIdByClientId(re.getClientId());
                if (vo != null && vo.getData() != null && vo.getData().length > 0) {
                    String clientId = ((ClientVO) vo.getData()[0]).getClientId();
                    String[] clientIds = new String[1];
                    clientIds[0] = clientId;
                    if (clientId.equals(re.getClientId())) {
                        //4. 온라인 상태면 job으로 등록
                        jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
                    }
                }

                //5. 처리 결과 job으로 등록
                jobMaker.createJobForUserReq(re.getClientId(), map);
            }
        } catch (SQLException sqlEx) {
            logger.error("error in approvalUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            throw sqlEx;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("error in approvalUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
         }
        return statusVO;
    }

    /**
     * 사용자 USB 등록/삭제 요청 반려 - array
     *
     * @param reqSeqs string[] target request seq array
     * @return StatusVO result status object
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public StatusVO denyUserReq(String[] reqSeqs) throws Exception {

        StatusVO statusVO = new StatusVO();

        try {
            for (String reqSeq : reqSeqs) {
                UserReqVO re = userReqDao.selectUserReq(reqSeq);
                re.setModUserId(LoginInfoHelper.getUserId());
                re.setStatus(re.getActionType().equals(GPMSConstants.ACTION_REGISTERING)
                        ? GPMSConstants.REQ_STS_REVOKE : GPMSConstants.REQ_STS_USABLE);
                re.setAdminCheck(re.getActionType().equals(GPMSConstants.ACTION_REGISTERING)
                        ? GPMSConstants.ACTION_REGISTER_DENY : GPMSConstants.ACTION_UNREGISTER_DENY);

                //1. 관리자 확인(추가/삭제 반려) 업데이트
                long reCnt = userReqDao.updateUserReq(re);
                userReqDao.updateUserReqStatus(re);
                if (reCnt > 0) {
                    // 2. 반려 이력 생성
                    re.setRegUserId(LoginInfoHelper.getUserId());
                    reCnt = userReqDao.createUserReqHist(re);
                    if (reCnt > 0) {
                        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                                MessageSourceHelper.getMessage("denyUserReq.result.update"));
                    } else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                                MessageSourceHelper.getMessage("denyUserReq.result.noupdate"));
                    }
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                            MessageSourceHelper.getMessage("denyUserReq.result.noupdate"));
                }

                HashMap<String, String> map = new HashMap<>();
                if (re.getActionType().equals(GPMSConstants.ACTION_UNREGISTERING)) {
                    map.put("action", GPMSConstants.ACTION_UNREGISTER_DENY);
                } else {
                    map.put("action", GPMSConstants.ACTION_REGISTER_DENY);
                }
                map.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                map.put("login_id", re.getUserId());
                map.put("usb_name", re.getUsbName());
                map.put("usb_product", re.getUsbProduct());
                map.put("usb_vendor", re.getUsbVendor());
                map.put("usb_model", re.getUsbModel());
                map.put("usb_serial", re.getUsbSerialNo());
                map.put("req_seq", re.getReqSeq());

                ResultVO vo = clientService.getOnlineClientIdByClientId(re.getClientId());
                if (vo != null && vo.getData() != null && vo.getData().length > 0) {
                    String clientId = ((ClientVO) vo.getData()[0]).getClientId();
                    String[] clientIds = new String[1];
                    clientIds[0] = clientId;
                    if (clientId.equals(re.getClientId())) {
                        //2. 온라인 상태면 job으로 등록
                        jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
                    }
                }

                //3. 처리 결과 job으로 등록
                jobMaker.createJobForUserReq(re.getClientId(), map);
            }
        } catch (SQLException sqlEx) {
            logger.error("error in denyUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            throw sqlEx;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("error in denyUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }
        return statusVO;
    }

    /**
     * 사용자 USB 권한 회수
     *
     * @param reqSeq string target request seq
     * @return StatusVO result status object
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public StatusVO revokeUsbPermissionFromAdmin(String reqSeq) throws Exception {

        StatusVO statusVO = new StatusVO();
        try {
            UserReqVO re = userReqDao.selectUserReq(reqSeq);
            if (re != null) {
                re.setModUserId(LoginInfoHelper.getUserId());
                re.setStatus(GPMSConstants.REQ_STS_REVOKE);
                re.setAdminCheck(GPMSConstants.ACTION_REGISTER_APPROVAL_CANCEL);

                // 1. 권한 회수 후 요청 리스트에 등록
                long reCnt = userReqDao.updateUserReq(re);
                userReqDao.updateUserReqStatus(re);
                if (reCnt > 0) {
                    // 2. 권한회수 이력 생성
                    re.setRegUserId(LoginInfoHelper.getUserId());
                    reCnt = userReqDao.createUserReqHist(re);
                    if (reCnt > 0) {
                        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                                MessageSourceHelper.getMessage("revokeUserReq.result.update"));
                    } else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                                MessageSourceHelper.getMessage("revokeUserReq.result.noupdate"));
                    }
                } else {
	                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
			                MessageSourceHelper.getMessage("revokeUserReq.result.noinsert"));
                }

                HashMap<String, String> map = new HashMap<>();
                map.put("action", GPMSConstants.ACTION_REGISTER_APPROVAL_CANCEL);
                map.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                map.put("login_id", re.getUserId());
                map.put("usb_name", re.getUsbName());
                map.put("usb_product", re.getUsbProduct());
                map.put("usb_vendor", re.getUsbVendor());
                map.put("usb_model", re.getUsbModel());
                map.put("usb_serial", re.getUsbSerialNo());
                map.put("req_seq", re.getReqSeq());

                ResultVO vo = clientService.getOnlineClientIdByClientId(re.getClientId());
                if (vo != null && vo.getData() != null && vo.getData().length > 0 ) {
                    String clientId = ((ClientVO) vo.getData()[0]).getClientId();
                    String[] clientIds = new String[1];
                    clientIds[0] = clientId;

                    if(clientId.equals(re.getClientId())) {
                        //2. 온라인 상태면 job으로 등록
                        jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
                    }
                }

                //3. 처리 결과 job으로 등록
                jobMaker.createJobForUserReq(re.getClientId(), map);
            }
        } catch (SQLException sqlEx) {
            logger.error("error in revokeUsbPermissionFromAdmin : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            throw sqlEx;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("error in revokeUsbPermissionFromAdmin : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        return statusVO;
    }
}
