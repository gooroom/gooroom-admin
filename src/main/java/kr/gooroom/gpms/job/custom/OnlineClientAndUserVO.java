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

package kr.gooroom.gpms.job.custom;

import java.io.Serial;
import java.io.Serializable;

/**
 * online client and user
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */
public class OnlineClientAndUserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -2149237186703581292L;
	private String userId;
	private String clientId;

	private String userBrowserRuleId;
	private String userMediaRuleId;
	private String userSecurityRuleId;
	private String userFilteredSoftwareRuleId;
	private String userCtrlCenterItemRuleId;
	private String userPolicyKitRuleId;

	private String deptBrowserRuleId;
	private String deptMediaRuleId;
	private String deptSecurityRuleId;
	private String deptFilteredSoftwareRuleId;
	private String deptCtrlCenterItemRuleId;
	private String deptPolicyKitRuleId;

	private String clientBrowserRuleId;
	private String clientMediaRuleId;
	private String clientSecurityRuleId;
	private String clientFilteredSoftwareRuleId;
	private String clientCtrlCenterItemRuleId;
	private String clientPolicyKitRuleId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserBrowserRuleId() {
		return userBrowserRuleId;
	}

	public void setUserBrowserRuleId(String userBrowserRuleId) {
		this.userBrowserRuleId = userBrowserRuleId;
	}

	public String getUserMediaRuleId() {
		return userMediaRuleId;
	}

	public void setUserMediaRuleId(String userMediaRuleId) {
		this.userMediaRuleId = userMediaRuleId;
	}

	public String getUserSecurityRuleId() {
		return userSecurityRuleId;
	}

	public void setUserSecurityRuleId(String userSecurityRuleId) {
		this.userSecurityRuleId = userSecurityRuleId;
	}

	public String getClientBrowserRuleId() {
		return clientBrowserRuleId;
	}

	public void setClientBrowserRuleId(String clientBrowserRuleId) {
		this.clientBrowserRuleId = clientBrowserRuleId;
	}

	public String getClientMediaRuleId() {
		return clientMediaRuleId;
	}

	public void setClientMediaRuleId(String clientMediaRuleId) {
		this.clientMediaRuleId = clientMediaRuleId;
	}

	public String getClientSecurityRuleId() {
		return clientSecurityRuleId;
	}

	public void setClientSecurityRuleId(String clientSecurityRuleId) {
		this.clientSecurityRuleId = clientSecurityRuleId;
	}

	public String getUserFilteredSoftwareRuleId() {
		return userFilteredSoftwareRuleId;
	}

	public void setUserFilteredSoftwareRuleId(String userFilteredSoftwareRuleId) {
		this.userFilteredSoftwareRuleId = userFilteredSoftwareRuleId;
	}

	public String getClientFilteredSoftwareRuleId() {
		return clientFilteredSoftwareRuleId;
	}

	public void setClientFilteredSoftwareRuleId(String clientFilteredSoftwareRuleId) {
		this.clientFilteredSoftwareRuleId = clientFilteredSoftwareRuleId;
	}

	public String getDeptBrowserRuleId() {
		return deptBrowserRuleId;
	}

	public void setDeptBrowserRuleId(String deptBrowserRuleId) {
		this.deptBrowserRuleId = deptBrowserRuleId;
	}

	public String getDeptMediaRuleId() {
		return deptMediaRuleId;
	}

	public void setDeptMediaRuleId(String deptMediaRuleId) {
		this.deptMediaRuleId = deptMediaRuleId;
	}

	public String getDeptSecurityRuleId() {
		return deptSecurityRuleId;
	}

	public void setDeptSecurityRuleId(String deptSecurityRuleId) {
		this.deptSecurityRuleId = deptSecurityRuleId;
	}

	public String getDeptFilteredSoftwareRuleId() {
		return deptFilteredSoftwareRuleId;
	}

	public void setDeptFilteredSoftwareRuleId(String deptFilteredSoftwareRuleId) {
		this.deptFilteredSoftwareRuleId = deptFilteredSoftwareRuleId;
	}

	public String getUserCtrlCenterItemRuleId() {
		return userCtrlCenterItemRuleId;
	}

	public void setUserCtrlCenterItemRuleId(String userCtrlCenterItemRuleId) {
		this.userCtrlCenterItemRuleId = userCtrlCenterItemRuleId;
	}

	public String getDeptCtrlCenterItemRuleId() {
		return deptCtrlCenterItemRuleId;
	}

	public void setDeptCtrlCenterItemRuleId(String deptCtrlCenterItemRuleId) {
		this.deptCtrlCenterItemRuleId = deptCtrlCenterItemRuleId;
	}

	public String getClientCtrlCenterItemRuleId() {
		return clientCtrlCenterItemRuleId;
	}

	public void setClientCtrlCenterItemRuleId(String clientCtrlCenterItemRuleId) {
		this.clientCtrlCenterItemRuleId = clientCtrlCenterItemRuleId;
	}

	public String getUserPolicyKitRuleId() {
		return userPolicyKitRuleId;
	}

	public void setUserPolicyKitRuleId(String userPolicyKitRuleId) {
		this.userPolicyKitRuleId = userPolicyKitRuleId;
	}

	public String getDeptPolicyKitRuleId() {
		return deptPolicyKitRuleId;
	}

	public void setDeptPolicyKitRuleId(String deptPolicyKitRuleId) {
		this.deptPolicyKitRuleId = deptPolicyKitRuleId;
	}

	public String getClientPolicyKitRuleId() {
		return clientPolicyKitRuleId;
	}

	public void setClientPolicyKitRuleId(String clientPolicyKitRuleId) {
		this.clientPolicyKitRuleId = clientPolicyKitRuleId;
	}

	
}
