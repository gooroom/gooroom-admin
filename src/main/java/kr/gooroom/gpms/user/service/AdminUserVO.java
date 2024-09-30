/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.gooroom.gpms.user.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import kr.gooroom.gpms.common.service.NameAndValueVO;

/**
 * administrator user data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class AdminUserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 2449712698110547914L;
	private String adminId;
	private String adminNm;

	private String adminPw;
	private String adminTp;

	private String status;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	private String pollingCycle;
	private String connectIp;
	private String sessionId;

	private ArrayList<String> connIps;
	private ArrayList<NameAndValueVO> grpInfoList;
	private ArrayList<String> grpIds;
	private ArrayList<NameAndValueVO> deptInfoList;
	private ArrayList<String> deptCds;

	private String isClientAdmin;
	private String isUserAdmin;
	private String isDesktopAdmin;
	private String isNoticeAdmin;
	private String isPortableAdmin;

	private String dupLoginTryIp;
	private Date dupLoginTryDate;

	private int loginTrial;
	private Date loginTrialDate;
	private	int loginElapsedTime;

	private int otpLoginTrial;

	private String secret;
	private int secretSaved;
	
	public AdminUserVO() {

	}

	public String getValueByString(String name) {
		if (name != null) {
			switch (name) {
			case "isClientAdmin":
				return getIsClientAdmin();
			case "isUserAdmin":
				return getIsUserAdmin();
			case "isDesktopAdmin":
				return getIsDesktopAdmin();
			case "isNoticeAdmin":
				return getIsNoticeAdmin();
			case "isPortableAdmin":
				return getIsPortableAdmin();
			default:
				return "";
			}
		}

		return "";
	}

	public AdminUserVO(String adminId, String adminPw) {
		this.adminId = adminId;
		this.adminPw = adminPw;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getAdminNm() {
		return adminNm;
	}

	public void setAdminNm(String adminNm) {
		this.adminNm = adminNm;
	}

	public String getAdminPw() {
		return adminPw;
	}

	public void setAdminPw(String adminPw) {
		this.adminPw = adminPw;
	}

	public String getAdminTp() {
		return adminTp;
	}

	public void setAdminTp(String adminTp) {
		this.adminTp = adminTp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public String getPollingCycle() {
		return pollingCycle;
	}

	public void setPollingCycle(String pollingCycle) {
		this.pollingCycle = pollingCycle;
	}

	public String getConnectIp() {
		return connectIp;
	}

	public void setConnectIp(String connectIp) {
		this.connectIp = connectIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public ArrayList<String> getConnIps() {
		return connIps;
	}

	public void setConnIps(ArrayList<String> connIps) {
		this.connIps = connIps;
	}

	public ArrayList<NameAndValueVO> getGrpInfoList() {
		return grpInfoList;
	}

	public void setGrpInfoList(ArrayList<NameAndValueVO> grpInfoList) {
		this.grpInfoList = grpInfoList;
	}

	public ArrayList<NameAndValueVO> getDeptInfoList() {
		return deptInfoList;
	}

	public void setDeptInfoList(ArrayList<NameAndValueVO> deptInfoList) {
		this.deptInfoList = deptInfoList;
	}

	public String getIsClientAdmin() {
		return isClientAdmin;
	}

	public void setIsClientAdmin(String isClientAdmin) {
		this.isClientAdmin = isClientAdmin;
	}

	public String getIsUserAdmin() {
		return isUserAdmin;
	}

	public void setIsUserAdmin(String isUserAdmin) {
		this.isUserAdmin = isUserAdmin;
	}

	public String getIsDesktopAdmin() {
		return isDesktopAdmin;
	}

	public void setIsDesktopAdmin(String isDesktopAdmin) {
		this.isDesktopAdmin = isDesktopAdmin;
	}

	public String getIsNoticeAdmin() {
		return isNoticeAdmin;
	}

	public void setIsNoticeAdmin(String isNoticeAdmin) {
		this.isNoticeAdmin = isNoticeAdmin;
	}

	public ArrayList<String> getGrpIds() {
		return grpIds;
	}

	public void setGrpIds(ArrayList<String> grpIds) {
		this.grpIds = grpIds;
	}

	public ArrayList<String> getDeptCds() {
		return deptCds;
	}

	public void setDeptCds(ArrayList<String> deptCds) {
		this.deptCds = deptCds;
	}

	public String getDupLoginTryIp() {
		return dupLoginTryIp;
	}

	public void setDupLoginTryIp(String dupLoginTryIp) {
		this.dupLoginTryIp = dupLoginTryIp;
	}

	public Date getDupLoginTryDate() {
		return dupLoginTryDate;
	}

	public void setDupLoginTryDate(Date dupLoginTryDate) {
		this.dupLoginTryDate = dupLoginTryDate;
	}

	public String getIsPortableAdmin() {
		return isPortableAdmin;
	}

	public void setIsPortableAdmin(String isPortableAdmin) {
		this.isPortableAdmin = isPortableAdmin;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public int getLoginTrial() {
		return loginTrial;
	}

	public void setLoginTrial(int loginTrial) {
		this.loginTrial = loginTrial;
	}

	public int getLoginElapsedTime() {
		return loginElapsedTime;
	}

	public void setLoginElapsedTime(int loginElapsedTime) {
		this.loginElapsedTime = loginElapsedTime;
	}

	public Date getLoginTrialDate() {
		return loginTrialDate;
	}

	public void setLoginTrialDate(Date loginTrialDate) {
		this.loginTrialDate = loginTrialDate;
	}

	public int getOtpLoginTrial() {
		return otpLoginTrial;
	}

	public int getSecretSaved() {
		return secretSaved;
	}

	public void setSecretSaved(int sercretSaved) {
		this.secretSaved = sercretSaved;
	}
}
