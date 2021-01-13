package kr.gooroom.gpms.notice.service;

import java.util.Map;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface NoticePublishService {

    StatusVO createNoticePublish(NoticePublishVO noticePublishVO) throws Exception;

    StatusVO updateNoticePublish(NoticePublishVO noticePublishVO) throws Exception;
    
    NoticePublishVO getNoticePublish(String noticePublishId) throws Exception;

	ResultPagingVO getNoticePublishList(Map<String, Object> options) throws Exception;
}
