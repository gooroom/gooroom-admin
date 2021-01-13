package kr.gooroom.gpms.notice.service;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticePublishTargetVO implements Serializable {

	private String noticePublishId;
	
	private String targetType;
	
	private String targetId;
	
	private String targetName;
	
	private String isChildCheck;
	
	private String isShow;

	public String getNoticePublishId() {
		return noticePublishId;
	}

	public void setNoticePublishId(String noticePublishId) {
		this.noticePublishId = noticePublishId;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

    public String getIsChildCheck() {
        return isChildCheck;
    }

    public void setIsChildCheck(String isChildCheck) {
        this.isChildCheck = isChildCheck;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
