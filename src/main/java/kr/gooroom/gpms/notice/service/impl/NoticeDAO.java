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
import kr.gooroom.gpms.notice.service.NoticeVO;
import kr.gooroom.gpms.notice.service.TargetNoticeVO;

@Repository("noticeDAO")
public class NoticeDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(NoticeDAO.class);

	/**
	 * create notice master by notice data bean.
	 * 
	 * @param noticeVO
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createNoticeMaster(NoticeVO noticeVO) throws SQLException {
		return (long) sqlSessionMeta.insert("insertNoticeMaster", noticeVO);
	}

	/**
	 * update notice master by notice data bean.
	 * 
	 * @param noticeVO
	 * @return long data update result count.
	 * @throws SQLException
	 */
	public long updateNoticeMaster(NoticeVO noticeVO) throws SQLException {
		return (long) sqlSessionMeta.update("updateNoticeMaster", noticeVO);
	}

	/**
	 * delete notice master
	 * 
	 * @param noticeVO
	 * @return long data delete result count.
	 * @throws SQLException
	 */

	public long deleteNoticeMaster(NoticeVO noticeVO) throws SQLException {
		return (long) sqlSessionMeta.insert("deleteNoticeMaster", noticeVO);
	}

	/**
	 * get notice information list data
	 * 
	 * @param options Map<String, Object> options for select
	 * @return NoticeVO List selected list data
	 * @throws SQLException
	 */
	public List<NoticeVO> selectNoticeList(Map<String, Object> options) throws SQLException {
		List<NoticeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoticeList", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	/**
	 * response total count for notice information list by options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long total count number.
	 * @throws SQLException
	 */
	public long selectNoticeListTotalCount(Map<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectNoticeListTotalCount", options);
	}

	/**
	 * response filtered total count for notice information list by options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long filtered count number.
	 * @throws SQLException
	 */
	public long selectNoticeListFilteredCount(Map<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectNoticeListFilteredCount", options);
	}

	public List<TargetNoticeVO> findAllByTarget(Map<String, Object> options) throws SQLException {
		List<TargetNoticeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoticesByTarget", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticesByTarget : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}

		return re;
	}

	public long getTotalCountByTarget(Map<String, Object> options) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectTotalCountByTarget", options);
	}

	public TargetNoticeVO findOneByTarget(Map<String, Object> options) throws SQLException {
		TargetNoticeVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectNoticeByTarget", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticeByTarget : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
			re = null;
		}
		return re;
	}
}
