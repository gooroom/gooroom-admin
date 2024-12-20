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

/**
 * Icon group data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class IconGroupVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 6400947575235400108L;
	private String grpId;
	private String grpNm;

	private String grpCmt;

	private String iconCnt;

	private String modUserId;
	private Date modDate;
	private String regUserId;
	private Date regDate;

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getGrpNm() {
		return grpNm;
	}

	public void setGrpNm(String grpNm) {
		this.grpNm = grpNm;
	}

	public String getGrpCmt() {
		return grpCmt;
	}

	public void setGrpCmt(String grpCmt) {
		this.grpCmt = grpCmt;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
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

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getIconCnt() {
		return iconCnt;
	}

	public void setIconCnt(String iconCnt) {
		this.iconCnt = iconCnt;
	}

}
