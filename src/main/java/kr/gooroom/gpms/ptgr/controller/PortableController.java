package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.gkm.utils.CertificateUtils;
import kr.gooroom.gpms.gkm.utils.CertificateVO;
import kr.gooroom.gpms.gkm.utils.FileUtils;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.*;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    private final List<String> list = new ArrayList<>();
    private final SynchronousQueue<String> queue = new SynchronousQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final CompletionHandler<StatusVO, PortableVO> callback;

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

    public PortableController() {
        callback = new CompletionHandler<StatusVO, PortableVO>() {
            @Override
            public void completed(StatusVO statusVO, PortableVO portableVO) {
                System.out.println("completed" + Thread.currentThread().getName());
                try {
                    if (statusVO.getResult() == GPMSConstants.MSG_SUCCESS) {
                        //이미지 신청 상태 변경
                        HashMap<String,Object> options = new HashMap<>();
                        options.put("imageId", portableVO.getImageId());
                        ResultVO ret = portableImageService.readImageDataById(portableVO.getImageId());
                        if (ret.getData() == null) {
                            String msg = "completed: No image table information";
                            int logId = createLog(portableVO, GPMSConstants.CODE_NODATA, msg, PortableConstants.LOG_WARN);
                            portableVO.setLogId(logId);
                            portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REREQUEST);
                            portableService.updatePortableData(portableVO);
                            return;
                        }
                        PortableImageVO portableImageVO = (PortableImageVO)ret.getData()[0];
                        portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                        portableImageService.updateImageData(portableImageVO);
                        //승인 상태 변경
                        portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE);
                        //빌드 전달 상태 변경
                        portableVO.setBuildStatus(PortableConstants.STATUS_BUILD_REQUEST);
                        portableService.updatePortableData(portableVO);
                        //빌드 서버 요청

                        logger.debug("portable approve success : {}", statusVO.getMessage());
                    } else {
                        String msg = "completed: fail " + statusVO.getMessage();
                        int logId = createLog(portableVO, statusVO.getResultCode(), msg, PortableConstants.LOG_WARN);
                        portableVO.setStatusCd(PortableConstants.LOG_WARN);
                        portableVO.setLogId(logId);
                        portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REREQUEST);
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
                    portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REREQUEST);
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
            resultVO = userService.isNoExistInUserIdList(options);
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
        List<PortableVO> ptgrListVO =  portableListVO.getPortableListVO();

        if (ptgrListVO.size() == 0) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                    MessageSourceHelper.getMessage("portable.result.errparam")));
            return resultVO;
        }

        String adminId = "";
        try
        {
            for (PortableVO vo : ptgrListVO) {
                StatusVO statusVO = null;
                adminId = vo.getAdminId();
                //사용자 등록
                UserVO userVO;
                resultVO = userService.readUserData(vo.getUserId());
                if (resultVO.getData().length == 0) {
                    userVO = new UserVO();
                    userVO.setUserId(vo.getUserId());
                    userVO.setUserPasswd(vo.getUserPw());
                    userVO.setDeptCd("DEPTDEFAULT");
                    userVO.setUserNm(vo.getUserNm());
                    userVO.setUserEmail(vo.getNotiEmail());
                    statusVO = userService.createUserData(userVO);

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
                vo.setApproveStatus(PortableConstants.STATUS_APPROVE);
                statusVO = portableService.createPortableData(vo);

                if (statusVO.getResult() != GPMSConstants.MSG_SUCCESS) {
                    resultVO.setStatus(statusVO);
                    userService.deleteUserData(userVO.getUserId());
                    portableImageService.deleteImageDataByImageId(imageId);
                    portableCertService.deleteCertDataByCertId(certId);
                    logger.debug("not create portable table: {}", ptgrId);
                    return resultVO;
                }
            }
            //승인...
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("adminId", adminId);
            options.put("approveStatus", PortableConstants.STATUS_APPROVE);
            resultVO = portableService.readPortableDataByAdminIdAndApprove(options);
            if (resultVO != null && resultVO.getData() != null) {
                Object[] objs = resultVO.getData();
                for (Object obj: objs) {
                    PortableVO vo = (PortableVO)obj;
                    asyncPortableApprove(Integer.toString(vo.getPtgrId()));
                }
            }
            else {
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_CREATE, ""));
            }
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_CREATE, ""));
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
    public ResultVO getPortableDataList (@RequestParam(value = "adminId", required = false) String adminId) {
        ResultVO resultVO = new ResultVO();
        try
        {
            if (adminId == null || adminId.isEmpty()) {
                resultVO = portableService.readPortableView();
            }
            else {
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("adminId", adminId);
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
            /*
            HashMap<String, Object> fromDateHm = new HashMap<String, Object>();
            fromDateHm.put("name", "fromDate");
            fromDateHm.put("value", fromDate);
            HashMap<String, Object> toDateHm = new HashMap<String, Object>();
            toDateHm.put("name", "toDate");
            toDateHm.put("value", toDate);
            resultVO.setExtend(new Object[] { fromDateHm, toDateHm });
             */

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
        try {
            statusVO = portableApprove(ptgrVO);

            if (statusVO.getResult() == GPMSConstants.MSG_SUCCESS) {
                //이미지 신청 상태 변경
                options.clear();
                options.put("imageId", ptgrVO.getImageId());
                ResultVO ret = portableImageService.readImageDataById(ptgrVO.getImageId());
                if (ret.getData() == null) {
                    String msg = "updatePortableDataApprove : No image table information";
                    createLog(ptgrVO, GPMSConstants.CODE_NODATA, msg, PortableConstants.LOG_WARN);
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                            MessageSourceHelper.getMessage("portable.result.errdata"));
                    return statusVO;
                }
                PortableImageVO portableImageVO = (PortableImageVO) ret.getData()[0];
                portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                portableImageService.updateImageData(portableImageVO);

                //승인 상태 변경
                ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE);
                //빌드 서버 요청

                //빌드 전달 상태 변경
                ptgrVO.setBuildStatus(PortableConstants.STATUS_BUILD_REQUEST);
                portableService.updatePortableData(ptgrVO);

                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                        MessageSourceHelper.getMessage("portable.result.apprroving"));

                logger.debug("portable approve success : {}", statusVO.getMessage());
            } else {
                String msg = "updatePortableDataApprove : failed " + statusVO.getMessage();
                int logId = createLog(ptgrVO, statusVO.getResultCode(), msg, PortableConstants.LOG_WARN);
                ptgrVO.setStatusCd(PortableConstants.LOG_WARN);
                ptgrVO.setLogId(logId);
                ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE_REREQUEST);
                portableService.updatePortableData(ptgrVO);
                logger.debug("portable approve fail: {}", statusVO.getMessage());
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

            ResultVO resultVO;
            resultVO = portableService.readPortableDataByAdminIdAndApprove(options);
            if (resultVO.getData() == null || resultVO.getData().length == 0) {
                return resultVO.getStatus();
            }

            Object[] objs = resultVO.getData();
            for (Object obj: objs) {
                PortableVO vo = (PortableVO)obj;
                asyncPortableApprove(Integer.toString(vo.getPtgrId()));
                System.out.println(vo.getPtgrId());
            }
            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,"");
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

        System.out.println(statusVO.getResult() + ":" + statusVO.getResultCode() + ":" + statusVO.getMessage());

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
    private StatusVO portableApprove (PortableVO vo) {

        Security.addProvider(new BouncyCastleProvider());
        StatusVO statusVO = new StatusVO();
        try {
            //인증서 생성..
            String userId = vo.getUserId();
            ResultVO userResultVO = userService.readUserData(userId);
            if (userResultVO.getData().length == 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_NODATA,
                        MessageSourceHelper.getMessage("portable.result.erruser"));
                return statusVO;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 1);
            Date yearFromNow = cal.getTime();

            UserVO userVO = (UserVO) userResultVO.getData()[0];
            String userPw = userVO.getUserPasswd();
            CertificateUtils certUtils = new CertificateUtils();
            CertificateVO certVO = certUtils.createGcspCertificate(userId, yearFromNow, new BigInteger(64, new SecureRandom()), userPw);
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
                        MessageSourceHelper.getMessage("portable.result.errdata"));
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

            if (statusVO.getResult() == GPMSConstants.MSG_SUCCESS) {
                //승인 상태 변경
                vo.setApproveStatus(PortableConstants.STATUS_APPROVE);
                //빌드 서버 요청

                //빌드 전달 상태 변경
                vo.setBuildStatus(PortableConstants.STATUS_BUILD_REQUEST);
                portableService.updatePortableData(vo);

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
                portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_CREATE);
                portableImageService.updateImageData(portableImageVO);
                logger.debug("portable approve success : {}", statusVO.getMessage());
            } else {
                String msg = "portableApprove :  failed " + statusVO.getMessage();
                int logId = createLog(vo, statusVO.getResultCode(), msg, PortableConstants.LOG_WARN);
                vo.setStatusCd(PortableConstants.LOG_WARN);
                vo.setLogId(logId);
                portableService.updatePortableData(vo);
                logger.debug("portable approve fail: {}", statusVO.getMessage());
                return statusVO;
            }
            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                    MessageSourceHelper.getMessage("portable.result.apprroving"));
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR, e.getMessage());
            e.printStackTrace();
            return statusVO;
        }

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

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.YEAR, 1);
                Date yearFromNow = cal.getTime();

                UserVO userVO = (UserVO)userResultVO.getData()[0];
                String userPw = userVO.getUserPasswd();
                CertificateUtils certUtils = new CertificateUtils();
                CertificateVO certVO = certUtils.createGcspCertificate(userId, yearFromNow, new BigInteger(64, new SecureRandom()), userPw);
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
