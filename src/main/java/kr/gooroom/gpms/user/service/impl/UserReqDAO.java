package kr.gooroom.gpms.user.service.impl;

import kr.gooroom.gpms.account.service.ActHistoryVO;
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
     * 사용자 USB 등록/삭제 요청 리스트
     *
     * @return UserReqVO List
     * @throws SQLException
     */
    public List<UserReqVO> selectUserReqList(String userId) throws SQLException {
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
     * @throws SQLException
     */
    public long selectUserReqActListTotalCount(HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.selectOne("selectUserReqActListTotalCount", options);
    }

    /**
     * response filtered count for administrator user action logging list data.
     *
     * @param options HashMap<String, Object> options for select
     * @return long filtered count number.
     * @throws SQLException
     */
    public long selectUserReqActListFilteredCount(HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.selectOne("selectUserReqActListFilteredCount", options);
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
        return (long) sqlSessionMeta.update("updateUserReq", vo);
    }

    /**
     * 사용자 USB 추가/삭제에 대한 상태 업데이트
     *
     * @param vo MgServerConfVO gooroom server configuration data bean
     * @return long data update result count.
     * @throws SQLException
     */
    public long updateUserReqStatus(UserReqVO vo) throws SQLException {
        return (long) sqlSessionMeta.update("updateUserReqStatus", vo);
    }


    /**
     * 사용자의 요청 데이타
     * @param reqSeq
     * @return
     * @throws Exception
     */
    public List<UserReqVO> selectUserReqData(String reqSeq) throws Exception {
        return sqlSessionMeta.selectList("selectUserReqData", reqSeq);
    }

    /**
     * 관리자에 의한 usb 권한 회수 (사용자 요청 리스트로 추가됨)
     *
     * @param vo
     * @return int
     * @throws Exception
     */
    public int insertUserReqMstr(UserReqVO vo) throws Exception {
        return sqlSessionMeta.insert("insertUserReqMstr", vo);
    }

    /**
     * 매체 정보 저장
     *
     * @param vo
     * @return int
     * @throws Exception
     */
    public int insertUserReqProp(UserReqVO vo) throws Exception {
        return sqlSessionMeta.insert("insertUserReqProp", vo);
    }

    /**
     * 요청의 reqSeq 검색
     * @param urmVo
     * @return
     * @throws Exception
     */
    public String selectUserReqSeq(UserReqVO urmVo) throws Exception {
        return sqlSessionMeta.selectOne("selectUserReqSeq", urmVo);
    }

    /**
     * insert user req history
     *
     * @return long query result count
     * @throws SQLException
     */
    public long createUserReqHist(UserReqVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertUserReqHist", vo);
    }
}
