package kr.gooroom.gpms.health.service;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class GPMSModuleHealthVO implements Serializable {
    private String moduleType;
    private Date lastActivatedTime;
    private String moduleActionType;

    public String getModuleType() {
        return this.moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public Date getLastActivatedTime() {
        return this.lastActivatedTime;
    }

    public void setLastActivatedTime(Date lastActivatedTime) {
        this.lastActivatedTime = lastActivatedTime;
    }

    public String getModuleActionType() {
        return this.moduleActionType;
    }

    public void setModuleActionType(String moduleActionType) {
        this.moduleActionType = moduleActionType;
    }

    public GPMSModuleHealthVO() {
    }

    public GPMSModuleHealthVO(String moduleType, String moduleActionType) {
        this.moduleType = moduleType;
        this.moduleActionType = moduleActionType;
    }

    public GPMSModuleHealthVO(String moduleType) {
        this.moduleType = moduleType;
    }

    public GPMSModuleHealthVO(String moduleType, Date lastActivatedTime, String moduleActionType) {
        this.moduleType = moduleType;
        this.lastActivatedTime = lastActivatedTime;
        this.moduleActionType = moduleActionType;
    }
}
