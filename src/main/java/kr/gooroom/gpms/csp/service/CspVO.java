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

package kr.gooroom.gpms.csp.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * gooroom client service configuration data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

public class CspVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1672510711895192668L;
	private String gcspId;
	private String gcspNm;
	private String statusCd;

	private String expirationYmd;

	private String ipRanges;
	private String url;
	private String comment;

	private String modUserId;
	private Date modDt;
	private String regUserId;
	private Date regDt;

	private String cert;
	private String priv;
	private String serialNo;

	private String certGubun;
	private String gcspCsr;

	public String getCertGubun() {
		return certGubun;
	}

	public void setCertGubun(String certGubun) {
		this.certGubun = certGubun;
	}

	public String getGcspCsr() {
		return gcspCsr;
	}

	public void setGcspCsr(String gcspCsr) {
		this.gcspCsr = gcspCsr;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getGcspId() {
		return gcspId;
	}

	public void setGcspId(String gcspId) {
		this.gcspId = gcspId;
	}

	public String getGcspNm() {
		return gcspNm;
	}

	public void setGcspNm(String gcspNm) {
		this.gcspNm = gcspNm;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public String getExpirationYmd() {
		return expirationYmd;
	}

	public void setExpirationYmd(String expirationYmd) {
		this.expirationYmd = expirationYmd;
	}

	public String getIpRanges() {
		return ipRanges;
	}

	public void setIpRanges(String ipRanges) {
		this.ipRanges = ipRanges;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getModUserId() {
		return modUserId;
	}

	public void setModUserId(String modUserId) {
		this.modUserId = modUserId;
	}

	public Date getModDt() {
		return modDt;
	}

	public void setModDt(Date modDt) {
		this.modDt = modDt;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public Date getRegDt() {
		return regDt;
	}

	public void setRegDt(Date regDt) {
		this.regDt = regDt;
	}

}
