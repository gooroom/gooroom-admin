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

package kr.gooroom.gpms.site.service;

import java.io.Serializable;
import java.util.Date;

/**
 * administrator user data bean
 * 
 * @author HNC
 */

@SuppressWarnings("serial")
public class SiteMngVO implements Serializable {

	private String siteId;
	private String siteNm;

	private String comment;
	private String status;
	private String pollingCycle;
	private String serverVersion;

	private String rootDeptCd;
	private String rootGrpId;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteNm() {
		return siteNm;
	}

	public void setSiteNm(String siteNm) {
		this.siteNm = siteNm;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPollingCycle() {
		return pollingCycle;
	}

	public void setPollingCycle(String pollingCycle) {
		this.pollingCycle = pollingCycle;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getRootDeptCd() {
		return rootDeptCd;
	}

	public void setRootDeptCd(String rootDeptCd) {
		this.rootDeptCd = rootDeptCd;
	}

	public String getRootGrpId() {
		return rootGrpId;
	}

	public void setRootGrpId(String rootGrpId) {
		this.rootGrpId = rootGrpId;
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

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

}
