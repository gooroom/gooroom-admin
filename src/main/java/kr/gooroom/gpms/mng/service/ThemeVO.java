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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import kr.gooroom.gpms.common.service.FileVO;

/**
 * Theme data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ThemeVO implements Serializable {

	private String themeId;
	private String themeNm;
	private String themeCmt;

	private String wallpaper;
	private String wallpaperFileNm;
	private String wallpaperUrl;

	// private HashMap<String, FileVO> themeIcons;
	private List<FileVO> themeIcons;

	private String modUserId;
	private Date modDate;
	private String regUserId;
	private Date regDate;

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getThemeNm() {
		return themeNm;
	}

	public void setThemeNm(String themeNm) {
		this.themeNm = themeNm;
	}

	public String getThemeCmt() {
		return themeCmt;
	}

	public void setThemeCmt(String themeCmt) {
		this.themeCmt = themeCmt;
	}

	public String getWallpaper() {
		return wallpaper;
	}

	public void setWallpaper(String wallpaper) {
		this.wallpaper = wallpaper;
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

	public List<FileVO> getThemeIcons() {
		return themeIcons;
	}

	public void setThemeIcons(List<FileVO> themeIcons) {
		this.themeIcons = themeIcons;
	}

	public String getWallpaperUrl() {
		return wallpaperUrl;
	}

	public void setWallpaperUrl(String wallpaperUrl) {
		this.wallpaperUrl = wallpaperUrl;
	}

	public String getWallpaperFileNm() {
		return wallpaperFileNm;
	}

	public void setWallpaperFileNm(String wallpaperFileNm) {
		this.wallpaperFileNm = wallpaperFileNm;
	}

}
