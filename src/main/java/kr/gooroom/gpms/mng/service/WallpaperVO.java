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

/**
 * Wallpaper data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class WallpaperVO implements Serializable {

	private String wallpaperId;
	private String wallpaperNm;
	private String iconCmt;

	private String wallpaperUrl;

	private String fileNo;
	private String fileNm;
	private String filePath;

	private String modUserId;
	private Date modDate;

	public String getWallpaperId() {
		return wallpaperId;
	}

	public void setWallpaperId(String wallpaperId) {
		this.wallpaperId = wallpaperId;
	}

	public String getWallpaperNm() {
		return wallpaperNm;
	}

	public void setWallpaperNm(String wallpaperNm) {
		this.wallpaperNm = wallpaperNm;
	}

	public String getIconCmt() {
		return iconCmt;
	}

	public void setIconCmt(String iconCmt) {
		this.iconCmt = iconCmt;
	}

	public String getWallpaperUrl() {
		return wallpaperUrl;
	}

	public void setWallpaperUrl(String wallpaperUrl) {
		this.wallpaperUrl = wallpaperUrl;
	}

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
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

}
