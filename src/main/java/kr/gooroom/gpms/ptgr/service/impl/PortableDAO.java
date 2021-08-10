package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import kr.gooroom.gpms.ptgr.service.PortableViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository("portableDAO")
public class PortableDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PortableDAO.class);


    public List<String> checkCertID (HashMap<String, Object> options) throws SQLException {
        List<String> res = null;
        res = sqlSessionMeta.selectList("checkId", options);
        return res;
    }

    public long createPortableData(PortableVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableData", vo);
    }

    public List<PortableViewVO> selectPortableViewList() throws SQLException {
        List<PortableViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableViewList");
        return res;
    }

    public List<PortableViewVO> selectPortableViewList(HashMap<String, Object> options) throws SQLException {
        List<PortableViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableViewList", options);
        return res;
    }

    public List<PortableVO> selectPortableDataList(HashMap<String, Object> options) throws SQLException {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataList", options);
        return res;
    }

    public List<PortableVO> selectPortableDataListByAdminIdAndApprove(HashMap<String, Object> options) throws SQLException {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataListByAdminIdAndApprove", options);
        return res;
    }

    public List<PortableVO> selectPortableDataListPaged(HashMap<String, Object> options) throws SQLException {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataListPaged", options);
        return res;
    }

    public long updatePortableData(PortableVO vo) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableData", vo);
    }

    public long updateAllPortableApprove(String adminId) throws SQLException {
        return (long) sqlSessionMeta.update("updateAllPortableApprove", adminId);
    }

    public long deletePortableData (HashMap<String, Object> ids) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableData", ids);
    }

    public long deleteAllPortableData () throws SQLException {
        return (long) sqlSessionMeta.delete("deleteAllPortableData");
    }

    public int selectNextPortableNumber() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextPortableNo" );
    }

    public int selectPortableCount() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextPortableCount" );
    }
}
