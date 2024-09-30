package kr.gooroom.gpms.totp.Controller;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.login.OtpDataVO;
import kr.gooroom.gpms.totp.Service.GoogleOtpService;
import kr.gooroom.gpms.user.service.AdminUserService;
import kr.gooroom.gpms.user.service.AdminUserVO;

@Controller
@RequestMapping("/otp")
public class TotpController  {

    private static final Logger logger = LoggerFactory.getLogger(TotpController.class);

    @Resource(name = "googleOtpService")
    GoogleOtpService googleOtpService;

    @Resource(name = "adminUserService")
    AdminUserService adminUserService;

    @GetMapping(value = "/login")
    public ModelAndView login(@RequestParam(value = "s", required = false) String status,
                              @RequestParam(value = "c", required = false) String error) throws Exception {

        ModelAndView mv = new ModelAndView("main/otpLogin");

        //Error Handling

        AdminUserVO adminUserVO = new AdminUserVO();
        ResultVO resultVO = adminUserService.selectAdminUserData(LoginInfoHelper.getUserId());

        if(resultVO.getData().length > 0) {
            adminUserVO = (AdminUserVO) resultVO.getData()[0];
        }

        int saved = adminUserVO.getSecretSaved();
        if(saved == 0) {
            mv.addObject("saved", "false");
        } else {
            mv.addObject("saved", "true");
        }

        if (GPMSConstants.MSG_FAIL.equalsIgnoreCase(status)) {
            if(error != null ) {
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] decodedBytes = decoder.decode(error);

                String[] errorCode = (new String(decodedBytes)).split(";");

                if (GPMSConstants.ERR_LOGIN_DENIED.equalsIgnoreCase(errorCode[0])) {
                    mv.addObject("msg", "ERR_LOGIN_DENIED");
                } else if (GPMSConstants.ERR_OTP_LOGIN_TRIAL.equalsIgnoreCase(errorCode[0])) {
                    mv.addObject("msg", "OTP 코드가 일치하지 않습니다. " + "(" + errorCode[1] + "/" + errorCode[2] + ")");
                    mv.addObject("msg2", MessageSourceHelper.getMessage("system.login.login.trial2", (errorCode[2])));
                } else if (GPMSConstants.ERR_OTP_LOGIN_TRIAL_EXCEEDED.equalsIgnoreCase(errorCode[0])) {
                    mv.addObject("msg", "ERR_OTP_LOGIN_TRIAL_EXCEEDED");
                }
            } else {
                mv.setViewName("/");
            }
        } else if ("denied".equalsIgnoreCase(status)) {
            mv.addObject("msg", MessageSourceHelper.getMessage("system.common.deniedgpms"));
        }

        return mv;
    }

    @GetMapping(value = "/generate")
    public @ResponseBody ResultVO generate() {
        OtpDataVO otpDataVO = new OtpDataVO();
        ResultVO resultVO = new ResultVO();

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            otpDataVO = googleOtpService.createBarcode(username);
            StatusVO statusVO = adminUserService.updateOtpSecret(username, otpDataVO.getSecret());
            logger.info("updateOtpSecret result :" + statusVO.getResult());
            Object[] objects = { otpDataVO };
            resultVO.setData(objects);
        } catch (Exception e) {
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
            logger.error("Exception generate() in OTP URL generation");
        }
        return resultVO;
    }
}
