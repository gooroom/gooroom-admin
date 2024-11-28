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

/**
 * client summary information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class ClientSummaryVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -6388831706862443722L;
	private String totalCount;
	private String onCount;
	private String offCount;
	private String revokeCount;

	private String loginCount;
	private String userCount;

	private String noUpdateCount;
	private String updateCount;
	private String mainUpdateCount;

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getOnCount() {
		return onCount;
	}

	public void setOnCount(String onCount) {
		this.onCount = onCount;
	}

	public String getOffCount() {
		return offCount;
	}

	public void setOffCount(String offCount) {
		this.offCount = offCount;
	}

	public String getRevokeCount() {
		return revokeCount;
	}

	public void setRevokeCount(String revokeCount) {
		this.revokeCount = revokeCount;
	}

	public String getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(String loginCount) {
		this.loginCount = loginCount;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getNoUpdateCount() {
		return noUpdateCount;
	}

	public void setNoUpdateCount(String noUpdateCount) {
		this.noUpdateCount = noUpdateCount;
	}

	public String getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(String updateCount) {
		this.updateCount = updateCount;
	}

	public String getMainUpdateCount() {
		return mainUpdateCount;
	}

	public void setMainUpdateCount(String mainUpdateCount) {
		this.mainUpdateCount = mainUpdateCount;
	}

}
