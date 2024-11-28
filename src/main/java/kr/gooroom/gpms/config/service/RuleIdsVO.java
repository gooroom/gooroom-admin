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

/**
 * protector attack information data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class RuleIdsVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -6443770292940860619L;
	private String desktopConfId;
	private String securityRuleId;
	private String updateServerConfId;
	private String hostNameConfId;
	private String clientConfId;
	private String browserRuleId;
	private String mediaRuleId;
	private String filteredSwRuleId;
	private String ctrlCenterItemRuleId;
	private String policyKitRuleId;

	public String getDesktopConfId() {
		return desktopConfId;
	}

	public void setDesktopConfId(String desktopConfId) {
		this.desktopConfId = desktopConfId;
	}

	public String getSecurityRuleId() {
		return securityRuleId;
	}

	public void setSecurityRuleId(String securityRuleId) {
		this.securityRuleId = securityRuleId;
	}

	public String getUpdateServerConfId() {
		return updateServerConfId;
	}

	public void setUpdateServerConfId(String updateServerConfId) {
		this.updateServerConfId = updateServerConfId;
	}

	public String getHostNameConfId() {
		return hostNameConfId;
	}

	public void setHostNameConfId(String hostNameConfId) {
		this.hostNameConfId = hostNameConfId;
	}

	public String getClientConfId() {
		return clientConfId;
	}

	public void setClientConfId(String clientConfId) {
		this.clientConfId = clientConfId;
	}

	public String getBrowserRuleId() {
		return browserRuleId;
	}

	public void setBrowserRuleId(String browserRuleId) {
		this.browserRuleId = browserRuleId;
	}

	public String getMediaRuleId() {
		return mediaRuleId;
	}

	public void setMediaRuleId(String mediaRuleId) {
		this.mediaRuleId = mediaRuleId;
	}

	public String getFilteredSwRuleId() {
		return filteredSwRuleId;
	}

	public void setFilteredSwRuleId(String filteredSwRuleId) {
		this.filteredSwRuleId = filteredSwRuleId;
	}

	public String getCtrlCenterItemRuleId() {
		return ctrlCenterItemRuleId;
	}

	public void setCtrlCenterItemRuleId(String ctrlCenterItemRuleId) {
		this.ctrlCenterItemRuleId = ctrlCenterItemRuleId;
	}

	public String getPolicyKitRuleId() {
		return policyKitRuleId;
	}

	public void setPolicyKitRuleId(String policyKitRuleId) {
		this.policyKitRuleId = policyKitRuleId;
	}

}
