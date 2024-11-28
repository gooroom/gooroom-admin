package kr.gooroom.gpms.totp.Service;

public class CheckResult {
    private boolean result;
    private String sessionId;
    private Long expiredDate;

    public void setExpiredDate(Long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean getResult() {
        return result;
    }

    public Long getExpiredDate() {
        return expiredDate;
    }

    public String getSessionId() {
        return sessionId;
    }
}
