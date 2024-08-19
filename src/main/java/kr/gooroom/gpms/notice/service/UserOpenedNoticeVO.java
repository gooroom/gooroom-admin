package kr.gooroom.gpms.notice.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOpenedNoticeVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 8227533203538389031L;
	private String userId;

	private String noticePublishId;

	private Date openedDt;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNoticePublishId() {
		return noticePublishId;
	}

	public void setNoticePublishId(String noticePublishId) {
		this.noticePublishId = noticePublishId;
	}

	public Date getOpenedDt() {
		return openedDt;
	}

	public void setOpenedDt(Date openedDt) {
		this.openedDt = openedDt;
	}
}
