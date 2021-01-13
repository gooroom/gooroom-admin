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
 * client group data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ClientGroupVO implements Serializable {

	private String nullColumn;

	private String grpId;
	private String grpNm;
	private String comment;
	private String regClientIp;

	private String uprGrpId;
	private String whleGrpId;
	private String grpLevel;

	private Date regDate;
	private String regUserId;

	private Date modDate;
	private String modUserId;

	private int itemTotalCount;
	private int itemCount;

	private String desktopConfigId;
	private String desktopConfigNm;
	private String desktopConfigInfo;

	private String clientConfigId;
	private String clientConfigNm;

	private String hostNameConfigId;
	private String hostNameConfigNm;

	private String updateServerConfigId;
	private String updateServerConfigNm;

	private String mediaRuleId;
	private String browserRuleId;
	private String securityRuleId;
	private String filteredSoftwareRuleId;
	private String ctrlCenterItemRuleId;
	private String policyKitRuleId;

	private Boolean hasChildren;

	public String getNullColumn() {
		return nullColumn;
	}

	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getGrpNm() {
		return grpNm;
	}

	public void setGrpNm(String grpNm) {
		this.grpNm = grpNm;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRegClientIp() {
		return regClientIp;
	}

	public void setRegClientIp(String regClientIp) {
		this.regClientIp = regClientIp;
	}

	public String getUprGrpId() {
		return uprGrpId;
	}

	public void setUprGrpId(String uprGrpId) {
		this.uprGrpId = uprGrpId;
	}

	public String getWhleGrpId() {
		return whleGrpId;
	}

	public void setWhleGrpId(String whleGrpId) {
		this.whleGrpId = whleGrpId;
	}

	public String getGrpLevel() {
		return grpLevel;
	}

	public void setGrpLevel(String grpLevel) {
		this.grpLevel = grpLevel;
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

	public int getItemTotalCount() {
		return itemTotalCount;
	}

	public void setItemTotalCount(int itemTotalCount) {
		this.itemTotalCount = itemTotalCount;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public String getDesktopConfigId() {
		return desktopConfigId;
	}

	public void setDesktopConfigId(String desktopConfigId) {
		this.desktopConfigId = desktopConfigId;
	}

	public String getDesktopConfigNm() {
		return desktopConfigNm;
	}

	public void setDesktopConfigNm(String desktopConfigNm) {
		this.desktopConfigNm = desktopConfigNm;
	}

	public String getDesktopConfigInfo() {
		return desktopConfigInfo;
	}

	public void setDesktopConfigInfo(String desktopConfigInfo) {
		this.desktopConfigInfo = desktopConfigInfo;
	}

	public String getClientConfigId() {
		return clientConfigId;
	}

	public void setClientConfigId(String clientConfigId) {
		this.clientConfigId = clientConfigId;
	}

	public String getClientConfigNm() {
		return clientConfigNm;
	}

	public void setClientConfigNm(String clientConfigNm) {
		this.clientConfigNm = clientConfigNm;
	}

	public String getHostNameConfigId() {
		return hostNameConfigId;
	}

	public void setHostNameConfigId(String hostNameConfigId) {
		this.hostNameConfigId = hostNameConfigId;
	}

	public String getHostNameConfigNm() {
		return hostNameConfigNm;
	}

	public void setHostNameConfigNm(String hostNameConfigNm) {
		this.hostNameConfigNm = hostNameConfigNm;
	}

	public String getUpdateServerConfigId() {
		return updateServerConfigId;
	}

	public void setUpdateServerConfigId(String updateServerConfigId) {
		this.updateServerConfigId = updateServerConfigId;
	}

	public String getUpdateServerConfigNm() {
		return updateServerConfigNm;
	}

	public void setUpdateServerConfigNm(String updateServerConfigNm) {
		this.updateServerConfigNm = updateServerConfigNm;
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

	public Boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

}
