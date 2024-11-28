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
import java.util.ArrayList;
import java.util.Date;

/**
 * configuration item data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class CtrlItemVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 2767436962633219272L;
	private String objId;
	private String newObjId;

	private String objNm;
	private String mngObjTp;
	private String mngObjTpAbbr;
	private boolean standardObj = false;

	private String comment;
	private String extValue;

	private Date modDate;
	private String modUserId;

	private Date regDate;
	private String regUserId;

	private ArrayList<CtrlPropVO> propList;

	public CtrlItemVO() {
		propList = new ArrayList<CtrlPropVO>();
	}

	public ArrayList<CtrlPropVO> getPropList() {
		return propList;
	}

	public void setPropList(ArrayList<CtrlPropVO> propList) {
		this.propList = propList;
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

	public String getObjNm() {
		return objNm;
	}

	public void setObjNm(String objNm) {
		this.objNm = objNm;
	}

	public String getMngObjTp() {
		return mngObjTp;
	}

	public void setMngObjTp(String mngObjTp) {
		this.mngObjTp = mngObjTp;
	}

	public String getMngObjTpAbbr() {
		return mngObjTpAbbr;
	}

	public void setMngObjTpAbbr(String mngObjTpAbbr) {
		this.mngObjTpAbbr = mngObjTpAbbr;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExtValue() {
		return extValue;
	}

	public void setExtValue(String extValue) {
		this.extValue = extValue;
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

	public boolean isStandardObj() {
		return standardObj;
	}

	public void setStandardObj(boolean standardObj) {
		this.standardObj = standardObj;
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
