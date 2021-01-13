package kr.gooroom.gpms.notice.controller.model.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import kr.gooroom.gpms.notice.service.TargetNoticeVO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetNoticeResponseModel {

	private final String noticeId;

	private final String title;

	private final String content;

	private final String regUserId;

	private final Date openDt;

	private final String noticePublishId;

	private final Date openedDt;

	public TargetNoticeResponseModel(TargetNoticeVO targetNoticeVO, boolean showContent) {
		this.noticeId = targetNoticeVO.getNoticeId();
		this.title = targetNoticeVO.getTitle();
		this.content = showContent ? targetNoticeVO.getContent() : null;
		this.regUserId = targetNoticeVO.getRegUserId();
		this.openDt = targetNoticeVO.getOpenDt();
		this.noticePublishId = targetNoticeVO.getNoticePublishId();
		this.openedDt = targetNoticeVO.getOpenedDt();
	}

	public String getNoticeId() {
		return noticeId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getRegUserId() {
		return regUserId;
	}

	public Date getOpenDt() {
		return openDt;
	}

	public String getNoticePublishId() {
		return noticePublishId;
	}

	public Date getOpenedDt() {
		return openedDt;
	}
}
