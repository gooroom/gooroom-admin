package kr.gooroom.gpms.totp.Service;

public class OtpUpdateRequest {
    private String otpcode;
    private String userID;
    private String secret;
    private String userName;
    private String hostUrl;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public void setOtpcode(String otpcode) {
        this.otpcode = otpcode;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecret() {
        return secret;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public String getOtpcode() {
        return otpcode;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
}
