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

package kr.gooroom.gpms.mng.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class ClientSoftwareVO implements Serializable {

	@Serial
	private static final long serialVersionUID = -8363582080737543690L;
	private String swId;

	private String swTag;
	private String swName;
	private String swNameForKorean;

	private String swFilter;

	private Date regDate;
	private String regUserId;
	private Date modDate;
	private String modUserId;

	public String getSwId() {
		return swId;
	}

	public void setSwId(String swId) {
		this.swId = swId;
	}

	public String getSwTag() {
		return swTag;
	}

	public void setSwTag(String swTag) {
		this.swTag = swTag;
	}

	public String getSwName() {
		return swName;
	}

	public void setSwName(String swName) {
		this.swName = swName;
	}

	public String getSwNameForKorean() {
		return swNameForKorean;
	}

	public void setSwNameForKorean(String swNameForKorean) {
		this.swNameForKorean = swNameForKorean;
	}

	public String getSwFilter() {
		return swFilter;
	}

	public void setSwFilter(String swFilter) {
		this.swFilter = swFilter;
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

}
