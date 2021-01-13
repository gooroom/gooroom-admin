package kr.gooroom.gpms.notice.service.impl;

import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.notice.service.UserOpenedNoticeVO;

@Repository("userOpenedNoticeDAO")
public class UserOpenedNoticeDAO extends SqlSessionMetaDAO {

	/**
	 * create user opened notice.
	 * 
	 * @param userOpenedNoticeVO
	 * @return long data insert result count.
	 * @throws SQLException
	 */
	public long createUserOpenedNotice(UserOpenedNoticeVO userOpenedNoticeVO) throws SQLException {
		return (long) sqlSessionMeta.insert("insertUserOpenedNotice", userOpenedNoticeVO);
	}

	/**
	 * select user opened notice count
	 * 
	 * @return list count
	 * @throws SQLException
	 */
	public long selectUserOpenedNoticeCount(String noticePublishId) throws SQLException {
		return (long) sqlSessionMeta.selectOne("selectUserOpenedNoticeCount", noticePublishId);
	}
}
