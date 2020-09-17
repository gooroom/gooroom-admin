package kr.gooroom.gpms.user.service;

import java.io.Serializable;

public class UserReqVO implements Serializable {

    private String reqSeq;
    private String userId;
    private String clientId;
    private String regDt;
    private String actionType;
    private String usbName;
    private String usbSerialNo;
    private String usbProduct;
    private String usbSize;
    private String usbVendor;
    private String adminCheck;
    private String modDt;
    private String modUserId;


    public String getReqSeq() {
        return reqSeq;
    }
    public void setReqSeq(String reqSeq) {
        this.reqSeq = reqSeq;
    }
    public String getUserId(String userId) { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getRegDt() { return regDt; }
    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }
    public String getActionType() {
        return actionType;
    }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public void setUsbName(String usbName) { this.usbName = usbName; }
    public String getUsbSerialNo() { return usbSerialNo; }
    public void setUsbSerialNo(String usbSerialNo) { this.usbSerialNo = usbSerialNo; }
    public String getUsbProduct() { return usbProduct; }
    public void setUsbProduct(String usbProduct) { this.usbProduct = usbProduct; }
    public String getUsbSize() { return usbSize; }
    public void setUsbSize(String usbSize) { this.usbSize = usbSize; }
    public String getUsbVendor() { return usbVendor; }
    public void setUsbVendor(String usbVendor) { this.usbVendor = usbVendor; }
    public String getAdminCheck() {
        return adminCheck;
    }
    public void setAdminCheck(String adminCheck) {
        this.adminCheck = adminCheck;
    }
    public String getModDt() { return modDt; }
    public void setModDt(String modDt) { this.modDt = modDt; }
    public String getModUserId() { return modUserId; }
    public void setModUserId(String modUserId) { this.modUserId = modUserId; }
}
