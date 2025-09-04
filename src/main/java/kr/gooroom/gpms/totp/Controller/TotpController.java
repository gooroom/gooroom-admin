package kr.gooroom.gpms.totp.Controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/otp")
public class TotpController  {

    private static final Logger logger = LoggerFactory.getLogger(TotpController.class);
    private static final String ERR_ACCOUNT_MSG = "system.login.error.account";
    private static final String ERR_DENIED_GPMS_MSG = "system.common.deniedgpms";
    private static final String ERR_TRIAL_MSG = "system.login.login.trial2";
    private static final String ERR_NOTMATCH_OTP_MSG = "system.login.error.otp.notmatch";

    @Resource(name = "googleOtpService")
    GoogleOtpService googleOtpService;

    @Resource(name = "adminUserService")
    AdminUserService adminUserService;

    @GetMapping("/login")
    public ModelAndView loginGet() {
        ModelAndView mv = new ModelAndView("main/otpLogin");
        return mv;
    }

    @PostMapping(value = "/login")
    public ModelAndView login(
            @RequestAttribute(value = "s", required = false) String status,
            @RequestAttribute(value = "c", required = false) String error) throws Exception {

        final int MAX_B64_LEN = 512;
        final int MAX_TEXT_LEN = 256;
        final Pattern B64_RE = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
        final Pattern CODE_RE = Pattern.compile("^[A-Z][0-9]{2}$");

        ModelAndView mv = new ModelAndView("main/otpLogin");

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

        if (!GPMSConstants.MSG_FAIL.equalsIgnoreCase(status)) {
            if ("denied".equalsIgnoreCase(status)) {
                mv.addObject("msg", MessageSourceHelper.getMessage(ERR_DENIED_GPMS_MSG));
            }
            return mv;
        }

        if (error == null || error.isBlank()) {
            mv.setViewName("/");
            return mv;
        }

        if (error.length() > MAX_B64_LEN) {
            logger.warn("Error parameter too long (base64 length)");
            mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
            return mv;
        }

        if (!B64_RE.matcher(error).matches()) {
            logger.warn("Invalid Base64 format");
            mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
            return mv;
        }

        try {

            byte[] decoded = Base64.getDecoder().decode(error);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);

            if (decodedStr.length() > MAX_TEXT_LEN) {
                logger.warn("Decoded error data too long");
                mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
                return mv;
            }

            String[] parts = decodedStr.split(";", 3);
            String code = parts[0];

            if (!CODE_RE.matcher(code).matches()) {
                logger.warn("Invalid error code format: {}", code);
                mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
                return mv;
            }

            if (GPMSConstants.ERR_LOGIN_DENIED.equalsIgnoreCase(code)) {

                mv.addObject("msg", "ERR_LOGIN_DENIED");

            } else if (GPMSConstants.ERR_OTP_LOGIN_TRIAL.equalsIgnoreCase(code)) {

                String p1 = (parts.length > 1) ? parts[1] : "";
                String p2 = (parts.length > 2) ? parts[2] : "";
                mv.addObject("msg", MessageSourceHelper.getMessage(ERR_NOTMATCH_OTP_MSG, new String[]{p1, p2}));
                mv.addObject("msg2", MessageSourceHelper.getMessage(ERR_TRIAL_MSG, p2));

            } else if (GPMSConstants.ERR_OTP_LOGIN_TRIAL_EXCEEDED.equalsIgnoreCase(code)) {

                mv.addObject("msg", "ERR_OTP_LOGIN_TRIAL_EXCEEDED");
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Base64 decoding failed", e);
            mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
        } catch (Exception e) {
            logger.error("Login error handling failed", e);
            mv.addObject("msg", MessageSourceHelper.getMessage(ERR_ACCOUNT_MSG));
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
