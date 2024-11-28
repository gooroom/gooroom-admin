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

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * user login information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class LoginDataVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 2019914155090950343L;
	private String histSeq = "";
	private String clientGroup = "";
	private String clientId = "";
	private String deptName = "";
	private String userId = "";
	private String status = "";

	private Date regDate = null;

	public String getHistSeq() {
		return histSeq;
	}

	public void setHistSeq(String histSeq) {
		this.histSeq = histSeq;
	}

	public String getClientGroup() {
		return clientGroup;
	}

	public void setClientGroup(String clientGroup) {
		this.clientGroup = clientGroup;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

}
