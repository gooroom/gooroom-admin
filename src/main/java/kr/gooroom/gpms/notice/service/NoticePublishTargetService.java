package kr.gooroom.gpms.notice.service;

import java.util.List;
import java.util.Map;

import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;

public interface NoticePublishTargetService {

    StatusVO createNoticePublishTarget(String noticePublishId, List<String> grpInfos, List<String> clientIds, List<String> deptInfos, List<String> userIds) throws Exception;
    
    ResultVO getNoticePublishTargetList(Map<String, Object> options) throws Exception;

    ResultPagingVO getNoticePublishTargetListPaged(Map<String, Object> options) throws Exception;

}
