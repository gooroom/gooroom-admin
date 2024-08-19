package kr.gooroom.gpms.user.service.impl;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
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
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @return UserReqVO List
     */
    public List<UserReqVO> selectUserReqList(String userId) {
        List<UserReqVO> re = null;
        try {
            re = sqlSessionMeta.selectList("selectUserReqList", userId);
        } catch (Exception ex) {
            logger.error("error in selectUserReqList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }
        return re;
    }

    /**
     * 사용자 USB 등록/삭제 요청 검색
     *
     * @return UserReqVO List
     */
    public List<UserReqVO> selectUserReqListPaged(HashMap<String, Object> options) {
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
     */
    public long selectUserReqListTotalCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectUserReqListTotalCount", options);
    }

    /**
     * 사용자  USB 동륵/삭제 필터 적용한 요청 수
     *
     * @param options HashMap<String, Object> options for select
     * @return long filtered count number.
     */
    public long selectUserReqListFilteredCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectUserReqListFilteredCount", options);
    }

    /**
     * response administrator user action logging list paged
     *
     * @param options HashMap<String, Object> options for select
     * @return ActHistoryVO List
     * @throws SQLException
     */
    public List<UserReqVO> selectUserReqActListPaged(HashMap<String, Object> options) {
        List<UserReqVO> re = null;
        try {
            re = sqlSessionMeta.selectList("selectUserReqActListPaged", options);
        } catch (Exception ex) {
            logger.error("error in selectUserReqActListPaged : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }
        return re;
    }

    /**
     * response total count for administrator user action logging list data.
     *
     * @param options HashMap<String, Object> options for select
     * @return long total count number.
     */
    public long selectUserReqActListTotalCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectUserReqActListTotalCount", options);
    }

    /**
     * response filtered count for administrator user action logging list data.
     *
     * @param options HashMap<String, Object> options for select
     * @return long filtered count number.
     */
    public long selectUserReqActListFilteredCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectUserReqActListFilteredCount", options);
    }

    /**
     * reqSeq로 해당 요청 검색
     *
     * @return UserReqVO List
     * @throws SQLException
     */
    public UserReqVO selectUserReq(String reqSeq) throws SQLException {
        UserReqVO re = null;
        try {
            re = sqlSessionMeta.selectOne("selectUserReq", reqSeq);
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
        return sqlSessionMeta.update("updateUserReq", vo);
    }

    /**
     * 사용자 USB 추가/삭제에 대한 상태 업데이트
     *
     * @param vo MgServerConfVO gooroom server configuration data bean
     * @return long data update result count.
     * @throws SQLException
     */
    public long updateUserReqStatus(UserReqVO vo) throws SQLException {
        return sqlSessionMeta.update("updateUserReqStatus", vo);
    }


    /**
     * 사용자의 요청 데이타
     * @param reqSeq
     * @return
     */
    public List<UserReqVO> selectUserReqData(String reqSeq) {
        return sqlSessionMeta.selectList("selectUserReqData", reqSeq);
    }

    /**
     * 관리자에 의한 usb 권한 회수 (사용자 요청 리스트로 추가됨)
     *
     * @param vo
     * @return int
     */
    public int insertUserReqMstr(UserReqVO vo) {
        return sqlSessionMeta.insert("insertUserReqMstr", vo);
    }

    /**
     * 매체 정보 저장
     *
     * @param vo
     * @return int
     */
    public int insertUserReqProp(UserReqVO vo) {
        return sqlSessionMeta.insert("insertUserReqProp", vo);
    }

    /**
     * 요청의 reqSeq 검색
     * @param urmVo
     * @return
     */
    public String selectUserReqSeq(UserReqVO urmVo) {
        return sqlSessionMeta.selectOne("selectUserReqSeq", urmVo);
    }

    /**
     * insert user req history
     *
     * @return long query result count
     * @throws SQLException
     */
    public long createUserReqHist(UserReqVO vo) throws SQLException {
        return sqlSessionMeta.insert("insertUserReqHist", vo);
    }

    /**
     * 같은 시리얼의 매체 요청중 반려 이력이 있었는지 검색
     *
     * @return UserReqVO List
     * @throws SQLException
     */
    public UserReqVO selectDenyReqList(UserReqVO vo) throws SQLException {
        UserReqVO re = null;
        try {
            re = sqlSessionMeta.selectOne("selectDenyReqList", vo);
        } catch (Exception ex) {
            logger.error("error in selectDenyReqList : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            re = null;
        }
        return re;
    }
}
