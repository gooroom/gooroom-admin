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
 * manage server address data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class SiteConfVO implements Serializable {

	private String siteId;
	private String siteName;

	private String pollingCycle;
	private String serverVersion;
	private String trialCount;
	private String lockTime;
	private String passwordRule;
	private String enableDuplicateLogin;

	private Date modDate;
	private String modUserId;


	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getPollingCycle() {
		return pollingCycle;
	}

	public void setPollingCycle(String pollingCycle) {
		this.pollingCycle = pollingCycle;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getTrialCount() {
		return trialCount;
	}

	public void setTrialCount(String trialCount) {
		this.trialCount = trialCount;
	}

	public String getLockTime() {
		return lockTime;
	}

	public void setLockTime(String lockTime) {
		this.lockTime = lockTime;
	}

	public String getPasswordRule() {
		return passwordRule;
	}

	public void setPasswordRule(String passwordRule) {
		this.passwordRule = passwordRule;
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

	public String getEnableDuplicateLogin() {
		return enableDuplicateLogin;
	}

	public void setEnableDuplicateLogin(String enableDuplicateLogin) {
		this.enableDuplicateLogin = enableDuplicateLogin;
	}
	
	

}
