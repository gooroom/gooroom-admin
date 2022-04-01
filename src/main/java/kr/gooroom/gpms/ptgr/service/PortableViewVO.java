package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;
import java.util.Date;

public class PortableViewVO implements Serializable {

    int ptgrId;
    int certId;

    int buildStatus;
    int certStatus;

    String userId;
    String imageName;
    String imageUrl;
    String imageStatus;
    String approveStatus;

    Date regDt;
    Date beginDt;
    Date expiredDt;

    public int getPtgrId() {
        return ptgrId;
    }

    public void setPtgrId(int ptgrId) {
        this.ptgrId = ptgrId;
    }

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public int getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(int buildStatus) {
        this.buildStatus = buildStatus;
    }

    public int getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(int certStatus) {
        this.certStatus = certStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
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
