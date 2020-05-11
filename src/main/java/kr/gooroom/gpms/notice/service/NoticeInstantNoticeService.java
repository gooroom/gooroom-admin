package kr.gooroom.gpms.notice.service;

import java.util.Map;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface NoticeInstantNoticeService {

	StatusVO createNoticeInstantNotice(NoticeInstantNoticeVO noticeInstantNoticeVO) throws Exception;

	ResultPagingVO getNoticeInstantNoticeList(Map<String, Object> options) throws Exception;
}