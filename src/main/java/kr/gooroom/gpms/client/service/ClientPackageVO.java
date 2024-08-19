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

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * client package data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class ClientPackageVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -7664458400707081663L;
	// view
	private String nullColumn = "";

	private String clientId;
	private String packageId;
	private String packageArch;
	private String installVer;
	private String packageLastVer;

	private Date modDate;
	private String modUserId;

	private String totalCnt;
	private String updateTargetCnt;

	public String getNullColumn() {
		return nullColumn;
	}

	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getPackageArch() {
		return packageArch;
	}

	public void setPackageArch(String packageArch) {
		this.packageArch = packageArch;
	}

	public String getInstallVer() {
		return installVer;
	}

	public void setInstallVer(String installVer) {
		this.installVer = installVer;
	}

	public String getPackageLastVer() {
		return packageLastVer;
	}

	public void setPackageLastVer(String packageLastVer) {
		this.packageLastVer = packageLastVer;
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

}
