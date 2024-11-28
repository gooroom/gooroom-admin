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
import kr.gooroom.gpms.notice.service.NoticeInstantNoticeVO;

@Repository("noticeInstantNoticeDAO")
public class NoticeInstantNoticeDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(NoticeInstantNoticeDAO.class);

	/**
	 * create notice instant notice.
	 * 
	 * @param noticeInstantNoticeVO NoticeinstantNoticeVO
	 * @return long data insert result count.
	 */
	public long createNoticeInstantNotice(NoticeInstantNoticeVO noticeInstantNoticeVO) {
		return sqlSessionMeta.insert("insertNoticeInstantNotice", noticeInstantNoticeVO);
	}

	/**
	 * get notice instant notice list data
	 * 
	 * @param options
	 * @return NoticePublishTargetVO List selected list data
	 */
	public List<NoticeInstantNoticeVO> selectNoticeInstantNoticeList(Map<String, Object> options) {
		List<NoticeInstantNoticeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoticeInstantNoticeList", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticeInstantNoticeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response total count for notice_instant_notice information list by options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectNoticeInstantNoticeListTotalCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticeInstantNoticeListTotalCount", options);
	}

	/**
	 * response filtered total count for notice_instant_notice information list by
	 * options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectNoticeInstantNoticeListFilteredCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticeInstantNoticeListFilteredCount", options);
	}
}