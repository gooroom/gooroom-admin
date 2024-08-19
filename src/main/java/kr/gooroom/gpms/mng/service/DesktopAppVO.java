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

package kr.gooroom.gpms.mng.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Desktop application data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class DesktopAppVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -998904134266018632L;
	private String appId;
	private String appNm;

	private String appInfo;

	private String appGubun;

	private String appExec;
	private String appMountUrl;
	private String appMountPoint;

	private String iconGubun;

	private String iconId;
	private String iconUrl;
	private String iconNm;
	private String iconGrpId;
	private String iconGrpNm;
	private String fileNm;
	private String filePath;

	private String status;
	private String statusCd;
	private String modUserId;
	private Date modDate;
	private String regUserId;
	private Date regDate;

	private String newAppId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppNm() {
		return appNm;
	}

	public void setAppNm(String appNm) {
		this.appNm = appNm;
	}

	public String getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}

	public String getAppGubun() {
		return appGubun;
	}

	public void setAppGubun(String appGubun) {
		this.appGubun = appGubun;
	}

	public String getAppExec() {
		return appExec;
	}

	public void setAppExec(String appExec) {
		this.appExec = appExec;
	}

	public String getAppMountUrl() {
		return appMountUrl;
	}

	public void setAppMountUrl(String appMountUrl) {
		this.appMountUrl = appMountUrl;
	}

	public String getAppMountPoint() {
		return appMountPoint;
	}

	public void setAppMountPoint(String appMountPoint) {
		this.appMountPoint = appMountPoint;
	}

	public String getIconGubun() {
		return iconGubun;
	}

	public void setIconGubun(String iconGubun) {
		this.iconGubun = iconGubun;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconNm() {
		return iconNm;
	}

	public void setIconNm(String iconNm) {
		this.iconNm = iconNm;
	}

	public String getIconGrpId() {
		return iconGrpId;
	}

	public void setIconGrpId(String iconGrpId) {
		this.iconGrpId = iconGrpId;
	}

	public String getIconGrpNm() {
		return iconGrpNm;
	}

	public void setIconGrpNm(String iconGrpNm) {
		this.iconGrpNm = iconGrpNm;
	}

	public String getFileNm() {
		return fileNm;
	}

	public void setFileNm(String fileNm) {
		this.fileNm = fileNm;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
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

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getNewAppId() {
		return newAppId;
	}

	public void setNewAppId(String newAppId) {
		this.newAppId = newAppId;
	}

}
