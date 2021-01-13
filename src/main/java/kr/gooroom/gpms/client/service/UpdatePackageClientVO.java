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

import java.io.Serializable;

/**
 * package data bean for update
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class UpdatePackageClientVO implements Serializable {

	private String clientId;
	private String clientNm;

	private String updateCount;
	private String mainUpdateCount;

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
