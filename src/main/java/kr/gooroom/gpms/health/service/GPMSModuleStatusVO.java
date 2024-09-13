package kr.gooroom.gpms.health.service;

import java.util.Date;

public class GPMSModuleStatusVO extends GPMSModuleHealthVO {
    private String status;

    public GPMSModuleStatusVO(String moduleType, String lastCommand, String status, Date lastActivatedTime) {
        super(moduleType, lastActivatedTime, lastCommand);
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}