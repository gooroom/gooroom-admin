package kr.gooroom.gpms.notice.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface NoticeService {

	StatusVO createNoticeData(NoticeVO noticeVO) throws Exception;

	StatusVO updateNoticeData(NoticeVO noticeVO) throws Exception;

	StatusVO deleteNoticeMaster(String noticeId) throws Exception;

	ResultPagingVO getNoticeList(Map<String, Object> options) throws Exception;

	Page<TargetNoticeVO> getNoticesByTarget(Pageable pageable, String userId, String clientId) throws Exception;

	Optional<TargetNoticeVO> getNoticeByTarget(String userId, String clientId, String noticePublishId) throws Exception;
}
