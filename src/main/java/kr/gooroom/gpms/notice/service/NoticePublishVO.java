package kr.gooroom.gpms.notice.service;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticePublishVO implements Serializable {

	private String noticePublishId;

	private String noticeId;

	private String statusCd;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date openDt;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date closeDt;

	private String viewType;

	private String regUserId;

	private Date regDt;

	private String modUserId;

	private Date modDt;

	private Long openedUserCnt;

	private Long instantNoticeCnt;

	public String getNoticePublishId() {
		return noticePublishId;
	}

	public void setNoticePublishId(String noticePublishId) {
		this.noticePublishId = noticePublishId;
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public Date getOpenDt() {
		return openDt;
	}

	public void setOpenDt(Date openDt) {
		this.openDt = openDt;
	}

	public Date getCloseDt() {
		return closeDt;
	}

	public void setCloseDt(Date closeDt) {
		this.closeDt = closeDt;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
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

	public Long getOpenedUserCnt() {
		return openedUserCnt;
	}

	public void setOpenedUserCnt(Long openedUserCnt) {
		this.openedUserCnt = openedUserCnt;
	}

	public Long getInstantNoticeCnt() {
		return instantNoticeCnt;
	}

	public void setInstantNoticeCnt(Long instantNoticeCnt) {
		this.instantNoticeCnt = instantNoticeCnt;
	}
}
