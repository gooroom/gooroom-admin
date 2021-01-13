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
     * @throws SQLException
     */
    public long createNoticePublish(NoticePublishVO noticePublishVO) throws SQLException {
        return (long) sqlSessionMeta.insert("insertNoticePublish", noticePublishVO);
    }
    /**
     * update notice_publish by noticePublish data bean.
     * 
     * @param noticePublishVO
     * @return long data update result count.
     * @throws SQLException
     */
    public long updateNoticePublish(NoticePublishVO noticePublishVO) throws SQLException {
        return (long) sqlSessionMeta.update("updateNoticePublish", noticePublishVO);
    }
    
	public NoticePublishVO selectNoticePublish(String noticePublishId) throws SQLException {
	    return sqlSessionMeta.selectOne("selectNoticePublish", noticePublishId);
	}

    /**
     * get notice_publish information list data
     * 
     * @param options Map<String, Object> options for select
     * @return NoticePublishVO List selected list data
     * @throws SQLException
     */
	public List<NoticePublishVO> selectNoticePublishList(Map<String, Object> options) throws SQLException {
		List<NoticePublishVO> re = null;
		try {
		    re = sqlSessionMeta.selectList("selectNoticePublishList", options);
		} catch (Exception ex) {
		    logger.error("error in selectNoticePublishList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
			    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		    re = null;
		}

		return re;
	}

    /**
     * response total count for notice_publish information list by options.
     * 
     * @param options Map<String, Object> options for select
     * @return long total count number.
     * @throws SQLException
     */
	public long selectNoticePublishListTotalCount(Map<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectNoticePublishListTotalCount", options);
	}

    /**
     * response filtered total count for notice_publish information list by
     * options.
     * 
     * @param options Map<String, Object> options for select
     * @return long filtered count number.
     * @throws SQLException
     */
	public long selectNoticePublishListFilteredCount(Map<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectNoticePublishListFilteredCount", options);
	}
}
