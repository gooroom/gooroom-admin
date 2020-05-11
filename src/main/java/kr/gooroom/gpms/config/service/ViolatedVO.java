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

/**
 * protector attack information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ViolatedVO implements Serializable {

	private String clientId;

	private String safeScore;

	private Date bootProtectorDt;
	private Date exeProtectorDt;
	private Date osProtectorDt;
	private Date mediaProtectorDt;
	private Date clientCheckDt;

	private Date confirmDt;
	private String confirmUserId;

	private Date modDate;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSafeScore() {
		return safeScore;
	}

	public void setSafeScore(String safeScore) {
		this.safeScore = safeScore;
	}

	public Date getBootProtectorDt() {
		return bootProtectorDt;
	}

	public void setBootProtectorDt(Date bootProtectorDt) {
		this.bootProtectorDt = bootProtectorDt;
	}

	public Date getExeProtectorDt() {
		return exeProtectorDt;
	}

	public void setExeProtectorDt(Date exeProtectorDt) {
		this.exeProtectorDt = exeProtectorDt;
	}

	public Date getOsProtectorDt() {
		return osProtectorDt;
	}

	public void setOsProtectorDt(Date osProtectorDt) {
		this.osProtectorDt = osProtectorDt;
	}

	public Date getMediaProtectorDt() {
		return mediaProtectorDt;
	}

	public void setMediaProtectorDt(Date mediaProtectorDt) {
		this.mediaProtectorDt = mediaProtectorDt;
	}

	public Date getClientCheckDt() {
		return clientCheckDt;
	}

	public void setClientCheckDt(Date clientCheckDt) {
		this.clientCheckDt = clientCheckDt;
	}

	public Date getConfirmDt() {
		return confirmDt;
	}

	public void setConfirmDt(Date confirmDt) {
		this.confirmDt = confirmDt;
	}

	public String getConfirmUserId() {
		return confirmUserId;
	}

	public void setConfirmUserId(String confirmUserId) {
		this.confirmUserId = confirmUserId;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

}
