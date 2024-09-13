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

package kr.gooroom.gpms.account.service;

import java.io.Serializable;
import java.util.Date;

/**
 * history data bean from admin doing.
 * 
 * @author HNC
 * @version 1.0
 * @since 1.8
 */

@SuppressWarnings("serial")
public class ActHistoryVO implements Serializable {

	String logSeq;
	String actTp;
	String actItem;
	String actData;
	String accessIp;
	String actUserId;
	String actTarget;
	String isSuccess;
	Date actDt;

    public String getLogSeq() {
      return this.logSeq;
    }
    public void setLogSeq(String value) {
      this.logSeq = value;
    }

    public String getActTp() {
      return this.actTp;
    }
    public void setActTp(String value) {
      this.actTp = value;
    }

    public String getActItem() {
      return this.actItem;
    }
    public void setActItem(String value) {
      this.actItem = value;
    }

    public String getActData() {
      return this.actData;
    }
    public void setActData(String value) {
      this.actData = value;
    }

    public String getAccessIp() {
      return this.accessIp;
    }
    public void setAccessIp(String value) {
      this.accessIp = value;
    }

    public String getActUserId() {
      return this.actUserId;
    }
    public void setActUserId(String value) {
      this.actUserId = value;
    }

    public String getActTarget() {
      return this.actTarget;
    }
    public void setActTarget(String value) {
      this.actTarget = value;
    }

    public String getIsSuccess() {
      return this.isSuccess;
    }
    public void setIsSuccess(String value) {
      this.isSuccess = value;
    }

    public Date getActDt() {
      return this.actDt;
    }
    public void setActDt(Date value) {
      this.actDt = value;
    }
}
