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

package kr.gooroom.gpms.client.service;

import java.io.Serializable;
import java.util.Date;

/**
 * client data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ClientVO implements Serializable {

	// view
	private String nullColumn = "";

	private String clientId;
	private String clientStatus;
	private String viewStatus;

	private String clientIp;
	private String prodNo;

	private String patchVersion;

	private String clientGroupName;
	private Date regDate;

	private String clientName;
	private String keyRenewDate;
	private Date modDate;
	private String regUserId;
	private String modUserId;
	private String comment;

	private String clientGroupId;

	private String keyInfo;
	private String keyDateFrom;
	private String keyDateTo;
	private String keySerialNO;

	private String isOn;
	private String loginId;
	private Date lastLoginTime;

	private String safeScore;
	private String strgSize;
	private String strgUse;

	private String isProtector;
	private String countBootProtector;
	private String countExeProtector;
	private String countOsProtector;
	private String countMediaProtector;

	private String isStopBootProtector;
	private String isStopExeProtector;
	private String isStopOsProtector;
	private String isStopMediaProtector;

	private String totalCnt;
	private String updateTargetCnt;
	private String updateMainOsCnt;

	private String useHomeReset;
	private String rootAllow;
	private String sudoAllow;

	public String getIsProtector() {
		return isProtector;
	}

	public void setIsProtector(String isProtector) {
		this.isProtector = isProtector;
	}

	public String getSafeScore() {
		return safeScore;
	}

	public void setSafeScore(String safeScore) {
		this.safeScore = safeScore;
	}

	public String getCountBootProtector() {
		return countBootProtector;
	}

	public void setCountBootProtector(String countBootProtector) {
		this.countBootProtector = countBootProtector;
	}

	public String getCountExeProtector() {
		return countExeProtector;
	}

	public void setCountExeProtector(String countExeProtector) {
		this.countExeProtector = countExeProtector;
	}

	public String getCountOsProtector() {
		return countOsProtector;
	}

	public void setCountOsProtector(String countOsProtector) {
		this.countOsProtector = countOsProtector;
	}

	public String getCountMediaProtector() {
		return countMediaProtector;
	}

	public void setCountMediaProtector(String countMediaProtector) {
		this.countMediaProtector = countMediaProtector;
	}

	public String getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(String totalCnt) {
		this.totalCnt = totalCnt;
	}

	public String getUpdateTargetCnt() {
		return updateTargetCnt;
	}

	public void setUpdateTargetCnt(String updateTargetCnt) {
		this.updateTargetCnt = updateTargetCnt;
	}

	public String getIsOn() {
		return isOn;
	}

	public void setIsOn(String isOn) {
		this.isOn = isOn;
	}

	public String getNullColumn() {
		return nullColumn;
	}

	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}

	public String getClientGroupName() {
		return clientGroupName;
	}

	public void setClientGroupName(String clientGroupName) {
		this.clientGroupName = clientGroupName;
	}

	public String getClientGroupId() {
		return clientGroupId;
	}

	public void setClientGroupId(String clientGroupId) {
		this.clientGroupId = clientGroupId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getKeyInfo() {
		return keyInfo;
	}

	public void setKeyInfo(String keyInfo) {
		this.keyInfo = keyInfo;
	}

	public String getKeyRenewDate() {
		return keyRenewDate;
	}

	public void setKeyRenewDate(String keyRenewDate) {
		this.keyRenewDate = keyRenewDate;
	}

	public String getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(String clientStatus) {
		this.clientStatus = clientStatus;
	}

	public String getViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(String viewStatus) {
		this.viewStatus = viewStatus;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getProdNo() {
		return prodNo;
	}

	public void setProdNo(String prodNo) {
		this.prodNo = prodNo;
	}

	public String getKeyDateFrom() {
		return keyDateFrom;
	}

	public void setKeyDateFrom(String keyDateFrom) {
		this.keyDateFrom = keyDateFrom;
	}

	public String getKeyDateTo() {
		return keyDateTo;
	}

	public void setKeyDateTo(String keyDateTo) {
		this.keyDateTo = keyDateTo;
	}

	public String getKeySerialNO() {
		return keySerialNO;
	}

	public void setKeySerialNO(String keySerialNO) {
		this.keySerialNO = keySerialNO;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getIsStopBootProtector() {
		return isStopBootProtector;
	}

	public void setIsStopBootProtector(String isStopBootProtector) {
		this.isStopBootProtector = isStopBootProtector;
	}

	public String getIsStopExeProtector() {
		return isStopExeProtector;
	}

	public void setIsStopExeProtector(String isStopExeProtector) {
		this.isStopExeProtector = isStopExeProtector;
	}

	public String getIsStopOsProtector() {
		return isStopOsProtector;
	}

	public void setIsStopOsProtector(String isStopOsProtector) {
		this.isStopOsProtector = isStopOsProtector;
	}

	public String getIsStopMediaProtector() {
		return isStopMediaProtector;
	}

	public void setIsStopMediaProtector(String isStopMediaProtector) {
		this.isStopMediaProtector = isStopMediaProtector;
	}

	public String getUpdateMainOsCnt() {
		return updateMainOsCnt;
	}

	public void setUpdateMainOsCnt(String updateMainOsCnt) {
		this.updateMainOsCnt = updateMainOsCnt;
	}

	public String getUseHomeReset() {
		return useHomeReset;
	}

	public void setUseHomeReset(String useHomeReset) {
		this.useHomeReset = useHomeReset;
	}

	public String getRootAllow() {
		return rootAllow;
	}

	public void setRootAllow(String rootAllow) {
		this.rootAllow = rootAllow;
	}
	
	public String getSudoAllow() {
		return sudoAllow;
	}

	public void setSudoAllow(String sudoAllow) {
		this.sudoAllow = sudoAllow;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getStrgSize() {
		return strgSize;
	}

	public void setStrgSize(String strgSize) {
		this.strgSize = strgSize;
	}

	public String getStrgUse() {
		return strgUse;
	}

	public void setStrgUse(String strgUse) {
		this.strgUse = strgUse;
	}

}
