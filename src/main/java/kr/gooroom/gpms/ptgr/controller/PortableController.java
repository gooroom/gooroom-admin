package kr.gooroom.gpms.ptgr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.gkm.utils.CertificateUtils;
import kr.gooroom.gpms.gkm.utils.CertificateVO;
import kr.gooroom.gpms.gkm.utils.FileUtils;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.*;
import kr.gooroom.gpms.ptgr.util.JenkinsJob;
import kr.gooroom.gpms.ptgr.util.JenkinsUtils;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

@Controller
@RequestMapping("/portable")
public class PortableController {

    private static final Logger logger = LoggerFactory.getLogger(PortableController.class);

    private final String serverAPI = GPMSConstants.PORTABLE_SERVER_API + "updateImageList";
    private final String certDeleteAPI = GPMSConstants.PORTABLE_SERVER_API + "removeCert";
    private final String updateDataAPI = GPMSConstants.PORTABLE_SERVER_API + "updateData";

    private final ArrayList<String> list = new ArrayList<>();
    private final SynchronousQueue<String> queue = new SynchronousQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final CompletionHandler<StatusVO, PortableVO> callback;
    private final JenkinsUtils jenkinsUtils = new JenkinsUtils();

    @Resource(name = "portableService")
    private PortableService portableService;

    @Resource(name = "portableImageService")
    private PortableImageService portableImageService;

    @Resource(name = "portableCertService")
    private PortableCertService portableCertService;

    @Resource(name = "portableLogService")
    private PortableLogService portableLogService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "portableJobService")
    private PortableJobService portableJobService;

    public PortableController() {
        callback = new CompletionHandler<StatusVO, PortableVO>() {
            @Override
            public void completed(StatusVO statusVO, PortableVO portableVO) {
                try {
                    if (statusVO.getResult() == GPMSConstants.MSG_SUCCESS) {
                        //이미지 신청 상태 변경
                        HashMap<String,Object> options = new HashMap<>();
                        options.put("imageId", portableVO.getImageId());
                        ResultVO imageRet = portableImageService.readImageDataById(portableVO.getImageId());
                        ResultVO certRet = portableCertService.readCertDataByCertId(Integer.toString(portableVO.getCertId()));
                        if (imageRet.getData() == null || certRet.getData() == null) {
                            String msg = "completed: No image table information";
                            int logId = createLog(portableVO, GPMSConstants.CODE_NODATA, msg, PortableConstants.LOG_WARN);
                            portableVO.setLogId(logId);
                            portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REAPPROVE);
                            portableService.updatePortableData(portableVO);
                            return;
                        }
                        PortableImageVO portableImageVO = (PortableImageVO)imageRet.getData()[0];
                        //기존 JOB 정보 제거
                        portableJobService.deleteJobDataByImageId(portableImageVO.getImageId());
                        portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                        portableImageService.updateImageData(portableImageVO);
                        //승인 상태 변경
                        portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE);
                        //빌드 전달 상태 변경
                        portableVO.setBuildStatus(PortableConstants.STATUS_BUILD_REQUEST);
                        portableService.updatePortableData(portableVO);
                        //인증서 전달 시간
                        PortableCertVO portableCertVO = (PortableCertVO)certRet.getData()[0];
                        portableCertVO.setTransferDt(new Date());
                        portableCertService.updateCertData(portableCertVO);
                        //빌드 서버 요청
                        callJenkins(portableVO, portableCertVO);
                        logger.debug("portable approve success : {}", statusVO.getMessage());
                    } else {
                        String msg = "completed: fail " + statusVO.getMessage();
                        int logId = createLog(portableVO, statusVO.getResultCode(), msg, PortableConstants.LOG_WARN);
                        portableVO.setStatusCd(PortableConstants.LOG_WARN);
                        portableVO.setLogId(logId);
                        portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REAPPROVE);
                        portableService.updatePortableData(portableVO);
                        logger.debug("portable approve fail: {}", statusVO.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable throwable, PortableVO portableVO) {
                try {
                    String msg = "failed:  " + throwable.getMessage();
                    int logId = createLog(portableVO, PortableConstants.LOG_ERROR, msg, PortableConstants.LOG_ERROR);
                    portableVO.setStatusCd(PortableConstants.LOG_ERROR);
                    portableVO.setLogId(logId);
                    portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REAPPROVE);
                    portableService.updatePortableData(portableVO);
                    logger.debug("portable approve fail: {}", throwable.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 사용자 아이디 중복 체크
     *
     * @return ResultVO
     */
    @PostMapping (value = "/checkUserId")
    @ResponseBody
    public ResultVO checkUserIdInList(HttpServletRequest req, HttpServletResponse res)  {

        String[] ids = req.getParameter("ids").split(",");
        ResultVO resultVO = new ResultVO();
        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("userId", ids);
            resultVO = portableService.isNoExistInUserIdList(options);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }

        return resultVO;
    }

    /**
     * 휴대형구름 일괄 등록
     *
     * @param portableListVO
     * @return ResultVO
     */
    @PostMapping(value = "/registerPortableDataList")
    @ResponseBody
    public ResultVO registerPortableDataList (@ModelAttribute  PortableListVO portableListVO) {

        ResultVO resultVO = new ResultVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        List<PortableVO> ptgrListVO =  portableListVO.getPortableListVO();

        if (ptgrListVO.size() == 0) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        ArrayList<PortableVO> ptgrTemps = new ArrayList<>();
        try
        {
            for (PortableVO vo : ptgrListVO) {
                StatusVO statusVO = null;
                //사용자 등록
                UserVO userVO;
                resultVO = userService.readUserData(vo.getUserId());
                if (resultVO.getData() == null || resultVO.getData().length == 0) {

                    userVO = new UserVO();
                    userVO.setUserId(vo.getUserId());
                    userVO.setUserPasswd(vo.getUserPw());
                    userVO.setDeptCd(GPMSConstants.PORTABLE_GROUP);
                    userVO.setUserNm(vo.getUserNm());
                    userVO.setUserEmail(vo.getNotiEmail());
                    userVO.setPasswordStatus(GPMSConstants.STS_NORMAL_USER);
                    userVO.setDesktopConfId(GPMSConstants.PORTABLE_DESKTOP);
                    userVO.setCtrlCenterItemRuleId(GPMSConstants.PORTABLE_CTRL);
                    statusVO = userService.createUserDataWithRule(userVO, true);

                    if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                        resultVO.setStatus(statusVO);
                        logger.debug("not create user: {}", userVO.getUserId());
                        return resultVO;
                    }
                }
                else {
                    userVO = (UserVO)resultVO.getData()[0];
                }

                //이미지 테이블 생성
                int imageId;
                imageId = portableImageService.readNextImageDataIndex();
                PortableImageVO portableImageVO = new PortableImageVO();
                portableImageVO.setImageId(imageId);
                portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_REQUEST);
                statusVO = portableImageService.createImageData(portableImageVO);

                if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                    resultVO.setStatus(statusVO);
                    userService.deleteUserData(userVO.getUserId());
                    logger.debug("not create image table: {}", imageId);
                    return resultVO;
                }
                vo.setImageId(imageId);

                //인증서 테이블 생성
                int certId;
                certId = portableCertService.readNextCertDataIndex();
                PortableCertVO portableCertVO = new PortableCertVO();
                portableCertVO.setCertId(certId);
                portableCertVO.setPublish(0);
                statusVO = portableCertService.createCertData(portableCertVO);

                if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                    resultVO.setStatus(statusVO);
                    userService.deleteUserData(userVO.getUserId());
                    portableImageService.deleteImageDataByImageId(imageId);
                    logger.debug("not create cert table: {}", certId);
                    return resultVO;
                }
                vo.setCertId(certId);

                int ptgrId;
                ptgrId = portableService.readNextPortableDataIndex();
                vo.setPtgrId(ptgrId);
                vo.setApproveStatus(PortableConstants.STATUS_APPROVE_REQUEST);
                statusVO = portableService.createPortableData(vo);

                if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                    resultVO.setStatus(statusVO);
                    userService.deleteUserData(userVO.getUserId());
                    portableImageService.deleteImageDataByImageId(imageId);
                    portableCertService.deleteCertDataByCertId(certId);
                    logger.debug("not create portable table: {}", ptgrId);
                    return resultVO;
                }
                ptgrTemps.add(vo);
            }

            ptgrTemps.forEach((ptgr)->{
                asyncPortableApprove(Integer.toString(ptgr.getPtgrId()));
            });

            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_CREATE,
                    MessageSourceHelper.getMessage("portable.result.registerall")));
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }

        return resultVO;
    };

    /**
     * 휴대형구름 정보
     *
     * @param adminId
     * @return ResultVO
     */
    @PostMapping (value = "/readPortableDataList")
    @ResponseBody
    public ResultVO getPortableDataList (
            @RequestParam(value = "adminId", required = false) String adminId,
            @RequestParam(value = "userId", required = false) String userId) {
        ResultVO resultVO = new ResultVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            if (adminId != null && !adminId.isEmpty())
                options.put("adminId", adminId);
            if (userId != null && !userId.isEmpty())
                options.put("userId", userId);

            if (options.size() == 0) {
                resultVO = portableService.readPortableView();
            }
            else {
                resultVO = portableService.readPortableDataById(options);
            }
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }

        return resultVO;
    };

    /**
     * 휴대형구름 정보 (페이지)
     *
     * @return ResultVO
     */
    @PostMapping (value = "/readPortableDataListPaged")
    @ResponseBody
    public ResultPagingVO getPortableDataPaged (HttpServletRequest req, HttpServletResponse res, ModelMap model) {

        ResultPagingVO resultVO = new ResultPagingVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            String fromDate = StringUtils.defaultString(req.getParameter("fromDate"));
            String toDate = StringUtils.defaultString(req.getParameter("toDate"));
            if ("".equals(fromDate) || "".equals(toDate)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                toDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.YEAR, -1);
                fromDate = dateFormat.format(cal.getTime());
            }
            options.put("fromDate", fromDate);
            options.put("toDate", toDate);

            String adminId = req.getParameter("adminId");
            if (adminId != null && !adminId.isEmpty())
                options.put("adminId", req.getParameter(adminId));

            String isUser = req.getParameter("isUser");
            if (isUser != null && isUser.equalsIgnoreCase("true")) {
                ResultVO userResultVO = userService.readUserData(LoginInfoHelper.getUserId());
                if (userResultVO != null && userResultVO.getData() != null) {
                    UserVO userVO = (UserVO) userResultVO.getData()[0];
                    String userId = userVO.getUserId();
                    options.put("userId", userId);
                }
            }

            options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
            options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

            String searchType = req.getParameter("searchType");
            if (searchType != null) {
                if (searchType.equalsIgnoreCase("chUserId")) {
                    options.put("searchType", "USER_ID");
                }
                else if (searchType.equalsIgnoreCase("chImagePublish")) {
                    options.put("searchType", "IMAGE_STATUS");
                }
                else {
                    options.put("searchType", "ALL");
                }
            }
            options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));

            String paramOrderColumn = req.getParameter("orderColumn");
            if (paramOrderColumn != null) {
                if (paramOrderColumn.equalsIgnoreCase("chRegDate")) {
                    options.put("paramOrderColumn", "REG_DT");
                }
                else if (paramOrderColumn.equalsIgnoreCase("chUserIdl")) {
                    options.put("paramOrderColumn", "USER_ID");
                }
                else {
                    options.put("paramOrderColumn", "PTGR_ID");
                }
            }
            String paramOrderDir = req.getParameter("orderDir");
            options.put("paramOrderDir", paramOrderDir);

            String paramLang = req.getParameter("lang");
            options.put("lang", paramLang);

            resultVO = portableService.readPortableViewPaged(options);

            resultVO.setDraw(String.valueOf(req.getParameter("page")));
            resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
            resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
            resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }

        return resultVO;
    };

    /**
     * 휴대형 구름 승인 여부 조회
     *
     * @return ResultVO
     */
    @PostMapping (value = "/readReapproveStatus")
    @ResponseBody
    public ResultVO readPortableReapproveState (HttpServletRequest req, HttpServletResponse res)  {

        String adminId = req.getParameter("adminId");
        ResultVO resultVO = new ResultVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            if (adminId != null)
                options.put("adminId", adminId);
            resultVO = portableService.readPortableArroveState(options);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }

        return resultVO;
    }

    /**
     * 휴대형 구름 신청 정보 개별 승인
     *
     * @param certId
     * @return ResultVO
     */
    @PostMapping(value = "/updateData")
    @ResponseBody
    public ResultVO updatePortableDataForRemoveCertFileAndUpdateDurationTime(
            @RequestParam(value= "certId") String certId,
            @RequestParam(value= "imageId") String imageId,
            @RequestParam(value= "dataDelete") String dataDelete,
            @RequestParam(value= "buildNm") String buildNm) {

        ResultVO resultVO = new ResultVO();

        try
        {
            if (certId == null || certId.isEmpty() || buildNm == null || buildNm.isEmpty()) {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                        MessageSourceHelper.getMessage("portable.result.errdata")));
            }
            else {
                //Remove Cert Data
                resultVO = portableCertService.readCertDataByCertId(certId);
                if (resultVO.getData() == null || resultVO.getData().length == 0) {
                    resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECT,
                            MessageSourceHelper.getMessage("portable.result.nodatacert")));
                }
                else {
                    PortableCertVO certVO = (PortableCertVO)resultVO.getData()[0];
                    Path path = Paths.get(certVO.getCertPath());
                    FileUtils.delete(new File(path.getParent().toString()));

                    if (dataDelete != null && dataDelete.equalsIgnoreCase("true"))
                        portableCertService.deleteCertDataByCertId(Integer.parseInt(certId));
                }
                //Update JobId
                logger.debug("certID [" +  certId  + "] imageId [ " + imageId + " ]");
                ResultVO ptgrJobResultVO = portableJobService.readJobDataByImageId(Integer.parseInt(imageId));
                if (ptgrJobResultVO.getData() != null && ptgrJobResultVO.getData().length != 0) {
                   PortableJobVO jobVO = (PortableJobVO) ptgrJobResultVO.getData()[0];
                   jobVO.setJobId(Integer.parseInt(buildNm));
                   portableJobService.updateJobData(jobVO);
                   logger.debug("Update Job [" + buildNm + "]");
                }
                else {
                    PortableJobVO jobVO = new PortableJobVO();
                    jobVO.setJobId(Integer.parseInt(buildNm));
                    jobVO.setImageId(Integer.parseInt(imageId));
                    portableJobService.createJobData(jobVO);
                    logger.debug("Create Job [" + buildNm + "]");
                }

                ResultVO ptgrImageResultVO = portableImageService.readImageDataById(Integer.parseInt(imageId));
                if (ptgrImageResultVO.getData() != null && ptgrImageResultVO.getData().length != 0) {
                    PortableImageVO imageVO = (PortableImageVO) ptgrImageResultVO.getData()[0];
                    String json = jenkinsUtils.jenkinsGetJobDuration(Integer.parseInt(buildNm));
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        JenkinsJob job = objectMapper.readValue(json, JenkinsJob.class);
                        long time = job.getTimestamp();
                        imageVO.setCreatedDt(new Date(time));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    portableImageService.updateImageData(imageVO);
                }
            }
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.MSG_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }
        return resultVO;
    }

    /**
     * 휴대형 구름 신청 정보 개별 승인
     *
     * @param ptgrId
     * @return ResultVO
     */
    @PostMapping(value = "/updateApprove")
    @ResponseBody
    public StatusVO updatePortableDataApprove(@RequestParam(value= "ptgrId") String ptgrId) {

        StatusVO statusVO = new StatusVO();

        if (ptgrId== null || ptgrId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam"));
            logger.debug("portable id is null : {}", ptgrId);
            return statusVO;
        }

        ResultVO resultVO = null;
        HashMap<String, Object> options = new HashMap<String, Object>();

        try {
            options.put("ptgrId", ptgrId);
            resultVO = portableService.readPortableDataById(options);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            return statusVO;
        }

        if (resultVO == null || resultVO.getData().length == 0) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errdata"));
            return statusVO;
        }

        PortableVO ptgrVO = (PortableVO) resultVO.getData()[0];
        if (ptgrVO.getApproveStatus().equals(PortableConstants.STATUS_APPROVE_REAPPROVE)) {
            try {
                ResultVO imageResultVO = portableImageService.readImageDataById(ptgrVO.getImageId());
                if (imageResultVO != null || imageResultVO.getData().length != 0) {
                    logger.debug("portable copied ");
                    PortableImageVO imageVO = (PortableImageVO) imageResultVO.getData()[0];
                    if (imageVO.getStatus() == PortableConstants.STATUS_IMAGE_COPIED_FAIL) {

                        ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE);
                        portableService.updatePortableData(ptgrVO);
                        imageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                        portableImageService.updateImageData(imageVO);

                        callJenkinsForFileCopy(ptgrVO, imageVO);
                        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                                MessageSourceHelper.getMessage("portable.result.approve"));
                        return statusVO;
                    }
                }
            } catch (Exception e) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
                e.printStackTrace();
            }
        }

        try {
            logger.debug("portable approve");
            statusVO = portableApprove(ptgrVO);
            if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                String msg = "updatePortableDataApprove : failed " + statusVO.getMessage();
                int logId = createLog(ptgrVO, statusVO.getResultCode(), msg, PortableConstants.LOG_WARN);
                ptgrVO.setStatusCd(PortableConstants.LOG_WARN);
                ptgrVO.setLogId(logId);
                ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REAPPROVE);
                portableService.updatePortableData(ptgrVO);
                logger.debug("portable approve fail: {}", statusVO.getMessage());
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.approveerror"));
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }
        return statusVO;
    }

    /**
     * 휴대형 구름 신청 정보 전체 승인
     *
     * @param adminId
     * @return ResultVO
     */
    @PostMapping (value ="/updateAllApprove")
    @ResponseBody
    public StatusVO updateAllInfoForApprove (@RequestParam(value ="adminId") String adminId) {

        StatusVO statusVO = new StatusVO();

        if (adminId == null || adminId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam"));
            logger.debug("portable admin id is null : {}", adminId);
            return statusVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("adminId", adminId);
            options.put("approveStatus", "REAPPROVE");

            ResultVO resultVO;
            resultVO = portableService.readPortableDataByAdminIdAndApprove(options);
            if (resultVO.getData() == null || resultVO.getData().length == 0) {
                return resultVO.getStatus();
            }

            Object[] objs = resultVO.getData();
            for (Object obj: objs) {
                PortableVO vo = (PortableVO)obj;

                if (vo.getApproveStatus().equals(PortableConstants.STATUS_APPROVE_REAPPROVE)){
                    try {
                        ResultVO imageResultVO = portableImageService.readImageDataById(vo.getImageId());
                        if (imageResultVO != null || imageResultVO.getData().length != 0) {
                            PortableImageVO imageVO = (PortableImageVO) imageResultVO.getData()[0];
                            if (imageVO.getStatus() == PortableConstants.STATUS_IMAGE_COPIED_FAIL) {

                                vo.setApproveStatus(PortableConstants.STATUS_APPROVE);
                                portableService.updatePortableData(vo);
                                imageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                                portableImageService.updateImageData(imageVO);

                                callJenkinsForFileCopy(vo, imageVO);
                                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                                        MessageSourceHelper.getMessage("portable.result.approve"));
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
                        e.printStackTrace();
                        continue;
                    }
                }
                asyncPortableApprove(Integer.toString(vo.getPtgrId()));
                System.out.println(vo.getPtgrId());
            }
            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                    MessageSourceHelper.getMessage("portable.result.reapprovingall"));
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }
        return statusVO;
    }

    /**
     * 휴대형 구름 신청 정보 삭제
     *
     * @return ResultVO
     */
    @PostMapping (value = "/deletePortableDataList")
    @ResponseBody
    public StatusVO deletePortableDatalist (HttpServletRequest req, HttpServletResponse res)  {

        String[] ids = req.getParameter("ids").split(",");
        StatusVO statusVO = new StatusVO();
        try
        {
            if (ids.length == 0 || ids[0].equals("")) {
                statusVO = portableService.deleteAllPortableData();
            }
            else {
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("ptgrIds", ids);
                statusVO = portableService.deletePortableData(options);
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }

        return statusVO;
    }

    /**
     * 휴대형구름 등록
     *
     * @param ptgrVO
     * @return ResultVO
     */
    @PostMapping(value = "/registerPortableData")
    @ResponseBody
    public StatusVO registerInfo (@ModelAttribute PortableVO ptgrVO) {
        StatusVO statusVO = new StatusVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            return statusVO;
        }

        try
        {
            //이미지 테이블 생성
            int imageId;
            imageId = portableImageService.readNextImageDataIndex();
            PortableImageVO portableImageVO = new PortableImageVO();
            portableImageVO.setImageId(imageId);
            portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_REQUEST);

            statusVO = portableImageService.createImageData(portableImageVO);
            if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                logger.debug("not create image table: {}", imageId);
                return statusVO;
            }
            ptgrVO.setImageId(imageId);
            //인증서 테이블 생성
            int certId;
            certId = portableCertService.readNextCertDataIndex();
            PortableCertVO portableCertVO = new PortableCertVO();
            portableCertVO.setCertId(certId);
            portableCertVO.setPublish(0);

            statusVO = portableCertService.createCertData(portableCertVO);
            if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                portableImageService.deleteImageDataByImageId(imageId);
                logger.debug("not create image table: {}", imageId);
                return statusVO;
            }
            ptgrVO.setCertId(certId);

            int ptgrId;
            ptgrId = portableService.readNextPortableDataIndex();
            ptgrVO.setPtgrId(ptgrId);
            ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REQUEST);

            statusVO = portableService.createPortableData(ptgrVO);
            if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                portableImageService.deleteImageDataByImageId(imageId);
                portableCertService.deleteCertDataByCertId(certId);
                logger.debug("not create portable table: {}", ptgrId);
                return statusVO;
            }

        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            e.printStackTrace();
        }
        return statusVO;
    };

    /**
     * 휴대형구름 정보
     *
     * @param userId
     * @return ResultVO
     */
    @PostMapping (value = "/readPortableData")
    @ResponseBody
    public ResultVO getPortableDataListOfUser (@RequestParam(value = "userId") String userId) {

        ResultVO resultVO = new ResultVO();

        if (GPMSConstants.USE_PORTABLE.equalsIgnoreCase("false")) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("userId", userId);
            resultVO = portableService.readPortableViewById(options);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            e.printStackTrace();
        }
        return resultVO;
    };

    /**
     * 휴대형구름 템플릿 파일 다운로드
     *
     * @return ResultVO
     */
    @GetMapping (value = "/downloadTemplate")
    @ResponseBody
    public ResponseEntity<FileSystemResource> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        String path= request.getSession().getServletContext().getRealPath("/template");
        String filename= "template.csv";
        Path fullPath = Paths.get(path, filename);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "text/csv");
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.csv");
        return new ResponseEntity<FileSystemResource>(new FileSystemResource(fullPath.toString()), header, HttpStatus.OK);
    }

    /**
     * 빌드 서버 요청
     */
    private void callJenkins (PortableVO ptgrVO, PortableCertVO ptgrCertVO) {
        ArrayList<String> params = new ArrayList<>();
        params.add("userId="+ptgrVO.getUserId());
        params.add("userEmail="+ptgrVO.getNotiEmail());
        params.add("userPassword="+ptgrVO.getUserPw());
        params.add("isoPassword="+ptgrVO.getIsoPw());
        params.add("imageId="+ptgrVO.getImageId());
        params.add("certId="+ptgrVO.getCertId());
        params.add("serverUrl="+serverAPI);
        params.add("certDeleteUrl="+updateDataAPI);
        params.add("root.pem=@"+Paths.get(GPMSConstants.ROOT_CERTPATH,GPMSConstants.ROOT_CERTFILENAME));
        params.add("cert.pem=@"+ptgrCertVO.getCertPath());
        params.add("private.key=@"+ptgrCertVO.getKeyPath());
        jenkinsUtils.jenkinsBuildWithParameter(params);
    }

    /**
     * 빌드 서버 요청
     */
    private void callJenkinsForFileCopy (PortableVO ptgrVO, PortableImageVO imageVO) {

        Path imgPath = Paths.get(imageVO.getUrl());
        int nCount = imgPath.getNameCount();
        String parentPath = imgPath.getName(nCount - 2).toString();

        ArrayList<String> params = new ArrayList<>();
        params.add("imageId="+ptgrVO.getImageId());
        params.add("certId="+ptgrVO.getCertId());
        params.add("serverUrl="+serverAPI);
        params.add("checkSum="+parentPath);
        params.add("userEmail="+ptgrVO.getNotiEmail());
        jenkinsUtils.jenkinsBuildWithParameter(params);
    }

    /**
     * 휴대형 구름 로그 생성
     */
    private int createLog (PortableVO portableVO, String code, String message, String level) throws Exception {
        PortableLogVO logVO = new PortableLogVO();
        logVO.setLogId(portableLogService.readNextLogDataIndex());
        logVO.setPtgrId(portableVO.getPtgrId());
        logVO.setAdminId(portableVO.getAdminId());
        logVO.setUserId(portableVO.getUserId());
        logVO.setErrorStatus(code);
        logVO.setLogLevel(level);
        logVO.setLogValue(message);
        portableLogService.createLogData(logVO);
        logger.debug("create log : \n {}", logVO.toString());
        return logVO.getLogId();
    }

    /**
     * 동기 휴대형 구름 신청정보 승인 및 빌드 서버 전달
     */
    private StatusVO portableApprove (PortableVO vo) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        StatusVO statusVO = new StatusVO();
        //인증서 생성..
        String userId = vo.getUserId();
        ResultVO userResultVO = userService.readUserData(userId);
        if (userResultVO.getData().length == 0) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.erruser"));
            return statusVO;
        }

        Locale dateLocale = new Locale.Builder().setLanguage("ko").setRegion("KO").build();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), dateLocale);
        cal.setTime(vo.getExpiredDt());
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.HOUR, 9);
        Date yearFromNow = cal.getTime();

        cal.setTime(vo.getBeginDt());
        cal.add(Calendar.HOUR, 9);
        Date beginDate = cal.getTime();

        UserVO userVO = (UserVO) userResultVO.getData()[0];
        String userPw = userVO.getUserPasswd();

        vo.setUserPw(userPw);

        CertificateUtils certUtils = new CertificateUtils();
        CertificateVO certVO = certUtils.createGcspCertificate(userId, beginDate, yearFromNow, new BigInteger(64, new SecureRandom()), userPw);
        //인증서 파일 저장
        Path certPath = Paths.get(GPMSConstants.PORTABLE_CERTPATH, userId, Integer.toString(vo.getCertId()), GPMSConstants.PORTABLE_CERTFILENAME);

        File rootPath = new File(certPath.getParent().toString());
        if (!rootPath.exists())
            rootPath.mkdirs();

        File certFile = new File(certPath.toString());
        FileUtils.writeContent(certFile, certVO.getCertificatePem());
        Path keyPath = Paths.get(GPMSConstants.PORTABLE_CERTPATH, userId, Integer.toString(vo.getCertId()), GPMSConstants.PORTABLE_KEYFILENAME);
        File privateFile = new File(keyPath.toString());
        FileUtils.writeContent(privateFile, certVO.getPrivateKeyPem());

        ResultVO certResultVO = portableCertService.readCertDataByCertId(Integer.toString(vo.getCertId()));
        if (certResultVO.getData().length == 0) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.nodatacert"));
            //인증서 삭제...
            FileUtils.delete(certFile);
            FileUtils.delete(privateFile);
            return statusVO;
        }
        PortableCertVO ptgrCertVO = (PortableCertVO) certResultVO.getData()[0];
        ptgrCertVO.setCreatedDt(new Date());
        ptgrCertVO.setPublish(1); //발급완료
        ptgrCertVO.setCertPath(certPath.toString());
        ptgrCertVO.setKeyPath(keyPath.toString());
        StatusVO certStatusVO = portableCertService.updateCertData(ptgrCertVO);
        if (certStatusVO.getResultCode() == GPMSConstants.MSG_FAIL) {
            FileUtils.delete(certFile);
            FileUtils.delete(privateFile);
            return statusVO;
        }
        //이미지 신청 상태 변경
        HashMap<String, Object> options = new HashMap<>();
        options.put("imageId", vo.getImageId());
        ResultVO ret = portableImageService.readImageDataById(vo.getImageId());
        if (ret.getData() == null) {
            String msg = "portableApprove : No image table information";
            createLog(vo, GPMSConstants.CODE_NODATA, msg, PortableConstants.LOG_WARN);
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errdata"));
            return statusVO;
        }
        PortableImageVO portableImageVO = (PortableImageVO) ret.getData()[0];
        //기존 JOB 정보 제거
        portableJobService.deleteJobDataByImageId(portableImageVO.getImageId());
        portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
        portableImageService.updateImageData(portableImageVO);
        //승인 상태 변경
        vo.setApproveStatus(PortableConstants.STATUS_APPROVE);
        //빌드 전달 상태 변경
        vo.setBuildStatus(PortableConstants.STATUS_BUILD_REQUEST);
        portableService.updatePortableData(vo);
        //인증서 전달 시간
        ptgrCertVO.setTransferDt(new Date());
        portableCertService.updateCertData(ptgrCertVO);

        //빌드 서버 요청
        callJenkins(vo, ptgrCertVO);
        logger.debug("portable approve success : {}", statusVO.getMessage());
        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                MessageSourceHelper.getMessage("portable.result.approve"));

        return statusVO;
    }

    /**
     * 비동기 휴대형 구름 신청정보 승인 및 빌드 서버 전달
     */
    private void asyncPortableApprove (String pId) {

        Runnable addJob = () -> {
            try {
                if (!list.contains(pId)) {
                    list.add(pId) ;
                    queue.put(pId);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable processJob = () -> {
            Security.addProvider(new BouncyCastleProvider());
            StatusVO statusVO = new StatusVO();
            PortableVO vo = new PortableVO();
            System.out.println("Runnable" + Thread.currentThread().getName());
            try
            {
                String ptgrId = queue.take();
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("ptgrId", ptgrId);

                ResultVO resultVO;
                resultVO = portableService.readPortableDataById(options);
                if (resultVO.getData().length == 0) {
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                            MessageSourceHelper.getMessage("portable.result.errdata"));
                    callback.completed(statusVO, vo);
                    return;
                }

                vo = (PortableVO)resultVO.getData()[0];
                //인증서 생성..
                String userId = vo.getUserId();
                ResultVO userResultVO = userService.readUserData(userId);
                if (userResultVO.getData().length == 0) {
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                            MessageSourceHelper.getMessage("portable.result.erruser"));
                    callback.completed(statusVO, vo);
                    return;
                }

                Locale dateLocale = new Locale.Builder().setLanguage("ko").setRegion("KO").build();
                Calendar cal = Calendar.getInstance(TimeZone.getDefault(), dateLocale);
                cal.setTime(vo.getExpiredDt());
                cal.add(Calendar.DATE, 1);
                cal.add(Calendar.HOUR, 9);
                Date yearFromNow = cal.getTime();

                cal.setTime(vo.getBeginDt());
                cal.add(Calendar.HOUR, 9);
                Date beginDate = cal.getTime();

                UserVO userVO = (UserVO)userResultVO.getData()[0];
                String userPw = userVO.getUserPasswd();

                vo.setUserPw(userPw);

                CertificateUtils certUtils = new CertificateUtils();
                CertificateVO certVO = certUtils.createGcspCertificate(userId, beginDate, yearFromNow, new BigInteger(64, new SecureRandom()), userPw);
                //인증서 파일 저장
                Path certPath = Paths.get(GPMSConstants.PORTABLE_CERTPATH, userId, Integer.toString(vo.getCertId()), GPMSConstants.PORTABLE_CERTFILENAME);

                File rootPath = new File (certPath.getParent().toString());
                if (!rootPath.exists())
                    rootPath.mkdirs();

                File certFile = new File(certPath.toString());
                FileUtils.writeContent(certFile, certVO.getCertificatePem());
                Path keyPath = Paths.get(GPMSConstants.PORTABLE_CERTPATH, userId, Integer.toString(vo.getCertId()), GPMSConstants.PORTABLE_KEYFILENAME);
                File privateFile = new File(keyPath.toString());
                FileUtils.writeContent(privateFile, certVO.getPrivateKeyPem());

                ResultVO certResultVO  = portableCertService.readCertDataByCertId(Integer.toString(vo.getCertId()));
                if (certResultVO.getData().length == 0) {
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                            MessageSourceHelper.getMessage("portable.result.errdata"));
                    //인증서 삭제...
                    FileUtils.delete(certFile);
                    FileUtils.delete(privateFile);
                    callback.completed(statusVO, vo);
                    return;
                }
                PortableCertVO ptgrCertVO = (PortableCertVO)certResultVO.getData()[0];
                ptgrCertVO.setCreatedDt(new Date());
                ptgrCertVO.setPublish(1); //발급완료
                ptgrCertVO.setCertPath(certPath.toString());
                ptgrCertVO.setKeyPath(keyPath.toString());
                StatusVO certStatusVO = portableCertService.updateCertData(ptgrCertVO);
                if (certStatusVO.getResultCode() == GPMSConstants.MSG_FAIL) {
                    FileUtils.delete(certFile);
                    FileUtils.delete(privateFile);
                    callback.completed(certStatusVO, vo);
                    return;
                }
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.apprroving"));
                callback.completed(statusVO, vo);
                list.remove(ptgrId);
            } catch (Exception e) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,e.getMessage());
                callback.failed(e, vo);
                e.printStackTrace();
            }
        };
        executor.execute(addJob);
        executor.submit(processJob);
    }
}
