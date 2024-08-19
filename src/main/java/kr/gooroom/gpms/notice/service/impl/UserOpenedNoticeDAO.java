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
	 */
	public long createUserOpenedNotice(UserOpenedNoticeVO userOpenedNoticeVO) {
		return sqlSessionMeta.insert("insertUserOpenedNotice", userOpenedNoticeVO);
	}

	/**
	 * select user opened notice count
	 * 
	 * @return list count
	 */
	public long selectUserOpenedNoticeCount(String noticePublishId) {
		return sqlSessionMeta.selectOne("selectUserOpenedNoticeCount", noticePublishId);
	}
}
