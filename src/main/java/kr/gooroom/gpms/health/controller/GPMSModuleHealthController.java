package kr.gooroom.gpms.health.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.health.service.GPMSModuleHealthVO;
import kr.gooroom.gpms.health.service.GPMSMoudleHealthService;
import kr.gooroom.gpms.user.service.AdminUserService;

@ResponseBody
@RequestMapping("/module")
@Controller
public class GPMSModuleHealthController {
    private static final Logger logger = LoggerFactory.getLogger(GPMSModuleHealthController.class);

    @Resource(name = "gpmsModuleHealthService")
    GPMSMoudleHealthService gpmsModuleStatusService;

    @Resource(name = "adminUserService")
    private AdminUserService adminUserService;

    @GetMapping(value = "/status/{moduleType}")
    public ResultVO getModuleStatus(@PathVariable("moduleType") String moduleType) {
        ResultVO resultVO = new ResultVO();

        try {
            resultVO = gpmsModuleStatusService.getGPMSModuleStatus(moduleType);

        } catch (Exception ex) {
            logger.error("error in /module/status/" + moduleType + ": {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            StatusVO statusVO = new StatusVO();
            statusVO.setMessage(ex.toString());
            statusVO.setResult(GPMSConstants.MSG_FAIL);
            resultVO.setStatus(statusVO);
        }
        return resultVO;
    }

    @GetMapping(value = "/status")
    public ResultVO getModuleStatus(){
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = gpmsModuleStatusService.getGPMSModuleStatus();

        } catch (Exception ex) {
            logger.error("error in /module/status"  + ": {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            StatusVO statusVO = new StatusVO();
            statusVO.setMessage(ex.toString());
            statusVO.setResult(GPMSConstants.MSG_FAIL);
            resultVO.setStatus(statusVO);
        }
        return resultVO;
    }

    @PostMapping(value = "/control")
    public ResultVO controlModuleStatus(@ModelAttribute("paramVO") GPMSModuleHealthVO bodyVO) {
        ResultVO resultVO = new ResultVO();

        try {
            // 어드민 유저에게만 사용 가능하도록 권한 제한
            String loginAdminId = LoginInfoHelper.getUserId();
            ResultVO adminResultVO = adminUserService.getAdminUserInfo(loginAdminId);
            if (!GPMSConstants.MSG_SUCCESS.equals(adminResultVO.getStatus().getResult())) {
                return adminResultVO;
            }
            StatusVO status = gpmsModuleStatusService.turnGPMSModule(bodyVO.getModuleType(),
                    bodyVO.getModuleActionType());
            resultVO.setStatus(status);
        } catch (Exception ex) {
            logger.error("error in /module/gkmstatus : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            StatusVO statusVO = new StatusVO();
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
                    bodyVO.getModuleActionType() + bodyVO.getModuleType() + bodyVO.getLastActivatedTime());
            resultVO.setStatus(statusVO);
        }

        return resultVO;
    }
}