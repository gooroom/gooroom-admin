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

package kr.gooroom.gpms.stats.service;

import java.io.Serializable;
import java.util.Date;

/**
 * protector attacked count data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ViolatedCountVO implements Serializable {

	private Integer protectorCount;
	private Integer bootProtectorCount;
	private Integer exeProtectorCount;
	private Integer osProtectorCount;
	private Integer mediaProtectorCount;

	private String logDateStr;
	private Date logDate = null;

	public Integer getProtectorCount() {
		return protectorCount;
	}

	public void setProtectorCount(Integer protectorCount) {
		this.protectorCount = protectorCount;
	}

	public Integer getBootProtectorCount() {
		return bootProtectorCount;
	}

	public void setBootProtectorCount(Integer bootProtectorCount) {
		this.bootProtectorCount = bootProtectorCount;
	}

	public Integer getExeProtectorCount() {
		return exeProtectorCount;
	}

	public void setExeProtectorCount(Integer exeProtectorCount) {
		this.exeProtectorCount = exeProtectorCount;
	}

	public Integer getOsProtectorCount() {
		return osProtectorCount;
	}

	public void setOsProtectorCount(Integer osProtectorCount) {
		this.osProtectorCount = osProtectorCount;
	}

	public Integer getMediaProtectorCount() {
		return mediaProtectorCount;
	}

	public void setMediaProtectorCount(Integer mediaProtectorCount) {
		this.mediaProtectorCount = mediaProtectorCount;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getLogDateStr() {
		return logDateStr;
	}

	public void setLogDateStr(String logDateStr) {
		this.logDateStr = logDateStr;
	}

}
