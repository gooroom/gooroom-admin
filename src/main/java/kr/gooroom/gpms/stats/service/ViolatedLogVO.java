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
 * violated log data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ViolatedLogVO implements Serializable {

	private String logSeq = "";
	private String clientId = "";
	private String userId = "";
	private String logItem = "";
	private String logTp = "";
	private String logValue = "";
	private Date logDate = null;
	private Date regDate = null;

	public String getLogSeq() {
		return logSeq;
	}

	public void setLogSeq(String logSeq) {
		this.logSeq = logSeq;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogItem() {
		return logItem;
	}

	public void setLogItem(String logItem) {
		this.logItem = logItem;
	}

	public String getLogTp() {
		return logTp;
	}

	public void setLogTp(String logTp) {
		this.logTp = logTp;
	}

	public String getLogValue() {
		return logValue;
	}

	public void setLogValue(String logValue) {
		this.logValue = logValue;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

}
