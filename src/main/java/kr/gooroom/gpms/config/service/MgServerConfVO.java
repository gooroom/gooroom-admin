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
public class MgServerConfVO implements Serializable {

	private String kmIp;
	private String kmUrl;

	private String lmIp;
	private String lmUrl;

	private String rmIp;
	private String rmUrl;

	private String pmIp;
	private String pmUrl;

	private String pollingTime;
	private String trialCount;
	private String lockTime;
	private String passwordRule;
	private String enableDuplicateLogin;

	private String maxMediaCnt;
	private String registerReq;
	private String deleteReq;

	private Date modDate;
	private String modUserId;

	public String getKmIp() {
		return kmIp;
	}

	public void setKmIp(String kmIp) {
		this.kmIp = kmIp;
	}

	public String getKmUrl() {
		return kmUrl;
	}

	public void setKmUrl(String kmUrl) {
		this.kmUrl = kmUrl;
	}

	public String getLmIp() {
		return lmIp;
	}

	public void setLmIp(String lmIp) {
		this.lmIp = lmIp;
	}

	public String getLmUrl() {
		return lmUrl;
	}

	public void setLmUrl(String lmUrl) {
		this.lmUrl = lmUrl;
	}

	public String getRmIp() {
		return rmIp;
	}

	public void setRmIp(String rmIp) {
		this.rmIp = rmIp;
	}

	public String getRmUrl() {
		return rmUrl;
	}

	public void setRmUrl(String rmUrl) {
		this.rmUrl = rmUrl;
	}

	public String getPmIp() {
		return pmIp;
	}

	public void setPmIp(String pmIp) {
		this.pmIp = pmIp;
	}

	public String getPmUrl() {
		return pmUrl;
	}

	public void setPmUrl(String pmUrl) {
		this.pmUrl = pmUrl;
	}

	public String getPollingTime() {
		return pollingTime;
	}

	public void setPollingTime(String pollingTime) {
		this.pollingTime = pollingTime;
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

	public String getEnableDuplicateLogin() {
		return enableDuplicateLogin;
	}

	public void setEnableDuplicateLogin(String enableDuplicateLogin) {
		this.enableDuplicateLogin = enableDuplicateLogin;
	}

	public String getMaxMediaCnt() {
		return maxMediaCnt;
	}

	public void setMaxMediaCnt(String maxMediaCnt) {
		this.maxMediaCnt = maxMediaCnt;
	}

	public String getRegisterReq() {
		return registerReq;
	}

	public void setRegisterReq(String registerReq) {
		this.registerReq = registerReq;
	}

	public String getDeleteReq() {
		return deleteReq;
	}

	public void setDeleteReq(String deleteReq) {
		this.deleteReq = deleteReq;
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

}
