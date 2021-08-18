package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.account.service.AccountVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
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
                try {
                    if (statusVO.getResult() == GPMSConstants.MSG_SUCCESS) {
                        portableVO.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_1);
                        portableService.updatePortableData(portableVO);
                        logger.debug("portable approve success : {}", statusVO.getMessage());
                        //빌드 서버 요청

                    } else {
                        int logId = createLog(statusVO, portableVO);
                        portableVO.setStatusCd("ERROR");
                        portableVO.setLogId(logId);
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
                    StatusVO statusVo = new StatusVO();
                    statusVo.setResultCode("ERROR");
                    statusVo.setMessage(throwable.getMessage());
                    int logId = createLog(statusVo, portableVO);
                    portableVO.setStatusCd("ERROR");
                    portableVO.setLogId(logId);
                    portableService.updatePortableData(portableVO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @PostMapping (value = "/admin/checkUserId")
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
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }

        return resultVO;
    }

    /**
     * 휴대형구름 일괄 등록
     *
     * @param portableListVO
     * @return ResultVO
     */
    @PostMapping(value = "/admin/infos")
    @ResponseBody
    public ResultVO registerInfos (@ModelAttribute  PortableListVO portableListVO) {

        ResultVO resultVO = new ResultVO();
        List<PortableVO> ptgrListVO =  portableListVO.getPortableListVO();
        //1.
        if (ptgrListVO.size() == 0) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, ""));
            return resultVO;
        }

        AccountVO vo1  =  LoginInfoHelper.getAccountVO();
        try
        {
            for (PortableVO vo : ptgrListVO) {
                //사용자 등록
                UserVO userVO = new UserVO();
                userVO.setUserId(vo.getUserId());
                userVO.setUserPasswd(vo.getUserPw());
                userVO.setDeptCd("DEPTDEFAULT");
                userVO.setUserNm(vo.getUserNm());
                userVO.setUserEmail(vo.getNotiEmail());
                userService.createUserData(userVO);

                //이미지 테이블 생성
                int imageId;
                imageId = portableImageService.readNextImageDataIndex();
                PortableImageVO portableImageVO = new PortableImageVO();
                portableImageVO.setImageId(imageId);
                portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_PROCEDURE_0);
                portableImageService.createImageData(portableImageVO);
                vo.setImageId(imageId);

                //인증서 테이블 생성
                int certId;
                certId = portableCertService.readNextCertDataIndex();
                PortableCertVO portableCertVO = new PortableCertVO();
                portableCertVO.setCertId(certId);
                portableCertVO.setPublish(0);
                portableCertService.createCertData(portableCertVO);
                vo.setCertId(certId);

                int ptgrId;
                ptgrId = portableService.readNextPortableDataIndex();
                vo.setPtgrId(ptgrId);

                vo.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_0);
                portableService.createPortableData(vo);
            }
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, PortableConstants.CODE_PTGR_SUCCESS, ""));
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }

        return resultVO;
    };

    @GetMapping (value = "/admin/infos")
    @ResponseBody
    public ResultVO getInfos (@RequestParam(value = "adminId", required = false) String adminId) {
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
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }

        return resultVO;
    };

    @GetMapping (value = "/admin/infoListPaged")
    @ResponseBody
    public ResultPagingVO getInfoEx (HttpServletRequest req, HttpServletResponse res, ModelMap model) {

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
                cal.add(Calendar.DATE, -1);
                fromDate = dateFormat.format(cal.getTime());
            }
            options.put("fromDate", fromDate);
            options.put("toDate", toDate);

            String adminId = req.getParameter("adminId");
            options.put("adminId", req.getParameter(adminId));

            options.put("searchKey", StringUtils.defaultString(((req.getParameter("keyword") != null) ? req.getParameter("keyword").replace("_", "\\_") : "")));

            options.put("paramStart", Integer.parseInt(StringUtils.defaultString(req.getParameter("start"), "0")));
            options.put("paramLength", Integer.parseInt(StringUtils.defaultString(req.getParameter("length"), "10")));

            String paramOrderColumn = req.getParameter("orderColumn");
            options.put("paramOrderColumn", paramOrderColumn);
            String paramOrderDir = req.getParameter("orderDir");
            options.put("paramOrderDir", paramOrderDir);

            resultVO = portableService.readPortableViewPaged(options);

            //TODO Date

            resultVO.setDraw(String.valueOf(req.getParameter("page")));
            resultVO.setOrderColumn(StringUtils.defaultString(req.getParameter("orderColumn")));
            resultVO.setOrderDir(StringUtils.defaultString(req.getParameter("orderDir")));
            resultVO.setRowLength(StringUtils.defaultString(req.getParameter("length"), "10"));
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }

        return resultVO;
    };

    @PostMapping(value = "/admin/approve/{ptgrId}")
    @ResponseBody
    public StatusVO updateInfoForApprove(@PathVariable String ptgrId) {

        StatusVO statusVO = new StatusVO();

        if (ptgrId== null || ptgrId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
            return statusVO;
        }

        asyncPortableApprove(ptgrId);
        statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, PortableConstants.CODE_PTGR_SUCCESS,"");
        return statusVO;
    }

    @PostMapping (value ="/admin/approve")
    @ResponseBody
    public StatusVO updateAllInfoForApprove (@RequestParam(value ="adminId") String adminId) {

        StatusVO statusVO = new StatusVO();

        if (adminId == null || adminId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
            return statusVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("adminId", adminId);

            ResultVO resultVO;
            resultVO = portableService.readPortableDataByAdminIdAndApprove(options);
            if (resultVO.getData() == null || resultVO.getData().length == 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
                return statusVO;
            }

            Object[] objs = resultVO.getData();
            for (Object obj: objs) {
                PortableVO vo = (PortableVO)obj;
                asyncPortableApprove(Integer.toString(vo.getPtgrId()));
                System.out.println(vo.getPtgrId());
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }
        return statusVO;
    }

    @DeleteMapping (value = "/admin/infos")
    @ResponseBody
    public StatusVO deleteInfo (@RequestBody List<String> ids)  {

        StatusVO statusVO = new StatusVO();
        try
        {
            if (ids.size() == 0) {
                statusVO = portableService.deleteAllPortableData();
            }
            else {
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("ptgrId", ids);
                statusVO = portableService.deletePortableData(options);
            }
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }

        return statusVO;
    }

    /**
     * 휴대형구름 등록
     *
     * @param ptgrVO
     * @return ResultVO
     */
    @PostMapping(value = "/user/info")
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
            portableImageVO.setStatus(PortableConstants.STATUS_IMAGE_PROCEDURE_0);
            portableImageService.createImageData(portableImageVO);
            ptgrVO.setImageId(imageId);
            //인증서 테이블 생성
            int certId;
            certId = portableCertService.readNextCertDataIndex();
            PortableCertVO portableCertVO = new PortableCertVO();
            portableCertVO.setCertId(certId);
            portableCertVO.setPublish(0);
            portableCertService.createCertData(portableCertVO);
            ptgrVO.setCertId(certId);

            int ptgrId;
            ptgrId = portableService.readNextPortableDataIndex();
            ptgrVO.setPtgrId(ptgrId);

            ptgrVO.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_0);
            statusVO = portableService.createPortableData(ptgrVO);
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage());
        }

        return statusVO;
    };

    @GetMapping (value = "/user/info")
    @ResponseBody
    public ResultVO getInfo (@RequestParam(value = "userId") String userId) {

        ResultVO resultVO = new ResultVO();
        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("userId", userId);
            resultVO = portableService.readPortableViewById(options);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR, e.getMessage()));
        }

        return resultVO;
    };


    private int createLog (StatusVO statusVO, PortableVO portableVO) throws Exception {
        PortableLogVO logVO = new PortableLogVO();
        logVO.setLogId(portableLogService.readNextLogDataIndex());
        logVO.setPtgrId(portableVO.getPtgrId());
        logVO.setAdminId(portableVO.getAdminId());
        logVO.setUserId(portableVO.getUserId());
        logVO.setErrorStatus(statusVO.getResultCode());
        logVO.setLogLevel("ERROR");
        logVO.setLogValue(statusVO.getMessage());
        portableLogService.createLogData(logVO);
        return logVO.getLogId();
    }
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
            try
            {
                String ptgrId = queue.take();
                HashMap<String, Object> options = new HashMap<String, Object>();
                options.put("ptgrId", ptgrId);

                ResultVO resultVO;
                resultVO = portableService.readPortableDataById(options);
                if (resultVO.getData().length == 0) {
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"user not found");
                    callback.completed(statusVO, vo);
                    return;
                }

                vo = (PortableVO)resultVO.getData()[0];
                //인증서 생성..
                String userId = vo.getUserId();
                ResultVO userResultVO = userService.readUserData(userId);
                if (userResultVO.getData().length == 0) {
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"user not found");
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
                    statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"user not found");
                    //인증서 삭제...
                    FileUtils.delete(certFile);
                    FileUtils.delete(privateFile);
                    callback.completed(statusVO, vo);
                    return;
                }
                PortableCertVO ptgrCertVO = (PortableCertVO)certResultVO.getData()[0];
                ptgrCertVO.setCreatedDt(new Date());
                ptgrCertVO.setPublish(1);
                ptgrCertVO.setCertPath(certPath.toString());
                ptgrCertVO.setKeyPath(keyPath.toString());
                StatusVO certStatusVO = portableCertService.updateCertData(ptgrCertVO);
                if (certStatusVO.getResultCode() == GPMSConstants.MSG_FAIL) {
                    FileUtils.delete(certFile);
                    FileUtils.delete(privateFile);
                    callback.completed(certStatusVO, vo);
                    return;
                }
                //빌드서버 요청!!!
                //정보 저장
                //vo.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_1);
                //statusVO = portableService.updatePortableData(vo);
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, PortableConstants.CODE_PTGR_SUCCESS,"");
                callback.completed(statusVO, vo);
                System.out.println(vo.getPtgrId());
                list.remove(ptgrId);
            } catch (Exception e) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,e.getMessage());
                callback.failed(e, vo);
            }
        };

        executor.execute(addJob);
        executor.submit(processJob);
    }
}
