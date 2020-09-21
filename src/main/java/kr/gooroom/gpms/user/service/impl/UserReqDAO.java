package kr.gooroom.gpms.user.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.config.service.SiteConfVO;
import kr.gooroom.gpms.user.service.UserReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository("userReqDAO")
public class UserReqDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserDAO.class);

    /**
     * 사용자 USB 등록/삭제 요청 검색
     *
     * @return UserReqVO List
     * @throws SQLException
     */
    public List<UserReqVO> selectUserReqListPaged(HashMap<String, Object> options) throws SQLException {
        List<UserReqVO> re = null;
        try {
            re = sqlSessionMeta.selectList("selectUserReqListPaged", options);
        } catch (Exception ex) {
            logger.error("error in selectUserReqListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }
        return re;
    }

    /**
     * 사용자  USB 등록/삭제 요청 수
     *
     * @param options HashMap<String, Object> options for select
     * @return long total count number.
     * @throws SQLException
     */
    public long selectUserReqListTotalCount(HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.selectOne("selectUserReqListTotalCount", options);
    }

    /**
     * 사용자  USB 동륵/삭제 필터 적용한 요청 수
     *
     * @param options HashMap<String, Object> options for select
     * @return long filtered count number.
     * @throws SQLException
     */
    public long selectUserReqListFilteredCount(HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.selectOne("selectUserReqListFilteredCount", options);
    }

    /**
     * reqSeq로 해당 요청 검색
     *
     * @return UserReqVO List
     * @throws SQLException
     */
    public List<UserReqVO> selectUserReq(String reqSeq) throws SQLException {
        List<UserReqVO> re = null;
        try {
            re = sqlSessionMeta.selectList("selectUserReq", reqSeq);
        } catch (Exception ex) {
            logger.error("error in selectUserReq : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }
        return re;
    }

    /**
     * 사용자 USB 추가/삭제에 대한 승인 정보 업데이트
     *
     * @param vo MgServerConfVO gooroom server configuration data bean
     * @return long data update result count.
     * @throws SQLException
     */
    public long updateUserReq(UserReqVO vo) throws SQLException {
        return (long) sqlSessionMeta.update("updateUserReq", vo);
    }

}
