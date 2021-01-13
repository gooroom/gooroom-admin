package kr.gooroom.gpms.notice.service;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetNoticeVO implements Serializable {

	private String noticeId;

	private String title;

	private String content;

	private String regUserId;

	private Date openDt;

	private String noticePublishId;

	private Date openedDt;

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public void setRegUserId(String regUserId) {
		this.regUserId = regUserId;
	}

	public Date getOpenDt() {
		return openDt;
	}

	public void setOpenDt(Date openDt) {
		this.openDt = openDt;
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
