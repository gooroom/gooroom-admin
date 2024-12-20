package kr.gooroom.gpms.ptgr.service;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PortableVO extends PortableUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3043677709902880978L;
    int ptgrId;
    int logId;
    int certId;
    int imageId;
    int bulk;
    int buildStatus; // 빌드전달결과

    String userId;
    String adminId;

    String isoPw;
    String notiType;
    String notiEmail;
    String approveStatus; //승인상태(요청,완료,재승인)

    String statusCd;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date regDt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date beginDt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date expiredDt;

    public int getPtgrId() {
        return ptgrId;
    }

    public void setPtgrId(int ptgrId) {
        this.ptgrId = ptgrId;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getBulk() {
        return bulk;
    }

    public void setBulk(int bulk) {
        this.bulk = bulk;
    }

    public int getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(int buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getIsoPw() {
        return isoPw;
    }

    public void setIsoPw(String isoPw) {
        this.isoPw = isoPw;
    }

    public String getNotiType() {
        return notiType;
    }

    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }

    public String getNotiEmail() {
        return notiEmail;
    }

    public void setNotiEmail(String notiEmail) {
        this.notiEmail = notiEmail;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getRegDt() {
        return regDt;
    }

    public void setRegDt(Date regDt) {
        this.regDt = regDt;
    }

    public Date getBeginDt() {
        return beginDt;
    }

    public void setBeginDt(Date beginDt) {
        this.beginDt = beginDt;
    }

    public Date getExpiredDt() {
        return expiredDt;
    }

    public void setExpiredDt(Date expiredDt) {
        this.expiredDt = expiredDt;
    }
}
