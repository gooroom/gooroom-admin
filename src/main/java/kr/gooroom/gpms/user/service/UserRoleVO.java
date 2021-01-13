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

import java.io.Serializable;
import java.util.Date;

/**
 * user's role information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class UserRoleVO implements Serializable {

	private String nullColumn;

	private String roleId;
	private String roleNm;
	private String comment;

	private Date regDate;
	private String regUserId;

	private Date modDate;
	private String modUserId;

	private int userCount;

	private String mediaRuleId;
	private String browserRuleId;
	private String clientSecuRuleId;
	private String securityRuleId;
	private String filteredSoftwareRuleId;
	private String ctrlCenterItemRuleId;
	private String policyKitRuleId;

	public String getSecurityRuleId() {
		return securityRuleId;
	}

	public void setSecurityRuleId(String securityRuleId) {
		this.securityRuleId = securityRuleId;
	}

	public String getFilteredSoftwareRuleId() {
		return filteredSoftwareRuleId;
	}

	public void setFilteredSoftwareRuleId(String filteredSoftwareRuleId) {
		this.filteredSoftwareRuleId = filteredSoftwareRuleId;
	}

	public String getNullColumn() {
		return nullColumn;
	}

	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleNm() {
		return roleNm;
	}

	public void setRoleNm(String roleNm) {
		this.roleNm = roleNm;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String getMediaRuleId() {
		return mediaRuleId;
	}

	public void setMediaRuleId(String mediaRuleId) {
		this.mediaRuleId = mediaRuleId;
	}

	public String getBrowserRuleId() {
		return browserRuleId;
	}

	public void setBrowserRuleId(String browserRuleId) {
		this.browserRuleId = browserRuleId;
	}

	public String getClientSecuRuleId() {
		return clientSecuRuleId;
	}

	public void setClientSecuRuleId(String clientSecuRuleId) {
		this.clientSecuRuleId = clientSecuRuleId;
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

}
