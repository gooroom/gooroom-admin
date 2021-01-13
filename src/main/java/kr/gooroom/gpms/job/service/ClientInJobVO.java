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

package kr.gooroom.gpms.job.service;

import java.io.Serializable;

/**
 * client data bean in job
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ClientInJobVO implements Serializable {

	private String jobNo;
	private String clientId;

	private String jobStat;
	private String grpNm;

	private String resultData;

	private String isOn;

	public String getJobNo() {
		return jobNo;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getJobStat() {
		return jobStat;
	}

	public void setJobStat(String jobStat) {
		this.jobStat = jobStat;
	}

	public String getGrpNm() {
		return grpNm;
	}

	public void setGrpNm(String grpNm) {
		this.grpNm = grpNm;
	}

	public String getResultData() {
		return resultData;
	}

	public void setResultData(String resultData) {
		this.resultData = resultData;
	}

	public String getIsOn() {
		return isOn;
	}

	public void setIsOn(String isOn) {
		this.isOn = isOn;
	}

}
