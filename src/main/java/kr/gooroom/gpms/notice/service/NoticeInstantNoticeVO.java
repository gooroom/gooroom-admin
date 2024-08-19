package kr.gooroom.gpms.notice.service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeInstantNoticeVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 8927359380303313236L;
	private String noticeInstantNoticeId;

	private String noticePublishId;

	private String regUserId;

	private Date regDt;

	public String getNoticeInstantNoticeId() {
		return noticeInstantNoticeId;
	}

	public void setNoticeInstantNoticeId(String noticeInstantNoticeId) {
		this.noticeInstantNoticeId = noticeInstantNoticeId;
	}

	public String getNoticePublishId() {
		return noticePublishId;
	}

	public void setNoticePublishId(String noticePublishId) {
		this.noticePublishId = noticePublishId;
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
