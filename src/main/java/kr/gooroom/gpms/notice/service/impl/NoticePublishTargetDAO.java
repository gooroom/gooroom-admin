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
import kr.gooroom.gpms.notice.service.NoticePublishTargetVO;

@Repository("noticePublishTargetDAO")
public class NoticePublishTargetDAO extends SqlSessionMetaDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(NoticePublishTargetDAO.class);

	/**
     * create notice_publish_target by noticePublishTarget list data bean.
     * 
     * @param noticePublishTargetVOs
     * @return long data insert result count.
     * @throws SQLException
     */
	public long createNoticePublishTargets(List<NoticePublishTargetVO> noticePublishTargetVOs) throws SQLException {
        return (long) sqlSessionMeta.insert("insertNoticePublishTargets", noticePublishTargetVOs);
    }

    /**
     * get notice publish target list data
     * 
     * @param options
     * @return NoticePublishTargetVO List selected list data
     * @throws SQLException
     */
    public List<NoticePublishTargetVO> selectNoticePublishTargetList(Map<String, Object> options) throws SQLException {
        List<NoticePublishTargetVO> re = null;
        try {
            re = sqlSessionMeta.selectList("selectNoticePublishTargetList", options);
        } catch (Exception ex) {
            logger.error("error in selectNoticePublishTargetList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }

        return re;
    }
	
    /**
     * get notice publish target list data
     * 
     * @param options
     * @return NoticePublishTargetVO List selected list data
     * @throws SQLException
     */
	public List<NoticePublishTargetVO> selectNoticePublishTargetListPaged(Map<String, Object> options) throws SQLException {
		List<NoticePublishTargetVO> re = null;
		try {
		    re = sqlSessionMeta.selectList("selectNoticePublishTargetListPaged", options);
		} catch (Exception ex) {
		    logger.error("error in selectNoticePublishTargetListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
			    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		    re = null;
		}

		return re;
	}

    /**
     * response total count for notice_publish_target information list by options.
     * 
     * @param options Map<String, Object> options for select
     * @return long total count number.
     * @throws SQLException
     */
    public long selectNoticePublishTargetListTotalCount(Map<String, Object> options) {
        return (long) sqlSessionMeta.selectOne("selectNoticePublishTargetListTotalCount", options);
    }

    /**
     * response filtered total count for notice_publish_target information list by
     * options.
     * 
     * @param options Map<String, Object> options for select
     * @return long filtered count number.
     * @throws SQLException
     */
    public long selectNoticePublishTargetListFilteredCount(Map<String, Object> options) {
        return (long) sqlSessionMeta.selectOne("selectNoticePublishTargetListFilteredCount", options);
    }

}
