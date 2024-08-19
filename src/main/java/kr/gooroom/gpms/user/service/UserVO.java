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
import java.util.Date;

/**
 * user information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class UserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -2389752942339133511L;
	private String userId;
	private String userNm;
	private String userEmail;
	private String userType;

	private String userPasswd;
	private String isChangePasswd;

	private String loginId;
	private String status;
	private String statusCd;

	private Date firstLoginDt;
	private Date lastLoginDt;

	private String deptCd;
	private String deptNm;

	private String browserRuleId;
	private String securityRuleId;
	private String mediaRuleId;
	private String filteredSoftwareRuleId;
	private String ctrlCenterItemRuleId;
	private String policyKitRuleId;
	private String desktopConfId;

	private String clientId;
	private String useClientCnt;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	private String nfcSecretData;
	private String passwordStatus;

	private Date passwordExpireDate;
	private String isPasswordExpired;
	private String isUsePasswordExpire;
	private Date expireDate;
	private String expireDateStr;
	private String isExpired;
	private String isUseExpire;

	private String pwdExpireRemainDate;
	private String userExpireRemainDate;

	private String loginTrial;
	private String grade;
	private String sortOrder;

	private String maxMediaCnt;
	private String userReqCnt;
	private String userReqData;

	public UserVO() {
		this.userId = "";
	}

	public UserVO(String userId, String userNm, String userPasswd, String deptCd, String userEmail) {
		this.userId = userId;
		this.userNm = userNm;
		this.userPasswd = userPasswd;
		this.userEmail = userEmail;
		this.loginId = userId;
		this.deptCd = deptCd;
	}

	public UserVO(String userId, String userNm, String userPasswd, String deptCd, String userEmail,
			String expireDateStr) {
		this.userId = userId;
		this.userNm = userNm;
		this.userEmail = userEmail;

		// change by sql
		this.userPasswd = userPasswd;

		this.loginId = userId;
		this.deptCd = deptCd;

		// change by sql
		this.expireDateStr = expireDateStr;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserPasswd() {
		return userPasswd;
	}

	public void setUserPasswd(String userPasswd) {
		this.userPasswd = userPasswd;
	}

	public String getIsChangePasswd() {
		return isChangePasswd;
	}

	public void setIsChangePasswd(String isChangePasswd) {
		this.isChangePasswd = isChangePasswd;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public Date getFirstLoginDt() {
		return firstLoginDt;
	}

	public void setFirstLoginDt(Date firstLoginDt) {
		this.firstLoginDt = firstLoginDt;
	}

	public Date getLastLoginDt() {
		return lastLoginDt;
	}

	public void setLastLoginDt(Date lastLoginDt) {
		this.lastLoginDt = lastLoginDt;
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

	public String getBrowserRuleId() {
		return browserRuleId;
	}

	public void setBrowserRuleId(String browserRuleId) {
		this.browserRuleId = browserRuleId;
	}

	public String getSecurityRuleId() {
		return securityRuleId;
	}

	public void setSecurityRuleId(String securityRuleId) {
		this.securityRuleId = securityRuleId;
	}

	public String getMediaRuleId() {
		return mediaRuleId;
	}

	public void setMediaRuleId(String mediaRuleId) {
		this.mediaRuleId = mediaRuleId;
	}

	public String getFilteredSoftwareRuleId() {
		return filteredSoftwareRuleId;
	}

	public void setFilteredSoftwareRuleId(String filteredSoftwareRuleId) {
		this.filteredSoftwareRuleId = filteredSoftwareRuleId;
	}

	public String getCtrlCenterItemRuleId() {
		return ctrlCenterItemRuleId;
	}

	public void setCtrlCenterItemRuleId(String ctrlCenterItemRuleId) {
		this.ctrlCenterItemRuleId = ctrlCenterItemRuleId;
	}

	public String getPolicyKitRuleId() {
		return policyKitRuleId;
	}

	public void setPolicyKitRuleId(String policyKitRuleId) {
		this.policyKitRuleId = policyKitRuleId;
	}

	public String getDesktopConfId() {
		return desktopConfId;
	}

	public void setDesktopConfId(String desktopConfId) {
		this.desktopConfId = desktopConfId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUseClientCnt() {
		return useClientCnt;
	}

	public void setUseClientCnt(String useClientCnt) {
		this.useClientCnt = useClientCnt;
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

	public String getNfcSecretData() {
		return nfcSecretData;
	}

	public void setNfcSecretData(String nfcSecretData) {
		this.nfcSecretData = nfcSecretData;
	}

	public String getPasswordStatus() {
		return passwordStatus;
	}

	public void setPasswordStatus(String passwordStatus) {
		this.passwordStatus = passwordStatus;
	}

	public Date getPasswordExpireDate() {
		return passwordExpireDate;
	}

	public void setPasswordExpireDate(Date passwordExpireDate) {
		this.passwordExpireDate = passwordExpireDate;
	}

	public String getIsPasswordExpired() {
		return isPasswordExpired;
	}

	public void setIsPasswordExpired(String isPasswordExpired) {
		this.isPasswordExpired = isPasswordExpired;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getExpireDateStr() {
		return expireDateStr;
	}

	public void setExpireDateStr(String expireDateStr) {
		this.expireDateStr = expireDateStr;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}

	public String getLoginTrial() {
		return loginTrial;
	}

	public void setLoginTrial(String loginTrial) {
		this.loginTrial = loginTrial;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getIsUsePasswordExpire() {
		return isUsePasswordExpire;
	}

	public void setIsUsePasswordExpire(String isUsePasswordExpire) {
		this.isUsePasswordExpire = isUsePasswordExpire;
	}

	public String getIsUseExpire() {
		return isUseExpire;
	}

	public void setIsUseExpire(String isUseExpire) {
		this.isUseExpire = isUseExpire;
	}

	public String getPwdExpireRemainDate() {
		return pwdExpireRemainDate;
	}

	public void setPwdExpireRemainDate(String pwdExpireRemainDate) {
		this.pwdExpireRemainDate = pwdExpireRemainDate;
	}

	public String getUserExpireRemainDate() {
		return userExpireRemainDate;
	}

	public void setUserExpireRemainDate(String userExpireRemainDate) {
		this.userExpireRemainDate = userExpireRemainDate;
	}

	
}
