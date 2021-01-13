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

package kr.gooroom.gpms.account.service;

import java.io.Serializable;
import java.util.Date;

/**
 * history data bean from admin doing.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ActHistoryVO implements Serializable {

	String logSeq;

	String actTp;
	String actItem;
	String actData;
	String accessIp;
	String actUserId;

	Date actDt;

	public String getLogSeq() {
		return logSeq;
	}

	public void setLogSeq(String logSeq) {
		this.logSeq = logSeq;
	}

	public String getActTp() {
		return actTp;
	}

	public void setActTp(String actTp) {
		this.actTp = actTp;
	}

	public String getActItem() {
		return actItem;
	}

	public void setActItem(String actItem) {
		this.actItem = actItem;
	}

	public String getActData() {
		return actData;
	}

	public void setActData(String actData) {
		this.actData = actData;
	}

	public String getAccessIp() {
		return accessIp;
	}

	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}

	public String getActUserId() {
		return actUserId;
	}

	public void setActUserId(String actUserId) {
		this.actUserId = actUserId;
	}

	public Date getActDt() {
		return actDt;
	}

	public void setActDt(Date actDt) {
		this.actDt = actDt;
	}

}
