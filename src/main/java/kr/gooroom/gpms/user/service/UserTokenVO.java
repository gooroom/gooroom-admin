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

package kr.gooroom.gpms.user.service;

import java.io.Serializable;
import java.util.Date;

/**
 * user token information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class UserTokenVO implements Serializable {

	private static final long serialVersionUID = 3538239955449723982L;

	private String token;
	private String userId;
	private Date expirationDt;
	private String statusCd;
	private int authAllowCnt;
	private int authCnt;
	private String clientId;
	private Date modDt;
	private String nfcSecretData;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getExpirationDt() {
		return expirationDt;
	}

	public void setExpirationDt(Date expirationDt) {
		this.expirationDt = expirationDt;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public int getAuthAllowCnt() {
		return authAllowCnt;
	}

	public void setAuthAllowCnt(int authAllowCnt) {
		this.authAllowCnt = authAllowCnt;
	}

	public int getAuthCnt() {
		return authCnt;
	}

	public void setAuthCnt(int authCnt) {
		this.authCnt = authCnt;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Date getModDt() {
		return modDt;
	}

	public void setModDt(Date modDt) {
		this.modDt = modDt;
	}

	public String getNfcSecretData() {
		return nfcSecretData;
	}

	public void setNfcSecretData(String nfcSecretData) {
		this.nfcSecretData = nfcSecretData;
	}

}
