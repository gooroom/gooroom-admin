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

/**
 * client register and revoke count data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class ClientMngCountVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -1238396595857805783L;
	private String regCount = "";
	private String revokeCount = "";

	private String logDate = "";

	public String getRegCount() {
		return regCount;
	}

	public void setRegCount(String regCount) {
		this.regCount = regCount;
	}

	public String getRevokeCount() {
		return revokeCount;
	}

	public void setRevokeCount(String revokeCount) {
		this.revokeCount = revokeCount;
	}

	public String getLogDate() {
		return logDate;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

}
