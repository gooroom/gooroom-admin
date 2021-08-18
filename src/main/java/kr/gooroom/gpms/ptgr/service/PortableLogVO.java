package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;
import java.util.Date;

public class PortableLogVO implements Serializable {

    int logId;
    int ptgrId;

    String adminId;
    String userId;
    String errorStatus;

    Date logDt;
    String logLevel;
    String logValue;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getPtgrId() {
        return ptgrId;
    }

    public void setPtgrId(int ptgrId) {
        this.ptgrId = ptgrId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    public Date getLogDt() {
        return logDt;
    }

    public void setLogDt(Date logDt) {
        this.logDt = logDt;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogValue() {
        return logValue;
    }

    public void setLogValue(String logValue) {
        this.logValue = logValue;
    }
}
