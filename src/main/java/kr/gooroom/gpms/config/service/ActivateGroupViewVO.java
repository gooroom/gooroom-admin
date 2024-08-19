package kr.gooroom.gpms.config.service;

import java.io.Serial;
import java.io.Serializable;

public class ActivateGroupViewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2063338831297664285L;
    private String gubun;
    private String deptCd;
    private String deptNm;
    private String regDt;

    public String getGunbun() {
        return gubun;
    }
    public void setGunbun(String gubun) {
        this.gubun = gubun;
    }

    public String getDeptCd() {
        return deptCd;
    }
    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    public String getDeptNm() {
        return deptNm;
    }
    public void setDeptNm(String deptNm) {
        this.deptNm = deptNm;
    }

    public String getRegDt() {
        return regDt;
    }
    public void setRegDt(String regDt) {
        this.regDt = regDt;
    }
}
