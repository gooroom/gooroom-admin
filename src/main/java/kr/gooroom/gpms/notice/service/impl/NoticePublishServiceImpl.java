package kr.gooroom.gpms.notice.service.impl;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultPagingVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.LoginInfoHelper;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticePublishService;
import kr.gooroom.gpms.notice.service.NoticePublishVO;

@Service("noticePublishService")
public class NoticePublishServiceImpl implements NoticePublishService {

	private static final Logger logger = LoggerFactory.getLogger(NoticePublishServiceImpl.class);

	@Resource(name = "noticePublishDAO")
    private NoticePublishDAO noticePublishDAO;

	@Override
	public StatusVO createNoticePublish(NoticePublishVO noticePublishVO) {
	    StatusVO statusVO = new StatusVO();
	    try {
	        noticePublishVO.setModUserId(LoginInfoHelper.getUserId());
	        noticePublishVO.setRegUserId(LoginInfoHelper.getUserId());

	        long resultCnt = noticePublishDAO.createNoticePublish(noticePublishVO);
	        if (resultCnt > 0) {
	            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_INSERT,
                    MessageSourceHelper.getMessage("noticePublish.result.insert"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_INSERTERROR,
                    MessageSourceHelper.getMessage("noticePublish.result.noinsert"));
            }
	    } catch (Exception ex) {
            logger.error("error in createNoticePublish : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }
	    return statusVO;
	}

    @Override
    public StatusVO updateNoticePublish(NoticePublishVO noticePublishVO) {
        StatusVO statusVO = new StatusVO();
        try {
            NoticePublishVO existedNoticePublishVO = noticePublishDAO.selectNoticePublish(noticePublishVO.getNoticePublishId());
            existedNoticePublishVO.setOpenDt(noticePublishVO.getOpenDt());
            existedNoticePublishVO.setCloseDt(noticePublishVO.getCloseDt());
            existedNoticePublishVO.setViewType(noticePublishVO.getViewType());
            existedNoticePublishVO.setStatusCd(noticePublishVO.getStatusCd());
            existedNoticePublishVO.setModUserId(LoginInfoHelper.getUserId());
            
            // update notice publish
            long resultCnt = noticePublishDAO.updateNoticePublish(existedNoticePublishVO);
            
            if (resultCnt > 0) {
                statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                    MessageSourceHelper.getMessage("noticepublish.result.update"));
            } else {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
                    MessageSourceHelper.getMessage("noticepublish.result.noupdate"));
            }
        } catch (Exception ex) {
            logger.error("error in updateNoticePublish : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }
        
        return statusVO;
    }

    @Override
    public NoticePublishVO getNoticePublish(String noticePublishId) {
        return noticePublishDAO.selectNoticePublish(noticePublishId);
    }

	@Override
	public ResultPagingVO getNoticePublishList(Map<String, Object> options) {

		ResultPagingVO resultVO = new ResultPagingVO();
		
		try {
			List<NoticePublishVO> re = noticePublishDAO.selectNoticePublishList(options);
			long totalCount = noticePublishDAO.selectNoticePublishListTotalCount(options);
			long filteredCount = noticePublishDAO.selectNoticePublishListFilteredCount(options);

			if (re != null && re.size() > 0) {
				resultVO.setData(re.toArray(NoticePublishVO[]::new));
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
						MessageSourceHelper.getMessage("system.common.selectdata")));

				resultVO.setRecordsTotal(String.valueOf(totalCount));
				resultVO.setRecordsFiltered(String.valueOf(filteredCount));
			} else {
				resultVO.setData(new Object[0]);
				resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
						MessageSourceHelper.getMessage("system.common.noselectdata")));
			}
		} catch (Exception ex) {
			logger.error("error in getNoticePublishList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
		}
		
		return resultVO;
	}
}
