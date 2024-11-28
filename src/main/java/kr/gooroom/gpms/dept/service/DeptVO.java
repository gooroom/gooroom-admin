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

package kr.gooroom.gpms.dept.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * department data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class DeptVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 5465873033393267619L;
	private String deptCd;
	private String deptNm;

	private String uprDeptCd;
	private Date uprExpireDate;

	private String whleDeptCd;
	private String deptLevel;

	private String optYn;
	private String sortOrder;

	private String itemCount;
	private String itemTotalCount;

	private String desktopConfId;
	private String desktopConfNm;

	private String browserRuleId;
	private String securityRuleId;
	private String mediaRuleId;
	private String filteredSoftwareRuleId;
	private String ctrlCenterItemRuleId;
	private String policyKitRuleId;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	private Boolean hasChildren;
	private String paramIsInherit;
	
	private Date expireDate;
	private String isExpired;
	private String isUseExpire;

	public DeptVO() {
		this.deptCd = "";
		this.deptNm = "";
		this.uprDeptCd = "";
		this.sortOrder = "";
		this.optYn = "";
	}

	public DeptVO(String deptCd, String deptNm) {
		this.deptCd = deptCd;
		this.deptNm = deptNm;
		this.sortOrder = "1";
		this.optYn = "y";
	}

	public DeptVO(String deptCd, String deptNm, String uprDeptCd) {
		this.deptCd = deptCd;
		this.deptNm = deptNm;
		this.uprDeptCd = uprDeptCd;
		this.sortOrder = "1";
		this.optYn = "y";
	}

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	public String getItemTotalCount() {
		return itemTotalCount;
	}

	public void setItemTotalCount(String itemTotalCount) {
		this.itemTotalCount = itemTotalCount;
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

	public String getUprDeptCd() {
		return uprDeptCd;
	}

	public void setUprDeptCd(String uprDeptCd) {
		this.uprDeptCd = uprDeptCd;
	}

	public Date getUprExpireDate() {
		return uprExpireDate;
	}

	public void setUprExpireDate(Date uprExpireDate) {
		this.uprExpireDate = uprExpireDate;
	}

	public String getWhleDeptCd() {
		return whleDeptCd;
	}

	public void setWhleDeptCd(String whleDeptCd) {
		this.whleDeptCd = whleDeptCd;
	}

	public int getDeptLevelInt() {
		return Integer.parseInt(deptLevel);
	}

	public String getDeptLevel() {
		return deptLevel;
	}

	public void setDeptLevel(String deptLevel) {
		this.deptLevel = deptLevel;
	}

	public String getOptYn() {
		return optYn;
	}

	public void setOptYn(String optYn) {
		this.optYn = optYn;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getDesktopConfId() {
		return desktopConfId;
	}

	public void setDesktopConfId(String desktopConfId) {
		this.desktopConfId = desktopConfId;
	}

	public String getDesktopConfNm() {
		return desktopConfNm;
	}

	public void setDesktopConfNm(String desktopConfNm) {
		this.desktopConfNm = desktopConfNm;
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

	public Boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
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

	public String getParamIsInherit() {
		return paramIsInherit;
	}

	public void setParamIsInherit(String paramIsInherit) {
		this.paramIsInherit = paramIsInherit;
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

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(String isExpired) {
		this.isExpired = isExpired;
	}

	public String getIsUseExpire() {
		return isUseExpire;
	}

	public void setIsUseExpire(String isUseExpire) {
		this.isUseExpire = isUseExpire;
	}

}
