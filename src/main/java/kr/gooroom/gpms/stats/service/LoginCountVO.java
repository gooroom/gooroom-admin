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

/**
 * user login count data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class LoginCountVO implements Serializable {

	private String userAll = "";
	private String userSuccess = "";
	private String clientAll = "";
	private String clientSuccess = "";

	private String loginAll = "";
	private String loginSuccess = "";
	private String loginFail = "";

	private String logDate = null;

	public String getUserAll() {
		return userAll;
	}

	public void setUserAll(String userAll) {
		this.userAll = userAll;
	}

	public String getUserSuccess() {
		return userSuccess;
	}

	public void setUserSuccess(String userSuccess) {
		this.userSuccess = userSuccess;
	}

	public String getClientAll() {
		return clientAll;
	}

	public void setClientAll(String clientAll) {
		this.clientAll = clientAll;
	}

	public String getClientSuccess() {
		return clientSuccess;
	}

	public void setClientSuccess(String clientSuccess) {
		this.clientSuccess = clientSuccess;
	}

	public String getLoginAll() {
		return loginAll;
	}

	public void setLoginAll(String loginAll) {
		this.loginAll = loginAll;
	}

	public String getLoginSuccess() {
		return loginSuccess;
	}

	public void setLoginSuccess(String loginSuccess) {
		this.loginSuccess = loginSuccess;
	}

	public String getLoginFail() {
		return loginFail;
	}

	public void setLoginFail(String loginFail) {
		this.loginFail = loginFail;
	}

	public String getLogDate() {
		return logDate;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

}
