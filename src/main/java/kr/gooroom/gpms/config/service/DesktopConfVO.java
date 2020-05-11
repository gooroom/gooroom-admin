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

package kr.gooroom.gpms.config.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import kr.gooroom.gpms.mng.service.DesktopAppVO;

/**
 * Desktop configuration data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class DesktopConfVO implements Serializable {

	private String confId;
	private String confNm;
	private String confInfo;
	private boolean standardConf = false;

	private Date regDate;
	private String regUserId;

	private Date modDate;
	private String modUserId;

	private String mngObjTp;
	private String mngObjTpAbbr;

	private String themeId;
	private String themeNm;

	private String appId;
	private String appOrder;

	private List<DesktopAppVO> apps;

	private String newConfId;
	private String confGrade;

	public String getNewConfId() {
		return newConfId;
	}

	public void setNewConfId(String newConfId) {
		this.newConfId = newConfId;
	}

	public String getConfNm() {
		return confNm;
	}

	public void setConfNm(String confNm) {
		this.confNm = confNm;
	}

	public String getConfId() {
		return confId;
	}

	public void setConfId(String confId) {
		this.confId = confId;
	}

	public String getConfInfo() {
		return confInfo;
	}

	public void setConfInfo(String confInfo) {
		this.confInfo = confInfo;
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

	public String getMngObjTp() {
		return mngObjTp;
	}

	public void setMngObjTp(String mngObjTp) {
		this.mngObjTp = mngObjTp;
	}

	public String getMngObjTpAbbr() {
		return mngObjTpAbbr;
	}

	public void setMngObjTpAbbr(String mngObjTpAbbr) {
		this.mngObjTpAbbr = mngObjTpAbbr;
	}

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<DesktopAppVO> getApps() {
		return apps;
	}

	public void setApps(List<DesktopAppVO> apps) {
		this.apps = apps;
	}

	public String getThemeNm() {
		return themeNm;
	}

	public void setThemeNm(String themeNm) {
		this.themeNm = themeNm;
	}

	public String getAppOrder() {
		return appOrder;
	}

	public void setAppOrder(String appOrder) {
		this.appOrder = appOrder;
	}

	public String getConfGrade() {
		return confGrade;
	}

	public void setConfGrade(String confGrade) {
		this.confGrade = confGrade;
	}

	public boolean isStandardConf() {
		return standardConf;
	}

	public void setStandardConf(boolean standardConf) {
		this.standardConf = standardConf;
	}

}
