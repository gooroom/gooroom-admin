package kr.gooroom.gpms.login;

import java.util.Base64;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.login.exception.OtpException;
import kr.gooroom.gpms.login.exception.OtpTrialExceedException;
import kr.gooroom.gpms.login.exception.OtpTrialException;
import kr.gooroom.gpms.totp.Service.CheckResult;
import kr.gooroom.gpms.totp.Service.GoogleOtpService;
import kr.gooroom.gpms.totp.Service.OtpAuthenticationToken;
import kr.gooroom.gpms.user.service.AdminUserVO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

public class OtpAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(OtpAuthenticationProvider.class);

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    GoogleOtpService googleOtpService;

    @Autowired
    AdminUserDAO adminUserDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // OTP Secret code
        Integer otpCode = ((OtpAuthenticationToken) authentication).getOtpCode();

        try {
            // Get Username-Password Authentication
            Authentication prev = SecurityContextHolder.getContext().getAuthentication();
            if (prev == null) {
                Base64.Encoder encoder = Base64.getEncoder();
                byte[] encodedBytes = encoder
                        .encode((new StringBuffer(GPMSConstants.ERR_OTP_LOGIN_USERNAME_NOT_FOUND)).append(";NONE").toString().getBytes());
                throw new PreAuthenticatedCredentialsNotFoundException(new String(encodedBytes));
            }

            User user = userLoginService.loadUserByUsername(prev.getName());

            // OTP 검증 로직 수행
            CheckResult checkResult = new CheckResult();
            checkResult = googleOtpService.checkOtpCode(prev.getName(), otpCode, null);

            // OTP 인증 성공 시, 첫 번째 인증 정보의 details 필드에 OTP secret 추가
            if (checkResult.getResult()) {
                OtpAuthenticationToken otpAuthenticationToken = new OtpAuthenticationToken(prev.getPrincipal(), prev.getCredentials(), prev.getAuthorities(), otpCode);
                otpAuthenticationToken.setDetails(prev.getDetails());
                otpAuthenticationToken.setOtpCode(otpCode);
                return otpAuthenticationToken;
            } else {
                AdminUserVO adminUserVO = user.getAdminUserVO();
                // update Login Trial Count
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("adminId", prev.getName());
                if(adminUserVO != null) {
                    int currentLoginTrials = 0;
                    int totalLoginTrialCount = adminUserDao.selectAdminLoginTrialCount("");
                    int remainingLoginAttempts = adminUserVO.getOtpLoginTrial();
                    if(remainingLoginAttempts > 0) {
                        currentLoginTrials = totalLoginTrialCount - remainingLoginAttempts + 1;
                        long result = adminUserDao.updateOtpLoginTrial(paramMap);
                        System.out.println(result);
                    } else {
                        int accountLockoutTime = adminUserDao.selectAdminLoginLockTimeValue("");
				        int loginElapsedTime = adminUserVO.getLoginElapsedTime();
				        int remainingUnlockTime = accountLockoutTime - loginElapsedTime;
                        throw new OtpTrialExceedException((new StringBuffer(GPMSConstants.ERR_LOGIN_LOCK))
							.append(";").append(remainingUnlockTime > 0 ? remainingUnlockTime : 0).toString());
                    }

                    throw new OtpTrialException((new StringBuffer(GPMSConstants.ERR_OTP_LOGIN_TRIAL))
                            .append(";").append(currentLoginTrials > 0 ? currentLoginTrials : 0).append(";").append(totalLoginTrialCount).toString());
                }

                Base64.Encoder encoder = Base64.getEncoder();
                byte[] encodedBytes = encoder
                        .encode((new StringBuffer(GPMSConstants.ERR_OTP_LOGIN_USERCODE)).append(";NONE").toString().getBytes());
                throw new BadCredentialsException(new String(encodedBytes));
            }

        } catch (PreAuthenticatedCredentialsNotFoundException e) {
            logger.error("error in Otp Authentication Provider: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        } catch (BadCredentialsException e) {
            logger.error("error in createLogData: {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), e.toString());
            throw e;
        } catch (OtpTrialException e) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode(e.getMessage().getBytes());
            throw new OtpTrialException(new String(encodedBytes));
        } catch (OtpTrialExceedException e) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder
                    .encode((new StringBuffer(GPMSConstants.ERR_LOGIN_LOCK)).append(";NONE").toString().getBytes());
            throw new OtpTrialExceedException(new String(encodedBytes));
        } catch (Exception e) {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder
                    .encode((new StringBuffer(GPMSConstants.ERR_OTP_LOGIN_USERCODE)).append(";NONE").toString().getBytes());
            throw new OtpException(new String(encodedBytes));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(OtpAuthenticationToken.class);
    }
}
