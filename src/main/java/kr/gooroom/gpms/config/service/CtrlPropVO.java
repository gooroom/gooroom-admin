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

import java.io.Serializable;
import java.util.Date;

/**
 * configuration property data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class CtrlPropVO implements Serializable {

	private String objId;
	private String newObjId;
	private String mngObjTp;

	private String propId;
	private String propNm;
	private String propValue;

	private String link;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	public CtrlPropVO() {

	}

	public CtrlPropVO(String objId, String propId, String propNm, String propValue, String link, String modUserId) {
		this.setObjId(objId);
		this.setPropId(propId);
		this.setPropNm(propNm);
		this.setPropValue(propValue);
		this.setLink(link);
		this.setModUserId(modUserId);
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getNewObjId() {
		return newObjId;
	}

	public void setNewObjId(String newObjId) {
		this.newObjId = newObjId;
	}

	public String getMngObjTp() {
		return mngObjTp;
	}

	public void setMngObjTp(String mngObjTp) {
		this.mngObjTp = mngObjTp;
	}

	public String getPropId() {
		return propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
	}

	public String getPropNm() {
		return propNm;
	}

	public void setPropNm(String propNm) {
		this.propNm = propNm;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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
