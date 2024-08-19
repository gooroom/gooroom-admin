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

package kr.gooroom.gpms.client.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * client data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class ClientStatsVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -1997956109531654417L;
	// view
	private String nullColumn = "";

	private String clientId;
	private String clientName;

	private String clientStatus;
	private String clientCnt;

	private String clientGroupId;
	private String clientGroupName;
	private String viewStatus;

	private Date regDate;
	private Date modDate;
	private String regUserId;
	private String modUserId;

	public String getNullColumn() {
		return nullColumn;
	}

	public void setNullColumn(String nullColumn) {
		this.nullColumn = nullColumn;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(String clientStatus) {
		this.clientStatus = clientStatus;
	}

	public String getClientCnt() {
		return clientCnt;
	}

	public void setClientCnt(String clientCnt) {
		this.clientCnt = clientCnt;
	}

	public String getClientGroupId() {
		return clientGroupId;
	}

	public void setClientGroupId(String clientGroupId) {
		this.clientGroupId = clientGroupId;
	}

	public String getClientGroupName() {
		return clientGroupName;
	}

	public void setClientGroupName(String clientGroupName) {
		this.clientGroupName = clientGroupName;
	}
	
	public String getViewStatus() {
		return viewStatus;
	}

	public void setViewStatus(String viewStatus) {
		this.viewStatus = viewStatus;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

}
