package kr.gooroom.gpms.notice.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticePublishVO;

@Repository("noticePublishDAO")
public class NoticePublishDAO extends SqlSessionMetaDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(NoticePublishDAO.class);

	/**
     * create notice_publish by noticePublish data bean.
     * 
     * @param noticePublishVO
     * @return long data insert result count.
	 */
    public long createNoticePublish(NoticePublishVO noticePublishVO) {
        return sqlSessionMeta.insert("insertNoticePublish", noticePublishVO);
    }
    /**
     * update notice_publish by noticePublish data bean.
     * 
     * @param noticePublishVO
     * @return long data update result count.
	 */
    public long updateNoticePublish(NoticePublishVO noticePublishVO) {
        return sqlSessionMeta.update("updateNoticePublish", noticePublishVO);
    }
    
	public NoticePublishVO selectNoticePublish(String noticePublishId) {
	    return sqlSessionMeta.selectOne("selectNoticePublish", noticePublishId);
	}

    /**
     * get notice_publish information list data
     * 
     * @param options Map<String, Object> options for select
     * @return NoticePublishVO List selected list data
	 */
	public List<NoticePublishVO> selectNoticePublishList(Map<String, Object> options) {
		List<NoticePublishVO> re = null;
		try {
		    re = sqlSessionMeta.selectList("selectNoticePublishList", options);
		} catch (Exception ex) {
		    logger.error("error in selectNoticePublishList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
			    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

    /**
     * response total count for notice_publish information list by options.
     * 
     * @param options Map<String, Object> options for select
     * @return long total count number.
	 */
	public long selectNoticePublishListTotalCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticePublishListTotalCount", options);
	}

    /**
     * response filtered total count for notice_publish information list by
     * options.
     * 
     * @param options Map<String, Object> options for select
     * @return long filtered count number.
	 */
	public long selectNoticePublishListFilteredCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticePublishListFilteredCount", options);
	}
}
