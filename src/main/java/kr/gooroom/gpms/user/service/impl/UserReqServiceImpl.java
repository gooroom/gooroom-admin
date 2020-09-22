package kr.gooroom.gpms.user.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.gooroom.gpms.client.service.ClientService;
import kr.gooroom.gpms.client.service.ClientVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.job.custom.CustomJobMaker;
import kr.gooroom.gpms.job.nodes.Job;
import kr.gooroom.gpms.job.service.JobService;
import kr.gooroom.gpms.job.service.JobVO;
import kr.gooroom.gpms.user.service.UserReqService;
import kr.gooroom.gpms.user.service.UserReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.StringWriter;
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
     * 사용자 요청(USB 등록/삭제) 리스트
     *
     * @param options HashMap<String, Object> option data
     * @return ResultPagingVO result object
     * @throws Exception
     */
    @Override
    public ResultPagingVO getUserReqListPaged(HashMap<String, Object> options) throws Exception {

        ResultPagingVO resultVO = new ResultPagingVO();

        try {
            List<UserReqVO> re = userReqDao.selectUserReqListPaged(options);
			long totalCount = userReqDao.selectUserReqListTotalCount(options);
			long filteredCount = userReqDao.selectUserReqListFilteredCount(options);

            if (re != null && re.size() > 0) {

                UserReqVO[] row = re.stream().toArray(UserReqVO[]::new);
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
            if (resultVO != null) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            }
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
        String modDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            UserReqVO userReqVo = new UserReqVO();
            userReqVo.setModUserId(LoginInfoHelper.getUserId());
            for (int i = 0; i < reqSeqs.length; i++) {
                List<UserReqVO> re = userReqDao.selectUserReq(reqSeqs[i]);
                UserReqVO[] row = re.stream().toArray(UserReqVO[]::new);
                userReqVo.setReqSeq(reqSeqs[i]);
                userReqVo.setModDt(modDt);
                if (row[0].getActionType().equals(GPMSConstants.ACTION_REGISTERING)) {
                    userReqVo.setAdminCheck(GPMSConstants.ACTION_REGISTER_APPROVAL);
                } else if (row[0].getActionType().equals(GPMSConstants.ACTION_UNREGISTERING)) {
                    userReqVo.setAdminCheck(GPMSConstants.ACTION_UNREGISTER_APPROVAL);
                }
                //1. 관리자 확인(추가/삭제 승인) 업데이트
                userReqDao.updateUserReq(userReqVo);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("action", row[0].getActionType());
                map.put("datetime", row[0].getModDt());
                map.put("login_id", row[0].getUserId());
                map.put("usb_name", row[0].getUsbName());
                map.put("usb_product", row[0].getUsbProduct());
                map.put("usb_size", row[0].getUsbSize());
                map.put("usb_vendor", row[0].getUsbVendor());
                map.put("usb_serial", row[0].getUsbSerialNo());

                ResultVO vo = clientService.getOnlineClientIdByClientId(row[0].getClientId());
                if (vo != null && vo.getData() != null && vo.getData().length > 0 ){
                    String clientId = ((ClientVO) vo.getData()[0]).getClientId();
                    String[] clientIds = new String[1];
                    clientIds[0] = clientId;
                    if(clientId.equals(row[0].getClientId())) {
                        //2. 온라인 상태면 job으로 등록
                        jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
                    }
                }

                //3. 처리 결과 job으로 등록
                jobMaker.createJobForUserReq(row[0].getClientId(), map);
            }
        } catch (SQLException sqlEx) {
            logger.error("error in approvalUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            if (statusVO != null) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            }
            throw sqlEx;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("error in approvalUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (statusVO != null) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            }
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
        String modDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            UserReqVO userReqVo = new UserReqVO();
            userReqVo.setModUserId(LoginInfoHelper.getUserId());
            for (int i = 0; i < reqSeqs.length; i++) {
                List<UserReqVO> re = userReqDao.selectUserReq(reqSeqs[i]);
                UserReqVO[] row = re.stream().toArray(UserReqVO[]::new);
                userReqVo.setReqSeq(reqSeqs[i]);
                userReqVo.setModDt(modDt);
                if (row[0].getActionType().equals(GPMSConstants.ACTION_REGISTERING)) {
                    userReqVo.setAdminCheck(GPMSConstants.ACTION_REGISTER_DENY);
                } else if (row[0].getActionType().equals(GPMSConstants.ACTION_UNREGISTERING)) {
                    userReqVo.setAdminCheck(GPMSConstants.ACTION_UNREGISTER_DENY);
                }
                //1. 관리자 확인(추가/삭제 반려) 업데이트
                userReqDao.updateUserReq(userReqVo);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("action", row[0].getActionType());
                map.put("datetime", row[0].getModDt());
                map.put("login_id", row[0].getUserId());
                map.put("usb_name", row[0].getUsbName());
                map.put("usb_product", row[0].getUsbProduct());
                map.put("usb_size", row[0].getUsbSize());
                map.put("usb_vendor", row[0].getUsbVendor());
                map.put("usb_serial", row[0].getUsbSerialNo());

                ResultVO vo = clientService.getOnlineClientIdByClientId(row[0].getClientId());
                if (vo != null && vo.getData() != null && vo.getData().length > 0 ){
                    String clientId = ((ClientVO) vo.getData()[0]).getClientId();
                    String[] clientIds = new String[1];
                    clientIds[0] = clientId;

                    if(clientId.equals(row[0].getClientId())) {
                        //2. 온라인 상태면 job으로 등록
                        jobMaker.createJobForClientSetupWithClients(GPMSConstants.JOB_MEDIA_RULE_CHANGE, null, clientIds);
                    }
                }

                //3. 처리 결과 job으로 등록
                jobMaker.createJobForUserReq(row[0].getClientId(), map);
            }
        } catch (SQLException sqlEx) {
            logger.error("error in denyUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), sqlEx.toString());
            if (statusVO != null) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            }
            throw sqlEx;
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("error in denyUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            if (statusVO != null) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            }
        }
        return statusVO;
    }
}
