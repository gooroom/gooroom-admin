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

package kr.gooroom.gpms.config.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * client setup configuration data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class ClientConfDataVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 8387716574640688952L;
	private String pmId;

	private String repoData;

	private String mainLabel;
	private String mainOsData;
	private String extOsData;
	private String priorityData;
	private String hostsData;

	private Date modDate;
	private String modUserId;

	private String comment;

	public String getMainLabel() {
		return mainLabel;
	}

	public void setMainLabel(String mainLabel) {
		this.mainLabel = mainLabel;
	}

	public String getRepoData() {
		return repoData;
	}

	public void setRepoData(String repoData) {
		this.repoData = repoData;
	}

	public String getPmId() {
		return pmId;
	}

	public void setPmId(String pmId) {
		this.pmId = pmId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public String getMainOsData() {
		return mainOsData;
	}

	public void setMainOsData(String mainOsData) {
		this.mainOsData = mainOsData;
	}

	public String getExtOsData() {
		return extOsData;
	}

	public void setExtOsData(String extOsData) {
		this.extOsData = extOsData;
	}

	public String getHostsData() {
		return hostsData;
	}

	public void setHostsData(String hostsData) {
		this.hostsData = hostsData;
	}

	public String getPriorityData() {
		return priorityData;
	}

	public void setPriorityData(String priorityData) {
		this.priorityData = priorityData;
	}

}
