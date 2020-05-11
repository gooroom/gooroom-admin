package kr.gooroom.gpms.notice.service;

import kr.gooroom.gpms.common.service.StatusVO;

public interface UserOpenedNoticeService {

	StatusVO createUserOpenedNotice(String userId, String noticePublishId) throws Exception;
}