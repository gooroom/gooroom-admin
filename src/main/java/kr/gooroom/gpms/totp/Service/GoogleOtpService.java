package kr.gooroom.gpms.totp.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.config.service.MgServerConfVO;
import kr.gooroom.gpms.config.service.impl.ClientConfDAO;
import kr.gooroom.gpms.login.OtpDataVO;
import kr.gooroom.gpms.user.service.AdminUserVO;
import kr.gooroom.gpms.user.service.impl.AdminUserDAO;

@Service
public class GoogleOtpService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOtpService.class);

    @Resource(name = "adminUserDAO")
    AdminUserDAO adminUserDAO;

    @Resource(name = "clientConfDAO")
    ClientConfDAO clientConfDAO;

    public OtpDataVO createBarcode(String userName) {
        String secret = generateSecretKey();
        return createBarcode(userName,secret);
    }
//
    private OtpDataVO createBarcode(String userName, String secret){
        String url = getQRBarcodeURL(userName,secret);
        OtpDataVO otpData = new OtpDataVO();
        otpData.setSecret(secret);
        otpData.setUrl(url);
        return otpData;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public CheckResult checkOtpCode(String username, int userCode, OtpUpdateRequest updateRequest) throws Exception {
        CheckResult checkResult = new CheckResult();
        boolean isValid;
        // String secret = "";

        try {
            // get secret to use
            HashMap<String, Object> options = new HashMap<>();
            options.put("adminId", username);
            options.put("isAuth", "yes");
            AdminUserVO adminUserVO = adminUserDAO.selectAdminUserData(options);
            String secret = adminUserVO.getSecret();

            if(updateRequest != null) {
                secret = updateRequest.getSecret();
                logger.info("change secret to update otp - secret : " + secret);
            }

            // invalid input check
            if(secret.equals("")){
                checkResult.setResult(false);
            }

            // validation check
            isValid = verifyCode(secret, userCode);
            logger.info("verifyCode result : " + isValid);

            // update or register
            if (isValid && updateRequest != null) {
                adminUserDAO.insertOrUpdateAdminSecret(username, secret);
            }

            checkResult.setResult(isValid);
            return checkResult;

        } catch (SQLException e) {
            logger.error("Exception in GoogleOtpService");
        }
        return null;
    }

    private String generateSecretKey() {
        byte[] buffer = new byte[5 + 5 * 5];
        new Random().nextBytes(buffer);
        Base32 base32 = new Base32();
        byte[] secretKey = Arrays.copyOf(buffer, 10);
        byte[] bEncodedKey = base32.encode(secretKey);

        return new String(bEncodedKey);
    }
    private String getQRBarcodeURL(String userName, String secret) {
        String urlStr = "";
        try {
            // Issuer: 서비스 이름
            String accountName = userName;
            MgServerConfVO mgServerConfVO = clientConfDAO.getMgServerConf();
            String issuer = mgServerConfVO.getPmUrl();
            urlStr = "otpauth://totp/"
                    + URLEncoder.encode(accountName, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secret, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");

        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlStr;
    }

    public boolean verifyCode(String secret, int userCode) {
        long wave = new Date().getTime() / 30000; // Google OTP의 주기는 30초
        logger.info("verifyCode Date.getTime()" + new Date() );
        logger.info("userCode : " + userCode);
        logger.info("wave : " + wave);

        try {
            Base32 codec = new Base32();
            byte[] decodedKey = codec.decode(secret);
//            int window = 1;
            int allowedTimeDrift = Integer.parseInt(GPMSConstants.OTP_LOGIN_ALLOWED_TIME_DRIFT);
            for (int i = -allowedTimeDrift; i <= allowedTimeDrift; ++i) {
                int serverCode = generateServerCode(decodedKey, wave + i);
                logger.info("server code : " + serverCode);
                if (serverCode == userCode) {
                    return true;
                }
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int generateServerCode(byte[] decodedKey, long wave) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = wave;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        logger.info("============ generateServerCode start ============");
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, "HmacSHA1");
        logger.info("signKey.getEncoded().toString() : "+signKey.getEncoded().toString());
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        logger.info("hash : "+hash);
        int offset = hash[20 - 1] & 0xF;

        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        logger.info("truncatedHash before : "+truncatedHash);
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        logger.info("truncatedHash after : "+truncatedHash);
        logger.info("============ generateServerCode  end  ============");
        return (int) truncatedHash;
    }
}
