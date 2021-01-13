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

@SuppressWarnings("serial")
public class ClientProfileSetVO implements Serializable {

	private String clientId;
	private String clientNm;

	private String profileNo;
	private String profileNm;
	private String profileCmt;

	private Date modDate;
	private String modUserId;
	private Date regDate;
	private String regUserId;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientNm() {
		return clientNm;
	}

	public void setClientNm(String clientNm) {
		this.clientNm = clientNm;
	}

	public String getProfileNo() {
		return profileNo;
	}

	public void setProfileNo(String profileNo) {
		this.profileNo = profileNo;
	}

	public String getProfileNm() {
		return profileNm;
	}

	public void setProfileNm(String profileNm) {
		this.profileNm = profileNm;
	}

	public String getProfileCmt() {
		return profileCmt;
	}

	public void setProfileCmt(String profileCmt) {
		this.profileCmt = profileCmt;
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

}
