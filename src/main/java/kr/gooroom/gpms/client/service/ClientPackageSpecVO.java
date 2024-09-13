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

package kr.gooroom.gpms.client.service;

import java.io.Serializable;
import java.util.Date;

/**
 * client package data bean
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ClientPackageSpecVO implements Serializable {

	// view
	private String nullColumn = "";

	private String clientId;
	private String packageId;
	private String packageArch;
	private String installVer;
	private String spec;
	private String license;
	private String supplier;
	private String integrity;
	private String isoVer;
	private String v1Ver;
	private String v2Ver;
	private String v1License;
	private String v2License;

	private Date modDate;


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Date getModDate() {
		return modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getPackageArch() {
		return packageArch;
	}

	public void setPackageArch(String packageArch) {
		this.packageArch = packageArch;
	}

	public String getInstallVer() {
		return installVer;
	}

	public void setInstallVer(String installVer) {
		this.installVer = installVer;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getV2License() {
		return v2License;
	}

	public void setV2License(String v2License) {
		this.v2License = v2License;
	}

	public String getV1License() {
		return v1License;
	}

	public void setV1License(String v1License) {
		this.v1License = v1License;
	}

	public String getV2Ver() {
		return v2Ver;
	}

	public void setV2Ver(String v2Ver) {
		this.v2Ver = v2Ver;
	}

	public String getV1Ver() {
		return v1Ver;
	}

	public void setV1Ver(String v1Ver) {
		this.v1Ver = v1Ver;
	}

	public String getIsoVer() {
		return isoVer;
	}

	public void setIsoVer(String isoVer) {
		this.isoVer = isoVer;
	}

	public String getIntegrity() {
		return integrity;
	}

	public void setIntegrity(String integrity) {
		this.integrity = integrity;
	}
}
