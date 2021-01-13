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

package kr.gooroom.gpms.log.service;

import java.io.Serializable;
import java.util.Date;

/**
 * client log data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ClientLogVO implements Serializable {

	String logSeq;

	String clientId;
	String userId;
	String logTp;
	String logItem;
	String logValue;

	Date regDt;

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

	public String getLogTp() {
		return logTp;
	}

	public void setLogTp(String logTp) {
		this.logTp = logTp;
	}

	public String getLogItem() {
		return logItem;
	}

	public void setLogItem(String logItem) {
		this.logItem = logItem;
	}

	public String getLogValue() {
		return logValue;
	}

	public void setLogValue(String logValue) {
		this.logValue = logValue;
	}

	public Date getRegDt() {
		return regDt;
	}

	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}

}
