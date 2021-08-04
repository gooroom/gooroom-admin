package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository("portableDAO")
public class PortableDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PortableDAO.class);

    public long createPortableData(PortableVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableData", vo);
    }

    public List<PortableVO> selectPortableDataList() throws SQLException {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataList");
        return res;
    }

    public List<PortableVO> selectPortableDataList(String id) throws SQLException {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataList", id);
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

    public long deletePortableData (String ptgrId) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableData", ptgrId);
    }

    public long deleteAllPortableData () throws SQLException {
        return (long) sqlSessionMeta.delete("deleteAllPortableData");
    }
}
