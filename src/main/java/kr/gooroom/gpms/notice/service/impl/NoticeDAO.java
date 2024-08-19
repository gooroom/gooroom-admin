package kr.gooroom.gpms.notice.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.notice.service.NoticeVO;
import kr.gooroom.gpms.notice.service.TargetNoticeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("noticeDAO")
public class NoticeDAO extends SqlSessionMetaDAO {

	private static final Logger logger = LoggerFactory.getLogger(NoticeDAO.class);

	/**
	 * create notice master by notice data bean.
	 * 
	 * @param noticeVO
	 * @return long data insert result count.
	 */
	public long createNoticeMaster(NoticeVO noticeVO) {
		return sqlSessionMeta.insert("insertNoticeMaster", noticeVO);
	}

	/**
	 * update notice master by notice data bean.
	 * 
	 * @param noticeVO
	 * @return long data update result count.
	 */
	public long updateNoticeMaster(NoticeVO noticeVO) {
		return sqlSessionMeta.update("updateNoticeMaster", noticeVO);
	}

	/**
	 * delete notice master
	 * 
	 * @param noticeVO
	 * @return long data delete result count.
	 */

	public long deleteNoticeMaster(NoticeVO noticeVO) {
		return sqlSessionMeta.insert("deleteNoticeMaster", noticeVO);
	}

	/**
	 * get notice information list data
	 * 
	 * @param options Map<String, Object> options for select
	 * @return NoticeVO List selected list data
	 */
	public List<NoticeVO> selectNoticeList(Map<String, Object> options) {
		List<NoticeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoticeList", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticeList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	/**
	 * response total count for notice information list by options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long total count number.
	 */
	public long selectNoticeListTotalCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticeListTotalCount", options);
	}

	/**
	 * response filtered total count for notice information list by options.
	 * 
	 * @param options Map<String, Object> options for select
	 * @return long filtered count number.
	 */
	public long selectNoticeListFilteredCount(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectNoticeListFilteredCount", options);
	}

	public List<TargetNoticeVO> findAllByTarget(Map<String, Object> options) {
		List<TargetNoticeVO> re = null;
		try {
			re = sqlSessionMeta.selectList("selectNoticesByTarget", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticesByTarget : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}

		return re;
	}

	public long getTotalCountByTarget(Map<String, Object> options) {
		return sqlSessionMeta.selectOne("selectTotalCountByTarget", options);
	}

	public TargetNoticeVO findOneByTarget(Map<String, Object> options) {
		TargetNoticeVO re = null;
		try {
			re = sqlSessionMeta.selectOne("selectNoticeByTarget", options);
		} catch (Exception ex) {
			logger.error("error in selectNoticeByTarget : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
					MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
		}
		return re;
	}
}
