package kr.gooroom.gpms.user.service;

import java.io.Serializable;

public class UserReqVO implements Serializable {

    private String reqSeq;
    private String userId;
    private String clientId;
    private String actionType;
    private String usbName;
    private String usbSerialNo;
    private String usbProduct;
    private String usbVendor;
    private String usbModel;
    private String adminCheck;
    private String regDt;
    private String regUserId;
    private String modDt;
    private String modUserId;
    private String status;


    public String getReqSeq() { return reqSeq; }
    public void setReqSeq(String reqSeq) { this.reqSeq = reqSeq; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getRegDt() { return regDt; }
    public void setRegDt(String regDt) { this.regDt = regDt; }
    public String getRegUserId() { return regUserId; }
    public void setRegUserId(String regUserId) { this.regUserId = regUserId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getUsbName() { return usbName; }
    public void setUsbName(String usbName) { this.usbName = usbName; }
    public String getUsbSerialNo() { return usbSerialNo; }
    public void setUsbSerialNo(String usbSerialNo) { this.usbSerialNo = usbSerialNo; }
    public String getUsbProduct() { return usbProduct; }
    public void setUsbProduct(String usbProduct) { this.usbProduct = usbProduct; }
    public String getUsbVendor() { return usbVendor; }
    public void setUsbVendor(String usbVendor) { this.usbVendor = usbVendor; }
    public String getUsbModel() { return usbModel; }
    public void setUsbModel(String usbModel) { this.usbModel = usbModel; }
    public String getAdminCheck() { return adminCheck; }
    public void setAdminCheck(String adminCheck) { this.adminCheck = adminCheck; }
    public String getModDt() { return modDt; }
    public void setModDt(String modDt) { this.modDt = modDt; }
    public String getModUserId() { return modUserId; }
    public void setModUserId(String modUserId) { this.modUserId = modUserId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
