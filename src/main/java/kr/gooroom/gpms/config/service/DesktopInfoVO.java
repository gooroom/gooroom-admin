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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * desktop information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class DesktopInfoVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 2686135313038985647L;
	private String confId;
	private String confNm;
	private String confInfo;
	private String defaultYn;
	private String modUserId;
	private Date modDt;
	private String regUserId;
	private Date regDt;

	private String themeId;
	private String themeNm;
	private String wallpaperNm;
	private String wallpaperFile;

	private ArrayList<DesktopItemVO> apps;
	private ArrayList<DesktopMountInfoVO> mounts;

	private String confGrade;

	public String getConfId() {
		return confId;
	}

	public void setConfId(String confId) {
		this.confId = confId;
	}

	public String getConfNm() {
		return confNm;
	}

	public void setConfNm(String confNm) {
		this.confNm = confNm;
	}

	public String getConfInfo() {
		return confInfo;
	}

	public void setConfInfo(String confInfo) {
		this.confInfo = confInfo;
	}

	public String getDefaultYn() {
		return defaultYn;
	}

	public void setDefaultYn(String defaultYn) {
		this.defaultYn = defaultYn;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public Date getModDt() {
		return modDt;
	}

	public void setModDt(Date modDt) {
		this.modDt = modDt;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public Date getRegDt() {
		return regDt;
	}

	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public ArrayList<DesktopItemVO> getApps() {
		return apps;
	}

	public void setApps(ArrayList<DesktopItemVO> apps) {
		this.apps = apps;
	}

	public ArrayList<DesktopMountInfoVO> getMounts() {
		return mounts;
	}

	public void setMounts(ArrayList<DesktopMountInfoVO> mounts) {
		this.mounts = mounts;
	}

	public String getThemeNm() {
		return themeNm;
	}

	public void setThemeNm(String themeNm) {
		this.themeNm = themeNm;
	}

	public String getWallpaperNm() {
		return wallpaperNm;
	}

	public void setWallpaperNm(String wallpaperNm) {
		this.wallpaperNm = wallpaperNm;
	}

	public String getWallpaperFile() {
		return wallpaperFile;
	}

	public void setWallpaperFile(String wallpaperFile) {
		this.wallpaperFile = wallpaperFile;
	}

	public String getConfGrade() {
		return confGrade;
	}

	public void setConfGrade(String confGrade) {
		this.confGrade = confGrade;
	}

}
