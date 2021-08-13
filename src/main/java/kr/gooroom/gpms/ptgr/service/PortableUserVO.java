package kr.gooroom.gpms.ptgr.service;

import java.io.Serializable;
import java.util.Date;

public class PortableUserVO implements Serializable {

    int ptgrUserId;

    String userNm;
    String userPw;
    String userDept;


    public int getPtgrUserId() {
        return ptgrUserId;
    }

    public void setPtgrUserId(int ptgrUserId) {
        this.ptgrUserId = ptgrUserId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserDept() {
        return userDept;
    }

    public void setUserDept(String userDept) {
        this.userDept = userDept;
    }
}
