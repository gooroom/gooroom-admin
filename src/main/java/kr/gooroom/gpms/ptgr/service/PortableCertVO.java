package kr.gooroom.gpms.ptgr.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PortableCertVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3260953642926733426L;
    int certId;
    int publish; //발급절차

    String certPem;
    String certPath;
    String keyPath;
    Date createdDt;
    Date transferDt;

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public int getPublish() {
        return publish;
    }

    public void setPublish(int publish) {
        this.publish = publish;
    }

    public String getCertPem() {
        return certPem;
    }

    public void setCertPem(String certPem) {
        this.certPem = certPem;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Date getTransferDt() {
        return transferDt;
    }

    public void setTransferDt(Date transferDt) {
        this.transferDt = transferDt;
    }
}
