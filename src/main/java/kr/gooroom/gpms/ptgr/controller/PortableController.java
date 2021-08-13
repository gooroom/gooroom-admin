package kr.gooroom.gpms.ptgr.controller;

import kr.gooroom.gpms.account.service.AccountVO;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.ptgr.PortableConstants;
import kr.gooroom.gpms.ptgr.service.*;
import kr.gooroom.gpms.user.service.UserService;
import kr.gooroom.gpms.user.service.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/portable")
public class PortableController {

    private static final Logger logger = LoggerFactory.getLogger(PortableController.class);

    @Resource(name = "portableService")
    private PortableService portableService;

    @Resource(name = "portableImageService")
    private PortableImageService portableImageService;

    @Resource(name = "portableCertService")
    private PortableCertService portableCertService;


    @Resource(name = "userService")
    private UserService userService;

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
                /*

                UserVO userVO = new UserVO();
                userVO.setUserId(vo.getUserId());
                String hashedPassword = passwordEncoder.encodePassword(vo.get, null);
                userVO.setUserPasswd();
                userService.createUserData(userVO);
                 */

                //인증서 생성
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
                //인증서 생성


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

    @PostMapping(value = "/admin/approve/{ptgrId}")
    @ResponseBody
    public StatusVO updateInfoForApprove(@PathVariable String ptgrId) {

        StatusVO statusVO = new StatusVO();

        if (ptgrId== null || ptgrId.isEmpty()) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
            return statusVO;
        }

        try
        {
            HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("ptgrId", ptgrId);

            ResultVO resultVO;
            resultVO = portableService.readPortableDataById(options);
            if (resultVO.getData().length == 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
                return statusVO;
            }

            PortableVO vo = (PortableVO)resultVO.getData()[0];
            vo.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_1);
            //인증서 생성..
            String userId = vo.getUserId();
            ResultVO userResultVO = userService.readUserData(userId);
            if (userResultVO.getData().length == 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"user not found");
                return statusVO;
            }
            UserVO userVO = (UserVO)userResultVO.getData()[0];
            String userPw = userVO.getUserPasswd();


            //user pw


            //빌드서버 요청!!!
            //정보 저장
            statusVO = portableService.updatePortableData(vo);
            System.out.println(vo.getPtgrId());
        } catch (Exception e) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, PortableConstants.CODE_PTGR_ERR,"");
        }

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

            //TODO Thread!!!
            Object[] objs = resultVO.getData();
            for (Object obj: objs) {
                PortableVO vo = (PortableVO)obj;
                vo.setApproveStatus(PortableConstants.STATUS_APPROVE_PROCEDURE_1);
                //빌드서버 요청!!!
                //정보 저장
                statusVO = portableService.updatePortableData(vo);
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
}
